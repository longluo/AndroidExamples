package pfg.com.screenproc;

import android.app.Activity;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Choreographer;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.lang.ref.WeakReference;

import pfg.com.screenproc.egl.EGLCore;
import pfg.com.screenproc.egl.FullFrameRect;
import pfg.com.screenproc.egl.Texture2dProgram;
import pfg.com.screenproc.egl.WindowSurface;
import pfg.com.screenproc.util.GlUtil;
import pfg.com.screenproc.util.MyLog;
import android.graphics.Rect;
import android.opengl.Matrix;

/**
 * Created by FPENG3 on 2018/7/26.
 */

public class ScreenRecordActivity extends Activity implements SurfaceHolder.Callback, Choreographer.FrameCallback{

    private final static String TAG = "ScreenRecord";

    SurfaceView mSurfaceView;
    private static final String VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"test.mp4";
    private static final String RECORD_VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"record01.mp4";


    private Surface outputSurface;
    static VideoDecoderCore decoderCore;


    private static final int RECMETHOD_DRAW_TWICE = 0;
    private static final int RECMETHOD_FBO = 1;
    private static final int RECMETHOD_BLIT_FRAMEBUFFER = 2;
    private int mSelectedRecordMethod;
    private static boolean mRecordingEnabled = false;
    private RenderThread mRenderThread;
    Button btn_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fbo);
        mSurfaceView = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        mSurfaceView.getHolder().addCallback(this);

        mSelectedRecordMethod = RECMETHOD_FBO;

        btn_record = (Button) findViewById(R.id.fboRecord_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Choreographer.getInstance().postFrameCallback(this);

    }

    public void clickTogglePlaying(@SuppressWarnings("unused") View unused) {
        MyLog.logd(TAG, "clickTogglePlaying");
        decoderCore.start();
        decoderCore.waitForInit();

        Choreographer.getInstance().postFrameCallback(this);
    }

    public void clickToggleRecording(@SuppressWarnings("unused") View unused) {
        MyLog.logd(TAG, "clickToggleRecording");

        //Choreographer.getInstance().postFrameCallback(this);
        RenderHandler rh = mRenderThread.getHandler();
        if(rh != null) {
            mRecordingEnabled = !mRecordingEnabled;
            rh.setRecordingEnabled(mRecordingEnabled);
        }
        if(mRecordingEnabled) {
            btn_record.setText("Stop Record");
        } else {
            btn_record.setText("Start Record");
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        MyLog.logd(TAG,"surfaceCreated isCurrentThread:"+Looper.myLooper().isCurrentThread());
        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        File outputFile = new File(RECORD_VIDEO_FILE_PATH);
        mRenderThread = new RenderThread(mSurfaceView.getHolder(), outputFile);
        mRenderThread.start();
        mRenderThread.waitUntilReady();

        mRenderThread.setRecordMethod(mSelectedRecordMethod);

        mRenderThread.getHandler().sendSurfaceCreated();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                               int height) {
        MyLog.logd(TAG,"surfaceChanged isCurrentThread:"+Looper.myLooper().isCurrentThread());
        outputSurface = surfaceHolder.getSurface();
        decoderCore = new VideoDecoderCore(VIDEO_FILE_PATH, outputSurface);

        mRenderThread.getHandler().sendSurfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        MyLog.logd(TAG,"surfaceDestroyed isCurrentThread:"+Looper.myLooper().isCurrentThread());
        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendShutdown();
            try {
                mRenderThread.join();
            } catch (InterruptedException ie) {
                // not expected
                throw new RuntimeException("join was interrupted", ie);
            }
        }
        mRenderThread = null;
        mRecordingEnabled = false;
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        decoderCore.stop();
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void doFrame(long var1) {
        //MyLog.logd(TAG,"doFrame isCurrentThread:"+Looper.myLooper().isCurrentThread());
        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            Choreographer.getInstance().postFrameCallback(this);
            rh.sendDoFrame();
        }
    }

    private static class RenderThread extends Thread {

        private static final String TAG = "RenderThread";
        // Used to wait for the thread to start.
        private Object mStartLock = new Object();
        private boolean mReady = false;
        private RenderHandler mRenderHandler;
        private EGLCore mEglCore;
        private VideoEncoderCore mEncoderCore;
        private VideoEncoder mVideoEncoder;

        private SurfaceHolder mSurfaceHolder;
        private File mOutputFile;
        private int mRecordMethod;
        private WindowSurface mWindowSurface;
        private boolean mRecordedPrevious;

        private WindowSurface mInputWindowSurface;

        // Used for off-screen rendering.
        private int mOffscreenTexture;
        private int mFramebuffer;
        private int mDepthBuffer;
        private FullFrameRect mFullScreen;
        private Rect mVideoRect;
        private final float[] mIdentityMatrix;
        private int mSurfaceWidth;
        private int mSurfaceHeight;

        public RenderThread(SurfaceHolder holder, File outputFile) {
            mSurfaceHolder = holder;
            mOutputFile = outputFile;

            mVideoRect = new Rect();
            mIdentityMatrix = new float[16];
            Matrix.setIdentityM(mIdentityMatrix, 0);
        }

        @Override
        public void run() {
            Looper.prepare();
            mRenderHandler = new RenderHandler(this);
            //mEglCore = new EGLCore(decoderCore.eglContext, EGLCore.FLAG_TRY_GLES3 | EGLCore.FLAG_RECORDABLE);


            synchronized (mStartLock) {
                mStartLock.notifyAll();
                mReady = true;
            }
            Looper.loop();
            MyLog.logd(TAG, "looper quit");
            releaseGl();
            mEglCore.release();
            synchronized (mStartLock) {
                mReady = false;
            }
        }

        public void waitUntilReady() {
            synchronized (mStartLock) {
                try {
                    mStartLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public RenderHandler getHandler() {
            return mRenderHandler;
        }

        public void surfaceCreated() {
            MyLog.logd(TAG, "surfaceCreated");
            Surface surface = mSurfaceHolder.getSurface();
            //prepareGl(surface);
        }

        public void prepareGl(Surface surface) {
            MyLog.logd(TAG, "prepareGl");
            mWindowSurface = new WindowSurface(mEglCore, surface, false);
            mWindowSurface.makeCurrent();
            mFullScreen = new FullFrameRect(
                    new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));

        }

        public void surfaceChanged(int width, int height) {
            MyLog.logd(TAG, "surfaceChanged");
            //prepareFramebuffer(width, height);
            mSurfaceWidth = width;
            mSurfaceHeight = height;
        }

        public void prepareFramebuffer(int width, int height) {
            MyLog.logd(TAG, "prepareFramebuffer width:"+width+" height:"+height);
            GlUtil.checkGlError("prepareFramebuffer start");

            int[] values = new int[1];

            // Create a texture object and bind it.  This will be the color buffer.
            GLES20.glGenTextures(1, values, 0);
            GlUtil.checkGlError("glGenTextures");
            mOffscreenTexture = values[0];   // expected > 0
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);
            GlUtil.checkGlError("glBindTexture " + mOffscreenTexture);

            // Create texture storage.
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

            // Set parameters.  We're probably using non-power-of-two dimensions, so
            // some values may not be available for use.
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            GlUtil.checkGlError("glTexParameter");

            // Create framebuffer object and bind it.
            GLES20.glGenFramebuffers(1, values, 0);
            GlUtil.checkGlError("glGenFramebuffers");
            mFramebuffer = values[0];    // expected > 0
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
            GlUtil.checkGlError("glBindFramebuffer " + mFramebuffer);

            // Create a depth buffer and bind it.
            GLES20.glGenRenderbuffers(1, values, 0);
            GlUtil.checkGlError("glGenRenderbuffers");
            mDepthBuffer = values[0];    // expected > 0
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);
            GlUtil.checkGlError("glBindRenderbuffer " + mDepthBuffer);

            // Allocate storage for the depth buffer.
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                    width, height);
            GlUtil.checkGlError("glRenderbufferStorage");

            // Attach the depth buffer and the texture (color buffer) to the framebuffer object.
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, mDepthBuffer);
            GlUtil.checkGlError("glFramebufferRenderbuffer");
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);
            GlUtil.checkGlError("glFramebufferTexture2D");

            // See if GLES is happy with all this.
            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            /*if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                throw new RuntimeException("Framebuffer not complete, status=" + status);
            }*/

            // Switch back to the default framebuffer.
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            GlUtil.checkGlError("prepareFramebuffer done");
        }

        public void releaseGl() {
            MyLog.logd(TAG, "releaseGl");
            if (mWindowSurface != null) {
                mWindowSurface.release();
                mWindowSurface = null;
            }

            mEglCore.makeNothingCurrent();
        }

        public void setRecordingEnabled(boolean enabled) {
            MyLog.logd(TAG, "setRecordingEnabled enabled:"+enabled);
            mEglCore = new EGLCore(decoderCore.eglContext, EGLCore.FLAG_TRY_GLES3 | EGLCore.FLAG_RECORDABLE);
            prepareGl(mSurfaceHolder.getSurface());
            prepareFramebuffer(mSurfaceWidth, mSurfaceHeight);
            if(enabled) {
                startEncoder();
            } else {
                stopEncoder();
            }
        }

        public void setRecordMethod(int recordMethod) {
            MyLog.logd(TAG, "setRecordMethod " + recordMethod);
            mRecordMethod = recordMethod;
        }

        public void startEncoder() {
            MyLog.logd(TAG, "startEncoder videoWidth:" + decoderCore.getVideoWidth()+" videoHeight:"+decoderCore.getVideoHeight());
            mEncoderCore = new VideoEncoderCore(decoderCore.getVideoWidth(), decoderCore.getVideoHeight(), RECORD_VIDEO_FILE_PATH);
            mInputWindowSurface = new WindowSurface(mEglCore, mEncoderCore.getInputSurface(), false);
            mVideoEncoder = new VideoEncoder(mEncoderCore);
        }

        public void stopEncoder() {
            MyLog.logd(TAG, "stopEncoder");
            if (mVideoEncoder != null) {
                MyLog.logd(TAG, "stopping recorder, mVideoEncoder=" + mVideoEncoder);
                mVideoEncoder.stopRecording();
                // TODO: wait (briefly) until it finishes shutting down so we know file is
                //       complete, or have a callback that updates the UI
                mVideoEncoder = null;
            }

            if (mInputWindowSurface != null) {
                mInputWindowSurface.release();
                mInputWindowSurface = null;
            }
        }

        public void shutdown() {
            MyLog.logd(TAG, "shutdown");
            stopEncoder();
            Looper.myLooper().quit();
        }

        public void doFrame() {
            MyLog.logd(TAG, "doFrame mRecordingEnabled:"+mRecordingEnabled+" mRecordedPrevious:"+mRecordedPrevious);
            boolean swapResult;
            if (!mRecordingEnabled || mRecordedPrevious) {
                mRecordedPrevious = false;
                // Render the scene, swap back to front.
                draw();
                //swapResult = mWindowSurface.swapBuffers();
            } else {
                mRecordedPrevious = true;
                if(mRecordMethod == RECMETHOD_FBO) {
                    mVideoEncoder.frameAvailableSoon();
                    mInputWindowSurface.makeCurrent();
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);    // again, only really need to
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);     //  clear pixels outside rect
                    GLES20.glViewport(mVideoRect.left, mVideoRect.top,
                            mVideoRect.width(), mVideoRect.height());
                    mFullScreen = new FullFrameRect(
                            new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
                    mFullScreen.drawFrame(mOffscreenTexture, mIdentityMatrix);
                    // mInputWindowSurface.setPresentationTime(timeStampNanos);
                    mInputWindowSurface.swapBuffers();
                }
            }
        }

        public void update() {

        }

        public void draw() {

        }

    }

    private static class RenderHandler extends Handler {

        private static final int MSG_SURFACE_CREATED = 0;
        private static final int MSG_SURFACE_CHANGED = 1;
        private static final int MSG_DO_FRAME = 2;
        private static final int MSG_RECORDING_ENABLED = 3;
        private static final int MSG_RECORD_METHOD = 4;
        private static final int MSG_SHUTDOWN = 5;

        WeakReference<RenderThread> renderThreadRef;

        public RenderHandler(RenderThread renderThread) {
            renderThreadRef = new WeakReference<RenderThread>(renderThread);
        }

        public void sendSurfaceCreated() {
            sendMessage(obtainMessage(MSG_SURFACE_CREATED));
        }

        public void sendSurfaceChanged(int width, int height) {
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height));
        }

        public void sendDoFrame() {
            sendMessage(obtainMessage(MSG_DO_FRAME));
        }

        public void setRecordingEnabled(boolean enabled) {
            sendMessage(obtainMessage(MSG_RECORDING_ENABLED, enabled ? 1:0, 0));
        }

        public void setRecordMethod(int recordMethod) {
            sendMessage(obtainMessage(MSG_RECORD_METHOD, recordMethod, 0));
        }

        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SURFACE_CREATED:
                    renderThreadRef.get().surfaceCreated();
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThreadRef.get().surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_DO_FRAME:
                    renderThreadRef.get().doFrame();
                    break;
                case MSG_RECORDING_ENABLED:
                    boolean enabled = (msg.arg1 == 1);
                    renderThreadRef.get().setRecordingEnabled(enabled);
                    break;
                case MSG_RECORD_METHOD:
                    renderThreadRef.get().setRecordMethod(msg.arg1);
                    break;
                case MSG_SHUTDOWN:
                    renderThreadRef.get().shutdown();
                    break;
                default:
                    break;
            }
        }
    };


}
