package com.example.samplegles30triangle;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SampleGLES30Triangle {
    private static final int BYTES_PER_FLOAT = 4;

    private final int mProgram;
    private int shader;
    private FloatBuffer vertexBuffer;

    final float vVertices[] = {0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f};


    private final String vertexShaderCode =
            "#version 300 es\n" +
                    "layout(location = 0) in vec4 vPosition;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   gl_Position = vPosition;\n" +
                    "}\n";

    private final String fragmentShaderCode =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   fragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
                    "}\n";

    private int loadShader(int type, String shaderCode) {
        int sh;
        int compileStatus[] = {GLES30.GL_FALSE};
        sh = GLES30.glCreateShader(type);
        GLES30.glShaderSource(sh, shaderCode);
        GLES30.glCompileShader(sh);
        GLES30.glGetShaderiv(sh, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == GLES30.GL_FALSE) {
            int logSize[] = {0};
            GLES30.glGetShaderiv(sh, GLES30.GL_INFO_LOG_LENGTH, logSize, 0);
            if (logSize[0] > 0) {
                String errorLog = GLES30.glGetShaderInfoLog(sh);
                Log.d(SampleGLES30Triangle.class.getName(), errorLog);
            }
        }
        return sh;
    }

    public void Draw(int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }

    public SampleGLES30Triangle() {
        ByteBuffer bb;

        // Prepare Vertex buffer
        bb = ByteBuffer.allocateDirect(vVertices.length * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vVertices);
        vertexBuffer.position(0);

        mProgram = GLES30.glCreateProgram();
        shader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        GLES30.glAttachShader(mProgram, shader);
        shader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        GLES30.glAttachShader(mProgram, shader);
        GLES30.glLinkProgram(mProgram);
    }
}
