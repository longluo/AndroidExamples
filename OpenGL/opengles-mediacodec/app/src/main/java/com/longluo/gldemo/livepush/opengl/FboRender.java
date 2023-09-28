package com.longluo.gldemo.livepush.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * 离屏渲染：把所有的纹理先绘制到 fbo 上面，然后再从 fbo 绘制到窗口上
 */
public class FboRender implements IFboRender {
    private static final String TAG = "FboRender";
    private int mFobWidth = 0;
    private int mFboHeight = 0;

    public final String mVertexShaderStr = "attribute vec4 v_Position;\n" +
            "    attribute vec2 f_Position;\n" +
            "    varying vec2 ft_Position;\n" +
            "    void main() {\n" +
            "        ft_Position = f_Position;\n" +
            "        gl_Position = v_Position;\n" +
            "    }";

    public final String mFragmentShaderStr = "precision mediump float;\n" +
            "varying vec2 ft_Position;\n" +
            "uniform sampler2D sTexture;\n" +
            "void main() {\n" +
            "    gl_FragColor=texture2D(sTexture, ft_Position);\n" +
            "}";

    /**
     * 顶点坐标
     */
    private float[] mVertexCoordinate = new float[]{
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };
    private FloatBuffer mVertexBuffer;

    /**
     * 纹理坐标
     */
    private float[] mFragmentCoordinate = new float[]{
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    private FloatBuffer mFragmentBuffer;
    private int mProgram;
    private int vPosition;
    private int fPosition;
    private int uMatrix;
    private int mVboId;
    private int mFboId;
    private int mTextureId;
    private float[] matrix = new float[16];
    private Context mContext;

    public FboRender(Context context) {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexCoordinate.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVertexCoordinate);
        mVertexBuffer.position(0);

        mFragmentBuffer = ByteBuffer.allocateDirect(mFragmentCoordinate.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mFragmentCoordinate);
        mFragmentBuffer.position(0);

        this.mContext = context;
        mFobWidth = Utils.getScreenWidth(context);
        mFboHeight = Utils.getScreenHeight(context);
    }


    @Override
    public void onSurfaceCreated(int viewWidth, int viewHeight) {
        mProgram = Utils.createProgram(mVertexShaderStr, mFragmentShaderStr);
        // 获取坐标
        vPosition = GLES20.glGetAttribLocation(mProgram, "v_Position");
        fPosition = GLES20.glGetAttribLocation(mProgram, "f_Position");
        int sTexture = GLES20.glGetUniformLocation(mProgram, "sTexture");
        uMatrix = GLES20.glGetUniformLocation(mProgram, "u_Matrix");


        // 创建 vbos
        int[] vBos = new int[1];
        GLES20.glGenBuffers(1, vBos, 0);
        // 绑定 vbos
        mVboId = vBos[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVboId);
        // 开辟 vbos
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (mVertexCoordinate.length + mFragmentCoordinate.length) * 4,
                null, GLES20.GL_STATIC_DRAW);
        // 赋值 vbos
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, mVertexCoordinate.length * 4, mVertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, mVertexCoordinate.length * 4,
                mFragmentCoordinate.length * 4, mFragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // 创建绑定激活纹理
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        mTextureId = textureIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(sTexture, 0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 创建 fbo 并把纹理绑定到 fbo
        int[] fBoIds = new int[1];
        GLES20.glGenBuffers(1, fBoIds, 0);
        mFboId = fBoIds[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId);

        if (viewWidth > 0) {
            mFobWidth = viewWidth;
        }
        if (viewHeight > 0) {
            mFboHeight = viewHeight;
        }

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mFobWidth, mFboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTextureId, 0);
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "fbo bind failure");
        } else {
            Log.e(TAG, "fbo bind success");
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glUseProgram(mProgram);
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        // 设置正交矩阵的值
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, matrix, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVboId);
        /**
         * 设置坐标
         * 2：2个为一个点
         * GLES20.GL_FLOAT：float 类型
         * false：不做归一化
         * 8：步长是 8
         */
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8, 0);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8, mVertexCoordinate.length * 4);
        // 绘制到屏幕
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        // 解绑
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void onBindFbo() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId);
    }

    @Override
    public void onUnbindFbo() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getTextureId() {
        return mTextureId;
    }
}
