package pfg.com.screenproc;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pfg.com.screenproc.objects.Mallet;
import pfg.com.screenproc.objects.Table;
import pfg.com.screenproc.programs.ColorShaderProgram;
import pfg.com.screenproc.programs.TextureShaderProgram;
import pfg.com.screenproc.util.MatrixHelper;
import pfg.com.screenproc.util.MyLog;
import pfg.com.screenproc.util.TextureHelper;

import static android.opengl.GLES30.*;
import static pfg.com.screenproc.MyGLRenderer.checkGlError;
import static pfg.com.screenproc.util.Constants.simple_fragment_shader;
import static pfg.com.screenproc.util.Constants.simple_vertex_shader;
import static pfg.com.screenproc.util.Constants.texture_fragment_shader;
import static pfg.com.screenproc.util.Constants.texture_vertex_shader;

/**
 * Created by FPENG3 on 2018/7/25.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer{

    private final static String TAG = "AirHockeyRenderer";

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        table = new Table();
        mallet = new Mallet();

        textureShaderProgram = new TextureShaderProgram(context, texture_vertex_shader, texture_fragment_shader);

        colorShaderProgram = new ColorShaderProgram(context, simple_vertex_shader, simple_fragment_shader);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        MyLog.logd(TAG, "onSurfaceChanged width:"+width+" height:"+height);
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES30.glViewport(0, 0, width, height);
        checkGlError("glDrawArrays:glViewport");

        // Z轴值-1到-10,但是默认Z是0，因此需要把Z平移到这个范围,否则看不到任何画像
        // (1) 获得projectionMatrix(自己的方法)
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

        // (2) 获得projectionMatrix(系统的方法)
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float) width / (float) height, 1f, 10f);
        // 初始化单位矩阵
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

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(projectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

    }
}
