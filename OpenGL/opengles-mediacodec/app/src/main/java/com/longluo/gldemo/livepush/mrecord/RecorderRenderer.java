package com.longluo.gldemo.livepush.mrecord;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.longluo.gldemo.R;
import com.longluo.gldemo.livepush.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RecorderRenderer implements GLSurfaceView.Renderer {
    private Context mContext;

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
    private int mVboId;
    private int mProgram;
    private int vPosition;
    private int fPosition;
    private int mTextureId;

    public RecorderRenderer(Context context, int textureId) {
        this.mContext = context;
        this.mTextureId = textureId;

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
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexSource = Utils.getGLResource(mContext, R.raw.vertex_shader);
        String fragmentSource = Utils.getGLResource(mContext, R.raw.fragment_shader);
        mProgram = Utils.createProgram(vertexSource, fragmentSource);
        // 获取坐标
        vPosition = GLES20.glGetAttribLocation(mProgram, "v_Position");
        fPosition = GLES20.glGetAttribLocation(mProgram, "f_Position");


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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 激活 program
        GLES20.glUseProgram(mProgram);

        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVboId);

          /*设置坐标
          2：2个为一个点
          GLES20.GL_FLOAT：float 类型
          false：不做归一化
          8：步长是 8*/

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
}
