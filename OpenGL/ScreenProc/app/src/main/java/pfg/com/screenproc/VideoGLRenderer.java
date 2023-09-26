package pfg.com.screenproc;

import pfg.com.screenproc.util.CheckGlError;
import pfg.com.screenproc.util.Constants;
import pfg.com.screenproc.util.MatrixHelper;
import pfg.com.screenproc.util.MyLog;
import pfg.com.screenproc.util.ShaderHelper;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by FPENG3 on 2018/7/30.
 */

public class VideoGLRenderer implements IRenderer
        , SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnVideoSizeChangedListener {
    private static final String TAG = "VideoGLRenderer";
    private Context context;
    private int aPositionLocation;
    private int programId;
    private FloatBuffer vertexBuffer;

    // X,Y
    private final float[] vertexData = {
            1f, -1f, 0f,
           -1f, -1f, 0f,
            1f,  1f, 0f,
           -1f,  1f, 0f
    };

    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    // S, T
    private final float[] textureVertexData = {
            1f, 0f,
            0f, 0f,
            1f, 1f,
            0f, 1f
    };
    private FloatBuffer textureVertexBuffer;
    private int uTextureSamplerLocation;
    private int aTextureCoordLocation;
    private int textureId;

    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;
    private float[] mSTMatrix = new float[16];
    private int uSTMMatrixHandle;

    private boolean updateSurface;
    private boolean playerPrepared;
    private int screenWidth, screenHeight;

    VideoEncoder mVideoEncoder;
    private static final String RECORD_VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"texture_record.mp4";

    private GLSurfaceView mGLSurfaceView;

    public VideoGLRenderer(Context context, GLSurfaceView glSurfaceView, String videoPath) {
        this.context = context;
        playerPrepared = false;
        mGLSurfaceView = glSurfaceView;
        synchronized (this) {
            updateSurface = false;
        }
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, Uri.parse(videoPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);

        mediaPlayer.setOnVideoSizeChangedListener(this);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexShader = Constants.video_vertex_shader;
        String fragmentShader = Constants.video_fragment_shader;
        programId = ShaderHelper.buildProgram(vertexShader, fragmentShader);
        aPositionLocation = GLES20.glGetAttribLocation(programId, "aPosition");

        uMatrixLocation = GLES20.glGetUniformLocation(programId, "uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        uTextureSamplerLocation = GLES20.glGetUniformLocation(programId, "sTexture");
        aTextureCoordLocation = GLES20.glGetAttribLocation(programId, "aTexCoord");


        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        CheckGlError.checkGlError("glBindTexture mTextureID");
        /* GLES11Ext.GL_TEXTURE_EXTERNAL_OES的用处？
         * 之前提到视频解码的输出格式是YUV的（YUV420p，应该是），那么这个扩展纹理的作用就是实现YUV格式到RGB的自动转化，
         * 我们就不需要再为此写YUV转RGB的代码了
         * GL_TEXTURE_EXTERNAL_OES 和 GL_TEXTURE_2D的区别？
         */
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);//监听是否有新的一帧数据到来

        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);
        surface.release();

        if (!playerPrepared) {
            try {
                mediaPlayer.prepare();
                playerPrepared = true;
            } catch (IOException t) {
                Log.e(TAG, "media player prepare failed");
            }
            mediaPlayer.start();
            playerPrepared = true;
        }

        //MyLog.logd(TAG, "onSurfaceCreated---eglContext: "+EGL14.eglGetCurrentContext()+" threadid: "+Thread.currentThread().getId());

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        MyLog.logd(TAG, "onSurfaceChanged: " + width + " " + height);
        screenWidth = width;
        screenHeight = height;

        if (surfaceTexture != null) { //need init
            mVideoEncoder = new VideoEncoder(968, 544, RECORD_VIDEO_FILE_PATH);
        } else {
            //todo resetSize for preview and encoder
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        MyLog.logd(TAG, "onDrawFrame: ");
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this) {
            if (updateSurface) {
                // 没有使用VIdeoEncoder多线程时，意思是InputWindowSurface和SurfaceTexture在同一个线程出现如下错误：
                /*
                 * E/GLConsumer: [SurfaceTexture-1-8514-0] checkAndUpdateEglState: invalid current EGLContext
                 * java.lang.IllegalStateException: Unable to update texture contents (see logcat for details)
                 * at android.graphics.SurfaceTexture.nativeUpdateTexImage(Native Method)
                 */
                surfaceTexture.updateTexImage();//获取新数据
                surfaceTexture.getTransformMatrix(mSTMatrix);//让新的纹理和纹理坐标系能够正确的对应,mSTMatrix的定义是和projectionMatrix完全一样的。
                updateSurface = false;
            }
        }

        // 接着对SurfaceTexture的YUV视频数据进行滤镜处理
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);

        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);

        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glUniform1i(uTextureSamplerLocation, 0);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 开始录制
        mVideoEncoder.setTextureId(textureId);
        mVideoEncoder.frameAvailable(surfaceTexture);


    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
        mGLSurfaceView.requestRender();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        MyLog.logd(TAG, "onVideoSizeChanged: " + width + " " + height);
        updateProjection(width, height);

    }

    private void updateProjection(int videoWidth, int videoHeight) {
        float screenRatio = (float) screenWidth / screenHeight;
        float videoRatio = (float) videoWidth / videoHeight;
        // 1
        /*if (videoRatio > screenRatio) {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -videoRatio / screenRatio, videoRatio / screenRatio, -1f, 1f);
        } else
            Matrix.orthoM(projectionMatrix, 0, -screenRatio / videoRatio, screenRatio / videoRatio, -1f, 1f, -1f, 1f);*/
        // ====================================
        // 2
        // Z轴值-1到-10,但是默认Z是0，因此需要把Z平移到这个范围,否则看不到任何画像
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) videoWidth / (float) videoHeight, 1f, 10f);
        // 初始化单位矩阵
        float [] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);

        // 沿着Z轴平移-2.5f, 这样在-1到-10范围内所以画面就可见了。
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        // 沿着X轴旋转
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        float [] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void startRecord(EGLContext eglContext) {
        MyLog.logd(TAG,"startRecord---eglContext(eglGetCurrentContext): "+eglContext+" threadid: "+Thread.currentThread().getId());
        mVideoEncoder.startRecording(eglContext);
    }

    @Override
    public void startRecord() {
        // do nothing
    }

    @Override
    public void stopRecord() {
        mVideoEncoder.stopRecording();
    }

    @Override
    public void shutdown() {
        stopRecord();
    }
}
