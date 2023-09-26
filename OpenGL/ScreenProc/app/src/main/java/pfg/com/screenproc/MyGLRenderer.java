/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pfg.com.screenproc;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pfg.com.screenproc.objects.Square;
import pfg.com.screenproc.objects.Triangle;
import pfg.com.screenproc.util.MatrixHelper;
import pfg.com.screenproc.util.MyLog;
import pfg.com.screenproc.util.ShaderHelper;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Triangle mTriangle;
    private Square mSquare;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float mAngle;

    // my code start
    // 坐标

    float[] tableVerticesWithTriangle = {
            /*// Triangle 1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,

            // Triangle 2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,*/

            /*// Triangle Fan
            0, 0,
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            -0.5f, -0.5f,


            // Line
            -0.5f, 0f,
            0.5f, 0f,

            //Mallets
            0f, -0.25f,
            0f, 0.25f,

            // 边框
            -0.505f, -0.505f,
            0.505f, -0.505f,
            0.505f, 0.505f,
            -0.505f, 0.505f*/

            // X,Y,R,G,B
            // Triangle Fan
            0, 0, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,


            // Line
            -0.5f, 0f, 1.0f, 0f, 0f,
            0.5f, 0f, 1.0f, 0f, 0f,

            //Mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f,

            // 边框
            -0.505f, -0.805f, 0.5f, 0f, 0.5f,
            0.505f, -0.805f, 0.5f, 0f, 0.5f,
            0.505f, 0.805f, 0.5f, 0f, 0.5f,
            -0.505f, 0.805f, 0.5f, 0f, 0.5f

            /*// X,Y,Z,W,R,G,B
            // Triangle Fan
               0,    0,   0, 1.5f,   1.0f, 1.0f, 1.0f,
            -0.5f, -0.8f, 0,   1f,   0.7f, 0.7f, 0.7f,
             0.5f, -0.8f, 0,   1f,   0.7f, 0.7f, 0.7f,
             0.5f,  0.8f, 0,   2f,   0.7f, 0.7f, 0.7f,
            -0.5f,  0.8f, 0,   2f,   0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0,   1f,   0.7f, 0.7f, 0.7f,


            // Line
            -0.5f,    0f, 0, 1.5f,   1.0f,   0f,   0f,
             0.5f,    0f, 0, 1.5f,   1.0f,   0f,   0f,

            //Mallets
               0f, -0.4f, 0, 1.25f,    0f,   0f,   1f,
               0f,  0.4f, 0, 1.75f,    1f,   0f,   0f,*/
    };

    float[] translationProjection1 = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };

    float[] translationProjection2 = new float[]{
            1, 0, 0, 0.2f,
            0, 1, 0, 0.4f,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            // "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
                    "attribute vec4 aColor;" +
                    "varying vec4 v_Color;" +
                    "uniform mat4 u_Matrix;" +
                    //"uniform mat4 u_Translation;"+
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    //"  gl_Position = u_Matrix * vPosition * u_Translation;" +
                    "  gl_Position = u_Matrix * vPosition;" +
                    "  gl_PointSize = 10.0;" +
                    "  v_Color = aColor;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    //"uniform vec4 vColor;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    //"  gl_FragColor = vColor;" +
                    "  gl_FragColor = v_Color;" +
                    "}";

    private final static String V_COLOR = "vColor";
    private int vColorLocation;
    private final static String V_POSITION = "vPosition";
    private int vPositionLocation;
    private final static String A_COLOR = "aColor";
    private int aColorLocation;
    private final static String u_Translation = "u_Translation";
    private int uTranslationLocation;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private final static String u_Matrix = "u_Matrix";
    private int uMatrixLocation;

    private final static int BYTES_PER_FLOAT = 4;
    private final static int POSITION_COMPONENT_COUNT = 2;
    private final static int COLOR_COMPONENT_COUNT = 3;
    private final static int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    int mWidth;
    int mHeight;
    int number = 0;

    private FloatBuffer mVerticeData;
    private FloatBuffer mLineData;
    private int vertexShaderId;
    private int fragmentShaderId;
    private int programId;
    // my code end

    public MyGLRenderer(Context context) {

    }


    // onSurfaceCreate onSurfaceChanged onFrameDraw all in render thread.

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        MyLog.logd(TAG, "onSurfaceCreated");

        // Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mTriangle = new Triangle();
        mSquare = new Square();

        // my code start
        // main thread, need remove to render thread or not?
        mVerticeData = ByteBuffer.allocateDirect(tableVerticesWithTriangle.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticeData.put(tableVerticesWithTriangle);
        mVerticeData.position(0);

        vertexShaderId = ShaderHelper.compileVertexShader(vertexShaderCode);
        fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShaderCode);
        programId = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);
        if (ShaderHelper.validateProgram(programId)) {
            GLES30.glUseProgram(programId);
            checkGlError("glUseProgram");
        }

        /*vColorLocation = GLES30.glGetUniformLocation(programId, V_COLOR);
        checkGlError("glGetUniformLocation");*/
        vPositionLocation = GLES30.glGetAttribLocation(programId, V_POSITION);
        checkGlError("glGetAttribLocation");

        GLES30.glVertexAttribPointer(vPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, mVerticeData);
        checkGlError("glVertexAttribPointer");
        GLES30.glEnableVertexAttribArray(vPositionLocation);
        checkGlError("glEnableVertexAttribArray");

        // 设置颜色值的初始位置
        mVerticeData.position(POSITION_COMPONENT_COUNT);
        aColorLocation = GLES30.glGetAttribLocation(programId, A_COLOR);
        checkGlError("glGetAttribLocation");
        GLES30.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES30.GL_FLOAT, false, STRIDE, mVerticeData);
        checkGlError("glVertexAttribPointer");
        GLES30.glEnableVertexAttribArray(aColorLocation);
        checkGlError("glEnableVertexAttribArray");

        uMatrixLocation = GLES30.glGetUniformLocation(programId, u_Matrix);
        checkGlError("glGetUniformLocation");
        /*uTranslationLocation = GLES30.glGetUniformLocation(programId, u_Translation);
        checkGlError("glGetUniformLocation");*/
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        MyLog.logd(TAG, "onDrawFrame");
        /*float[] scratch = new float[16];

        // Draw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw square
        mSquare.draw(mMVPMatrix);

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f); //沿着Z轴旋转

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        mTriangle.draw(scratch);*/

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        checkGlError("glDrawArrays:glClear");
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        checkGlError("glUniformMatrix4fv:projectionMatrix");

        /*// 平移
        number++;
        if (number == 2) {
            GLES30.glUniformMatrix4fv(uTranslationLocation, 1, false, translationProjection2, 0);
            checkGlError("glUniformMatrix4fv:translationProjection2");
        } else {
            GLES30.glUniformMatrix4fv(uTranslationLocation, 1, false, translationProjection1, 0);
            checkGlError("glUniformMatrix4fv:translationProjection1");
        }*/


        // draw 桌子
        //GLES30.glUniform4f(vColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        //checkGlError("glDrawArrays:glUniform4f");
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6);
        checkGlError("glDrawArrays:GL_TRIANGLES");

        // draw 中间线
        // RGBA
        //GLES30.glUniform4f(vColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        //checkGlError("glDrawArrays:glUniform4f");
        GLES30.glDrawArrays(GLES30.GL_LINES, 6, 2);
        checkGlError("glDrawArrays:GL_LINES");

        // draw 球
        // blue
        //GLES30.glUniform4f(vColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        //checkGlError("glDrawArrays:glUniform4f");
        GLES30.glDrawArrays(GLES30.GL_POINTS, 8, 1);
        checkGlError("glDrawArrays:GL_POINTS");

        // red
        //GLES30.glUniform4f(vColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        //checkGlError("glDrawArrays:glUniform4f");
        GLES30.glDrawArrays(GLES30.GL_POINTS, 9, 1);
        checkGlError("glDrawArrays:GL_POINTS");

        /*// 画边框
        //GLES30.glUniform4f(vColorLocation, 0.5f, 0f, 0.5f, 1.0f);
        //checkGlError("glDrawArrays:glUniform4f");
        // 设置线的粗细
        GLES30.glLineWidth(4);
        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 10, 4);
        checkGlError("glDrawArrays:GL_LINES");*/

        // 保留帧截图
        try {
            saveFrame(new File(Environment.getExternalStorageDirectory(),
                    "test" + number + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        MyLog.logd(TAG, "onSurfaceChanged width:" + width + " height:" + height);
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES30.glViewport(0, 0, width, height);
        checkGlError("glDrawArrays:glViewport");
        mWidth = width;
        mHeight = height;
        // Z轴值-1到-10,但是默认Z是0，因此需要把Z平移到这个范围,否则看不到任何画像
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        // 初始化单位矩阵
        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);

        // 沿着Z轴平移-2.5f, 这样在-1到-10范围内所以画面就可见了。
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        // 沿着X轴旋转
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        /*float aspectRatio = (width > height) ? (float) width / height : (float) height / width;
        if(width > height) {
            // landscape
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, -1, 1);
        } else {
            // portial
            Matrix.orthoM(projectionMatrix, 0, -1, 1,-aspectRatio, aspectRatio, -1, 1);
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < projectionMatrix.length; i++) {
            builder.append(projectionMatrix[i]+" ");
            if((i+1)%4 == 0) {
                builder.append("\n");
            }
        }
        MyLog.logd(TAG, "onSurfaceChanged projectionMatrix:\n"+builder.toString());*/

        //float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        // Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public void saveFrame(File file) throws IOException {
        /*if (!mEglCore.isCurrent(mEGLSurface)) {
            throw new RuntimeException("Expected EGL context/surface is not current");
        }*/

        // glReadPixels fills in a "direct" ByteBuffer with what is essentially big-endian RGBA
        // data (i.e. a byte of red, followed by a byte of green...).  While the Bitmap
        // constructor that takes an int[] wants little-endian ARGB (blue/red swapped), the
        // Bitmap "copy pixels" method wants the same format GL provides.
        //
        // Ideally we'd have some way to re-use the ByteBuffer, especially if we're calling
        // here often.
        //
        // Making this even more interesting is the upside-down nature of GL, which means
        // our output will look upside down relative to what appears on screen if the
        // typical GL conventions are used.

        String filename = file.toString();

        /*int width = getWidth();
        int height = getHeight();*/
        int width = mWidth;
        int height = mHeight;

        ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glReadPixels(0, 0, width, height,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
        checkGlError("glReadPixels");
        buf.rewind();

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filename));
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buf);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bmp.recycle();
        } catch (FileNotFoundException e) {

        } finally {
            if (bos != null) try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Saved " + width + "x" + height + " frame as '" + filename + "'");
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     * <p>
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            MyLog.loge(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

}