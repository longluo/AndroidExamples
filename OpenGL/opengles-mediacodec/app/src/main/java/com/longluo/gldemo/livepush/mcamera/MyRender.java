package com.longluo.gldemo.livepush.mcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.longluo.gldemo.R;
import com.longluo.gldemo.livepush.opengl.BaseRender;
import com.longluo.gldemo.livepush.opengl.FboRender;
import com.longluo.gldemo.livepush.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRender extends BaseRender {

    Context mContext;

    protected int mViewWidth;
    protected int mViewHeight;

    private float[] mVertexCoordinate = new float[]{
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

    };
    private FloatBuffer mVertexBuffer;
    private float[] mFragmentCoordinate = new float[]{
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    private FloatBuffer mFragmentBuffer;

    private int program;
    private int vPosition;
    private int fPosition;
    private int u_Matrix;
    private int mVboId;
    private float[] matrix = new float[16];
    private String TAG = "size";
    //    private int cameraTextureId;
    private SurfaceTexture surfaceTexture;

    FboRender mFboRender;

    public MyRender(Context context) {
        this.mContext = context;
        //一个float 4字节
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

        mFboRender = new FboRender(mContext);
        // fbo 的坐标是标准坐标,设定为正方形
        Matrix.orthoM(matrix, 0, -1, 1, -1f, 1f, -1f, 1f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFboRender.onSurfaceCreated(mViewWidth, mViewHeight);

        String vertexSource = Utils.getGLResource(mContext, R.raw.vertex_shader_matrix);
        String fragmentSource = Utils.getGLResource(mContext, R.raw.fragment_shader_camera);
        program = Utils.createProgram(vertexSource, fragmentSource);

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
//        int sTexture = GLES20.glGetUniformLocation(program,"sTexture");
        u_Matrix = GLES20.glGetUniformLocation(program, "u_Matrix");

        // 创建 vbos
        int[] vBos = new int[1];
        // 分配n个缓冲区对象,申明
        GLES20.glGenBuffers(1, vBos, 0);
        mVboId = vBos[0];
        // 赋值 vbos，初始化
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVboId);
        // 开辟 vbos , 分配空间
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (mVertexCoordinate.length + mFragmentCoordinate.length) * 4,
                null, GLES20.GL_STATIC_DRAW);
        // 初始化,分两段，第一段存顶底数据，第二段存片元数据
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, mVertexCoordinate.length * 4, mVertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, mVertexCoordinate.length * 4,
                mFragmentCoordinate.length * 4, mFragmentBuffer);
        //一旦我们用缓冲区绘制完成，我们应该解除它
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // 激活 program
        GLES20.glUseProgram(program);

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int cameraTextureId = textures[0];

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES, cameraTextureId);
        // 设置纹理环绕方式
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 设置纹理过滤方式
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        surfaceTexture = new SurfaceTexture(cameraTextureId);
        if (renderListener != null)
            renderListener.onSurfaceCreate(surfaceTexture);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES, 0);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mFboRender.onSurfaceCreated(width, height);
        GLES20.glViewport(0, 0, width, height);

//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.zly, opts);
//        float imageWidth = opts.outWidth;
//        float imageHeight = opts.outHeight;
//
//        //        变形参数计算，https://www.jianshu.com/p/8049014b7952
//        float ratio= width > height ? (float)width / height : (float)height / width;
//        if (width > height) {
//            Matrix.orthoM(matrix, 0, -width / ((height / imageHeight) * imageWidth),
//                    width / ((height / imageHeight) * imageWidth), -1f, 1f, -1f, 1f);
//        } else {
//            //竖着，计算(width / imageWidth) * imageHeight为让图片先以宽度为准缩放到屏幕内，然后再把高度拉伸铺满竖向的屏幕
//            // 第一步，需要设置屏幕的正常宽高比，宽度设为参照1，则高度显示需要[-height/width,height/width]
//            // 第二步，图片在当前尺寸下，会显示为正方形，因为高度受到了拉伸。
//            // w:500.h:300,比例系数，h/w=0.6,此时为了让图片显示正确，需要反向乘w/h,即
//            // 需要 *imageWidth/imageHeight来增大上下边界来显示正常,比方：https://www.jianshu.com/p/044d521351ec，为了图片显示正常，
//            // 宽度拉伸了2
//            Matrix.orthoM(matrix, 0, -1f, 1f, -(float)height/width*imageWidth/imageHeight ,
//                    (float)height/width *imageWidth/imageHeight, -1f, 1f);
//        }
//        Matrix.rotateM(matrix, 0, 0, 1, 0, 0);
        // fbo 的坐标是标准坐标
//        Matrix.rotateM(matrix, 0, 180, 1, 0, 0);

        // fbo 的坐标是标准坐标
        Matrix.rotateM(matrix, 0, 180, 1, 0, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mFboRender.onBindFbo();
        // 清屏并绘制红色
        // 　第一条语句表示清除颜色设为红色，第二条语句表示实际完成了把整个窗口清除为黑色的任务，glClear（）的唯一参数表示需要被清除的缓冲区。
//        GLES20.glClearColor(1f,0f,0f,1f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 激活 program
        GLES20.glUseProgram(program);
        // 绑定纹理，如果使用需要再次glBindTexture
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES,cameraTextureId);
        surfaceTexture.updateTexImage();
        // 把matrix传入，设置uMatrix正交矩阵的值
        GLES20.glUniformMatrix4fv(u_Matrix, 1, false, matrix, 0);

        //glBufferData顶点数据已经缓存了，在这里取出
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVboId);

        /**
         * 设置坐标
         * 2：2个为一个点
         * GLES20.GL_FLOAT：float 类型
         * false：不做归一化
         * 8：步长是 8
         */
        //默认非激活状态
        GLES20.glEnableVertexAttribArray(vPosition);
        // 取2个数据 ，一个float数据占四个字节，参考 ：https://www.cnblogs.com/fordreamxin/p/4676208.html
        // 跳转8个字节位再取另外2个数据，这是实现块状数据存储的关键
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8, 0);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8, mVertexCoordinate.length * 4);

        // GL_TRIANGLE_STRIP+4个顶点，对应矩形 https://www.cnblogs.com/lxb0478/p/6381677.html
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //清零
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_BINDING_EXTERNAL_OES,0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        mFboRender.onUnbindFbo();

        mFboRender.onDrawFrame();
    }

    /**
     * 重置矩阵参数
     */
    public void resetMatrix() {
        Matrix.setIdentityM(matrix, 0);
    }

    /**
     * 旋转矩阵
     *
     * @param offset 偏移
     * @param a      角度
     * @param x      x轴
     * @param y      y轴
     * @param z      z轴
     */
    public void rotateMatrix(int offset, float a, float x, float y, float z) {
        Matrix.rotateM(matrix, offset, a, x, y, z);
    }


    public void setViewHeight(int viewHeight) {
        this.mViewHeight = viewHeight;
    }

    public void setViewWidth(int viewWidth) {
        this.mViewWidth = viewWidth;
    }

    RenderListener renderListener;

    public void setOnRenderListener(RenderListener onRenderListener) {
        this.renderListener = onRenderListener;
    }

    public interface RenderListener {
        void onSurfaceCreate(SurfaceTexture surfaceTexture);
    }

    public int getTextureId() {
        return mFboRender.getTextureId();
    }
}
