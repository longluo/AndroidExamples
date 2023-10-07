package com.example.sampleopengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SampleGLTriangle {
    private static final int COORDS_PER_VERTEX = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexBuffer;

    static float triangleCoords[] = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.3f, 0.0f,
            0.5f, -0.3f, 0.0f
    };

    private final int mProgram;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "    gl_Position = vPosition;" +
                    "};";

    float color[] = {0.6f, 0.7f, 0.2f, 1.0f};
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private static int loadShader(int type, String shaderCode) {
        int shader;

        shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void draw() {
        int mPositionHandle;
        int mColorHandle;

        // Start using shader
        GLES20.glUseProgram(mProgram);

        // Get vertex "vPosition" handle
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable vertex handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Set vertices data of vertex handle
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                (COORDS_PER_VERTEX * BYTES_PER_FLOAT), vertexBuffer);

        // Get fragment shader variable "vColor" handle
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color of variable "vColor", total "1" data, starting from offset "0"
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw triangle from first "0" vertex, total "COORDS_PER_VERTEX" vertices
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, COORDS_PER_VERTEX);

        // Disable vertex handle
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public SampleGLTriangle() {
        ByteBuffer bb;

        // Prepare Vertex buffer
        bb = ByteBuffer.allocateDirect(triangleCoords.length * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        // Create Vertex and Fragment Shaders
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode));
        GLES20.glAttachShader(mProgram, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
        GLES20.glLinkProgram(mProgram);
    }
}
