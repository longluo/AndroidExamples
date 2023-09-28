package com.longluo.gldemo.livepush.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.longluo.gldemo.R;
import com.longluo.gldemo.livepush.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PictureRender implements GLSurfaceView.Renderer {

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
    private int mTexture;
    private float[] matrix = new float[16];
    private String TAG = "size";

    public PictureRender(Context context) {
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
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexSource = Utils.getGLResource(mContext, R.raw.vertex_shader_matrix);
        String fragmentSource = Utils.getGLResource(mContext, R.raw.fragment_shader);
        program = Utils.createProgram(vertexSource, fragmentSource);

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        int sTexture = GLES20.glGetUniformLocation(program, "sTexture");
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

        mTexture = loadTexture(R.mipmap.zly);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLES20.glActiveTexture(mTexture);
        // sTexture绑定到纹理单元0
        // https://blog.csdn.net/prahs/article/details/49818345
        GLES20.glUniform1i(sTexture, 0);
        //清空
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }

    private int loadTexture(int resId) {
        // 生成绑定纹理
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // 设置纹理环绕方式
        // https://blog.csdn.net/u012278016/article/details/105616537
        // 纹理坐标范围是0~1 ，如果设置的值超过1 怎么办？那么就由GL_TEXTURE_WRAP_S、GL_TEXTURE_WRAP_T参数决定怎么做
        // https://www.cnblogs.com/zhangzhang-y/p/13360369.html GLES20.参数解析
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        // 设置纹理过滤方式，当纹理需要比方放大缩小时,GL_LINEAR较为模糊，GL_TEXTURE_MIN_FILTER线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        // GL_TEXTURE_MAG_FILTER放大时的纹理过滤方式，GL_LINEAR类似于高斯模糊，取周边范围内颜色的平均值；GLES20.GL_NEAREST:锐化，只会取单色
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        // android.opengl.GLUtils工具中有，链接的android自带的navice方法
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // 解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.zly, opts);
        float imageWidth = opts.outWidth;
        float imageHeight = opts.outHeight;

        //        变形参数计算，https://www.jianshu.com/p/8049014b7952
        float ratio = width > height ? (float) width / height : (float) height / width;
        if (width > height) {
            Matrix.orthoM(matrix, 0, -width / ((height / imageHeight) * imageWidth),
                    width / ((height / imageHeight) * imageWidth), -1f, 1f, -1f, 1f);
        } else {
            //竖着，计算(width / imageWidth) * imageHeight为让图片先以宽度为准缩放到屏幕内，然后再把高度拉伸铺满竖向的屏幕
            // 第一步，需要设置屏幕的正常宽高比，宽度设为参照1，则高度显示需要[-height/width,height/width]
            // 第二步，图片在当前尺寸下，会显示为正方形，因为高度受到了拉伸。
            // w:500.h:300,比例系数，h/w=0.6,此时为了让图片显示正确，需要反向乘w/h,即
            // 需要 *imageWidth/imageHeight来增大上下边界来显示正常,比方：https://www.jianshu.com/p/044d521351ec，为了图片显示正常，
            // 宽度拉伸了2
//            Matrix.orthoM(matrix, 0, -1f, 1f, -(float)height/width*imageWidth/imageHeight ,
//                    (float)height/width *imageWidth/imageHeight, -1f, 1f);
            Matrix.orthoM(matrix, 0, -1f, 1f, -2,
                    2, -1f, 1f);
        }
//        if (width > height) {
//            // 横屏
//            Matrix.orthoM(matrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f);
//        } else {
//            // 竖屏or正方形
//            Matrix.orthoM(matrix, 0, -1f, 1f, -ratio, ratio, -1f, 1f);
//        }
//        Matrix.rotateM(matrix, 0, 0, 1, 0, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清屏并绘制红色
        // 　第一条语句表示清除颜色设为红色，第二条语句表示实际完成了把整个窗口清除为黑色的任务，glClear（）的唯一参数表示需要被清除的缓冲区。
        GLES20.glClearColor(1f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 激活 program
        GLES20.glUseProgram(program);
        // 绑定纹理，如果使用需要再次glBindTexture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
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
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }


    public void setViewHeight(int viewHeight) {
        this.mViewHeight = viewHeight;
    }

    public void setViewWidth(int viewWidth) {
        this.mViewWidth = viewWidth;
    }
}
