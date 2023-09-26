package pfg.com.screenproc;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pfg.com.screenproc.egl.EGLCore;
import pfg.com.screenproc.egl.WindowSurface;
import pfg.com.screenproc.util.GlUtil;
import pfg.com.screenproc.util.MyLog;

import pfg.com.screenproc.egl.FullFrameRect;
import pfg.com.screenproc.egl.Drawable2d;
import pfg.com.screenproc.egl.Texture2dProgram;
import pfg.com.screenproc.objects.Triangle;
import pfg.com.screenproc.util.MatrixHelper;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import static pfg.com.screenproc.FBORenderer.RecordHandler.MSG_FRAME_AVAILABLE;
import static pfg.com.screenproc.FBORenderer.RecordHandler.MSG_START_RECORD;
import static pfg.com.screenproc.FBORenderer.RecordHandler.MSG_STOP_RECORD;

/**
 * Created by FPENG3 on 2018/8/3.
 */

public class FBORenderer implements IRenderer{

    private final static String TAG = "FBORenderer";

    private VideoGLSurfaceView mGLSurfaceView;

    static private float[] mIdentityMatrix = new float[16];
    static private float[] mDisplayProjectionMatrix = new float[16];
    // 初始化单位矩阵
    static private float [] modelMatrix = new float[16];

    // Used for off-screen rendering.
    static private int mOffscreenTexture;
    private int mFramebuffer;
    private int mDepthBuffer;
    static private FullFrameRect mFullScreen;
    static private Triangle mTriangle;
    static private int mWidth, mHeight;

    private HandlerThread mHanderThread;
    private RecordHandler mRecordHandler;
    boolean isStoped = false;
    private static float mAngle;
    static int mSmallDim;

    static private EGLContext mEGLContext;

    public FBORenderer(Context context, VideoGLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        prepareGl();
        mHanderThread = new HandlerThread("Record Thread");
        mHanderThread.start();
        mTriangle = new Triangle();
        mRecordHandler = new RecordHandler(mHanderThread.getLooper());
        mEGLContext = EGL14.eglGetCurrentContext();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mWidth = width;
        mHeight = height;
        prepareFramebuffer(width, height);

        GLES30.glViewport(0, 0, width, height);

        Matrix.setIdentityM(mIdentityMatrix, 0);

        mSmallDim = Math.min(width, height);

        // 解决下一次开启录制时挂掉的问题
        // 原因开启新的录制时重新开启一个线程这时需要重新与sharedContext绑定并创建新的EGLCore等对象
        // mRecordHandler.sendMessage(mRecordHandler.obtainMessage(RecordHandler.MSG_PREPARE_WORK));
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        MyLog.logd(TAG, "onDrawFrame");
        // 方法1
        /*draw();
        // 接着GLSurfaceView的GLRender线程会自动调用swapBuffer方法用于显示

        // 开始录制
        if(!isStoped) {
            mRecordHandler.sendEmptyMessage(MSG_FRAME_AVAILABLE);
        }*/


        // 方法2
        // 线程直接共享mOffscreenTexture(注意创建EGLCore时要传入shareContext)，这里的draw应该是描化到了mFramebuffer
        // 在opengl这块不用上面的FrameBuffer和OffScreen方式也可以渲染显示，
        // 那么这里的区别是什么？他们分别是把图像画到哪里去了？
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
        GlUtil.checkGlError("glBindFramebuffer");
        draw();


        // Blit to display.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GlUtil.checkGlError("glBindFramebuffer");
        mFullScreen.drawFrame(mOffscreenTexture, mIdentityMatrix);
        // 接着GLSurfaceView的GLRender线程会自动调用swapBuffer方法用于显示

        // 开始录制
        // 这里一定要在新线程中来完成录制工作，因为上述draw需要swapBuffer显示在屏幕上
        if(!isStoped) {
            mRecordHandler.sendEmptyMessage(MSG_FRAME_AVAILABLE);
        }
    }

    public static class RecordHandler extends Handler {

        public static final int MSG_PREPARE_WORK = 0;
        public static final int MSG_FRAME_AVAILABLE = 1;
        public static final int MSG_START_RECORD = 2;
        public static final int MSG_STOP_RECORD = 3;
        public static final int MSG_SHUT_DOWN = 4;

        private FullFrameRect mRecordFullScreen;
        private Triangle mRecordTriangle;


        EGLCore mEglCore;
        WindowSurface mInputSurface;
        VideoEncoder mEncoder;
        VideoEncoderCore mEncoderCore;
        private static final String VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"fbo_record.mp4";

        public RecordHandler(Looper looper) {
            super(looper);
        }

        private void drawRecord() {
            //GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            MatrixHelper.perspectiveM(mDisplayProjectionMatrix, 45, (float) mWidth / (float) mHeight, 1f, 10f);

            Matrix.setIdentityM(modelMatrix, 0);

            // 沿着Z轴平移-2.5f, 这样在-1到-10范围内所以画面就可见了。
            Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);

            long time = SystemClock.uptimeMillis() % 1000L;
            float angleInDegree = (360.0f / 1000.0f ) * ((int)time);

            Matrix.rotateM(modelMatrix, 0, angleInDegree, 0f, 0f, 1f);

            float [] temp = new float[16];
            Matrix.multiplyMM(temp, 0, mDisplayProjectionMatrix, 0, modelMatrix, 0);
            System.arraycopy(temp, 0, mDisplayProjectionMatrix, 0, temp.length);

            mRecordTriangle.draw(mDisplayProjectionMatrix);
        }

        public void prepareEncoder() {
            mEncoderCore = new VideoEncoderCore(mWidth, mHeight, VIDEO_FILE_PATH);
            // 方法1
            // mEglCore = new EGLCore(null, EGLCore.FLAG_TRY_GLES3 | EGLCore.FLAG_RECORDABLE);

            // 方法2
            //
            mEglCore = new EGLCore(mEGLContext, EGLCore.FLAG_TRY_GLES3 | EGLCore.FLAG_RECORDABLE);

            mInputSurface = new WindowSurface(mEglCore, mEncoderCore.getInputSurface(), false);
            mInputSurface.makeCurrent();
        }

        private void startRecord() {
            mEncoder = new VideoEncoder(mEncoderCore);
            mEncoder.startRecording();
        }

        private void stopRecord() {
            removeMessages(MSG_FRAME_AVAILABLE);
            mEncoder.stopRecording();
        }

        private void shutdown() {
            stopRecord();
            Looper.myLooper().quit();
        }

        private void frameAvailable() {
            if(mEncoder != null) {
                // 方法1
                /*
                mEncoder.frameAvailableSoon();

                if(mRecordFullScreen == null) {
                    mRecordFullScreen = new FullFrameRect(
                            new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
                }
                if(mRecordTriangle == null) {
                    mRecordTriangle = new Triangle();
                }

                GLES20.glClearColor(0f, 0f, 0f, 1f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glViewport(0, 0, mWidth, mHeight);
                drawRecord();
                // mInputWindowSurface.setPresentationTime(timeStampNanos);

                mInputSurface.swapBuffers();
                */

                // 方法2
                mEncoder.frameAvailableSoon();
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);    // again, only really need to
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);                 //  clear pixels outside rect
                GLES20.glViewport(0, 0, mWidth, mHeight);
                mFullScreen.drawFrame(mOffscreenTexture, mIdentityMatrix);
                mInputSurface.swapBuffers();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PREPARE_WORK:
                    prepareEncoder();
                    break;
                case MSG_START_RECORD:
                    startRecord();
                    break;
                case MSG_STOP_RECORD:
                    stopRecord();
                    break;
                case MSG_SHUT_DOWN:
                    shutdown();
                    break;
                case MSG_FRAME_AVAILABLE:
                    frameAvailable();
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void startRecord(EGLContext eglContext) {
        MyLog.logd(TAG, "startRecord(eglContext)");
    }

    @Override
    public void startRecord() {
        MyLog.logd(TAG, "startRecord()");
        mRecordHandler.sendMessage(mRecordHandler.obtainMessage(RecordHandler.MSG_PREPARE_WORK));
        mRecordHandler.sendMessage(mRecordHandler.obtainMessage(MSG_START_RECORD));

        isStoped = false;
    }

    @Override
    public void stopRecord() {
        MyLog.logd(TAG, "stopRecord()");
        mRecordHandler.sendMessage(mRecordHandler.obtainMessage(MSG_STOP_RECORD));
        isStoped = true;
    }

    @Override
    public void shutdown() {
        mRecordHandler.sendMessage(mRecordHandler.obtainMessage(MSG_STOP_RECORD));
        isStoped = true;
    }

    private void prepareGl() {
        MyLog.logd(TAG, "prepareGl");

        // Used for blitting texture to FBO.
        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));

        // Program used for drawing onto the screen.
        // mProgram = new FlatShadedProgram();

        // Set the background color.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Disable depth testing -- we're 2D only.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // Don't need backface culling.  (If you're feeling pedantic, you can turn it on to
        // make sure we're defining our shapes correctly.)
        GLES20.glDisable(GLES20.GL_CULL_FACE);
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
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        // Switch back to the default framebuffer.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GlUtil.checkGlError("prepareFramebuffer done");
    }

    private static void draw() {
        GLES20.glClearColor(0f, 0f, 0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        MatrixHelper.perspectiveM(mDisplayProjectionMatrix, 45, (float) mWidth / (float) mHeight, 1f, 10f);

        Matrix.setIdentityM(modelMatrix, 0);

        // 沿着Z轴平移-2.5f, 这样在-1到-10范围内所以画面就可见了。
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);

        setRotation();

        Matrix.rotateM(modelMatrix, 0, mAngle, 0f, 0f, 1f);

        Matrix.scaleM(modelMatrix, 0, 0.6f, 0.6f, 1.0f);

        float [] temp = new float[16];
        Matrix.multiplyMM(temp, 0, mDisplayProjectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, mDisplayProjectionMatrix, 0, temp.length);

        mTriangle.draw(mDisplayProjectionMatrix);
    }

    public static void setRotation() {
        final float ONE_BILLION_F = 50000000000.0f;
        final float elapsedSeconds = SystemClock.elapsedRealtime() / ONE_BILLION_F;

        // Spin the triangle.  We want one full 360-degree rotation every 3 seconds,
        // or 120 degrees per second.
        final int SECS_PER_SPIN = 3;
        float angleDelta = (360.0f / SECS_PER_SPIN) * elapsedSeconds;
        MyLog.logd(TAG, "elapsedSeconds:"+elapsedSeconds+" angleDelta:"+angleDelta);
        // Normalize.  We're not expecting it to be way off, so just iterate.
        float angle = mAngle + angleDelta;
        while (angle >= 360.0f) {
            angle -= 360.0f;
        }
        while (angle <= -360.0f) {
            angle += 360.0f;
        }
        mAngle = angle;
    }


}
