package com.example.sampleopengl;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SampleGLES20Texture {
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORDS_PER_TEXTURE = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private int[] textureHandle = new int[1];
    private FloatBuffer vertexBuffer;
    // GL_TRIANGLE_STRIP rule
    // http://www.matrix44.net/cms/notes/opengl-3d-graphics/understanding-gl_triangle_strip
    private float vertexCoords[] = {
            -1.0f, -1.0f, 0.0f, // Bottom-Left
             1.0f, -1.0f, 0.0f, // Bottom-Right
            -1.0f,  1.0f, 0.0f, // Top-Left
             1.0f,  1.0f, 0.0f  // Top-Right
    };
    private FloatBuffer textureBuffer;
    // Texturing UV coordinates
    // http://ogldev.atspace.co.uk/www/tutorial16/tutorial16.html
    private float textureCoords[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    private final int mProgram;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "attribute vec2 vTexCoord;" +
            "varying vec2 texCoordVar;" +
            "void main() {" +
            "    gl_Position = vPosition;" +
            "    texCoordVar = vTexCoord;" +
            "};";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 texCoordVar;" +
                    "void main() {" +
                    "    vec4 texColor;" +
                    "    vec2 position  = texCoordVar;" +
                    "    if (texCoordVar.x < 0.5 && texCoordVar.y < 0.5) {" +
                    "        position.x = position.x * 2.0;" +
                    "        position.y = position.y * 2.0;" +
                    "        texColor.r = texture2D(texture, position).r;" +
                    "        texColor.g = 0.0;" +
                    "        texColor.b = 0.0;" +
                    "    } else if (texCoordVar.x < 0.5 && texCoordVar.y > 0.5){" +
                    "        position.x = position.x * 2.0;" +
                    "        position.y = (position.y - 0.5) * 2.0;" +
                    "        texColor.r = 0.0;" +
                    "        texColor.g = texture2D(texture, position).g;" +
                    "        texColor.b = 0.0;" +
                    "    } else if (texCoordVar.x > 0.5 && texCoordVar.y > 0.5){" +
                    "        position.x = (position.x - 0.5) * 2.0;" +
                    "        position.y = (position.y - 0.5) * 2.0;" +
                    "        texColor.r = 0.0;" +
                    "        texColor.g = 0.0;" +
                    "        texColor.b = texture2D(texture, position).b;" +
                    "    } else {" +
                    "        position.x = (position.x - 0.5) * 2.0;" +
                    "        position.y = position.y * 2.0;" +
                    "        texColor = texture2D(texture, position);" +
                    "    };" +
                    "    gl_FragColor = texColor;" +
                    "}";

    private static int loadShader(int type, String shaderCode) {
        int shader;

        shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // Check shader compile status
        int compileStatus[] = {GLES20.GL_FALSE};
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if(compileStatus[0] == GLES20.GL_FALSE) {
            int logSize[] = {0};
            GLES20.glGetShaderiv(shader, GLES20.GL_INFO_LOG_LENGTH, logSize, 0);
            if(logSize[0] > 0) {
                String errorLog = GLES20.glGetShaderInfoLog(shader);
                Log.d(SampleGLES20Texture.class.getName() , errorLog);
            }
        }
        return shader;
    }

    public void draw() {
        int mPositionHandle;
        int mTexCoordHandle;

        // Start using shader
        GLES20.glUseProgram(mProgram);

        // Get vertex position "vPosition" and texture coordinate "vTextCoord" handles
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoord");

        // Enable vertex handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Set vertices data of vertex handle
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                (COORDS_PER_VERTEX * BYTES_PER_FLOAT), vertexBuffer);

        // Enable texture handle
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        // Set texture coordinates of texture coordinate handle
        GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEXTURE, GLES20.GL_FLOAT, false,
                (COORDS_PER_TEXTURE * BYTES_PER_FLOAT), textureBuffer);

        // Draw square by GL_TRIANGLE_STRIP
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCoords.length / COORDS_PER_VERTEX);

        // Disable vertex handle
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
    }
    public SampleGLES20Texture(Resources res) {
        // Prepare vertices buffer for square and texture buffer
        // We need square to put texture on it
        ByteBuffer bb;
        bb = ByteBuffer.allocateDirect(vertexCoords.length * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexCoords);
        vertexBuffer.position(0);
        bb = ByteBuffer.allocateDirect(textureCoords.length * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        textureBuffer = bb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        // All about Texture of OpenGL and GLSL Shader language
        // https://www.opengl.org/wiki/Texture#Texture_image_units

        // Create Vertex and Fragment Shaders
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode));
        GLES20.glAttachShader(mProgram, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
        GLES20.glLinkProgram(mProgram);

        // Assign texture unit 0 (GL_TEXTURE0) to fragment shader Sampler2D object "texture"
        // https://www.opengl.org/wiki/Sampler_%28GLSL%29
        GLES20.glUseProgram(mProgram);
        int textureFlower;
        textureFlower = GLES20.glGetUniformLocation(mProgram, "texture");
        GLES20.glUniform1i(textureFlower, 0 /* texture unit 0*/);

        // Create "One" "texture object"
        GLES20.glGenTextures(1, textureHandle, 0);

        // Activate image unit GL_TEXTURE0 and bind "texture object" 0 to GL_TEXTURE0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // Set up filter - GL_LINEAR for better image quality
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Load bitmap and prepare ByteBuffer object of bitmap pixel data
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.flower);
        byte[] buffer = new byte[bitmap.getWidth() * bitmap.getHeight() * 4];
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);
                buffer[(y * bitmap.getWidth() + x) * 4]     = (byte) ((pixel >> 16) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 4 + 1] = (byte) ((pixel >> 8) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 4 + 2] = (byte) ( pixel & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 4 + 3] = (byte) ((pixel >> 24) & 0xFF);
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(buffer.length);
        byteBuffer.put(buffer);
        byteBuffer.position(0);

        // Set up GL_TEXTURE0 pixel data
        // GL_TEXTURE0 has been activated by previous glActiveTexture() call
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, byteBuffer);

        // Recycle bitmap object after use
        bitmap.recycle();
    }
}
