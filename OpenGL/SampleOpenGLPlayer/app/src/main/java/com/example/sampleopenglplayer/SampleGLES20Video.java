package com.example.sampleopenglplayer;

import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SampleGLES20Video {

    int surfaceWidth;

    int surfaceHeight;

    String screenshotName;


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
            -1.0f, 1.0f, 0.0f, // Top-Left
            1.0f, 1.0f, 0.0f  // Top-Right
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

    // For video streaming from SurfaceTexture, GL_OES_EGL_image_external extension must be declared
    // and use samplerExternalOES instead of Sample2D
    // http://developer.android.com/reference/android/graphics/SurfaceTexture.html
    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "uniform samplerExternalOES texture;" +
                    "varying vec2 texCoordVar;" +
                    "void main() {" +
                    "    gl_FragColor = texture2D(texture, texCoordVar);" +
                    "}";

    private static int loadShader(int type, String shaderCode) {
        int shader;

        shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // Check shader compile status
        int compileStatus[] = {GLES20.GL_FALSE};
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == GLES20.GL_FALSE) {
            int logSize[] = {0};
            GLES20.glGetShaderiv(shader, GLES20.GL_INFO_LOG_LENGTH, logSize, 0);
            if (logSize[0] > 0) {
                String errorLog = GLES20.glGetShaderInfoLog(shader);
                Log.d(SampleGLES20Video.class.getName(), errorLog);
            }
        }

        return shader;
    }

    // Create bitmap of video returned by glReadPixels()
    // Must be called in context of onFrameDraw()
    private void createBitmap(String fileName) {
        Bitmap videoFrame;
        int size = surfaceWidth * surfaceHeight;
        ByteBuffer buf = ByteBuffer.allocateDirect(size * 4);
        buf.order(ByteOrder.nativeOrder());
        GLES20.glReadPixels(0, 0, surfaceWidth, surfaceHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
        int data[] = new int[size];
        buf.asIntBuffer().get(data);

        videoFrame = Bitmap.createBitmap(surfaceWidth, surfaceHeight, Bitmap.Config.ARGB_8888);
        videoFrame.setPixels(data, size - surfaceWidth, -surfaceWidth, 0, 0, surfaceWidth, surfaceHeight);

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(fileName);
            videoFrame.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        videoFrame.recycle();
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

        if (screenshotName != null) {
            createBitmap(screenshotName);
            screenshotName = null;
        }
    }

    public SampleGLES20Video() {
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

        // Assign GL_TEXTURE0 to fragment shader Sampler2D object "texture"
        GLES20.glUseProgram(mProgram);
        int texture;
        texture = GLES20.glGetUniformLocation(mProgram, "texture");
        GLES20.glUniform1i(texture, 0 /* texture unit 0 */);

        // Create "One" "texture object"
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Use GL_TEXTURE_EXTERNAL_OES instead of GL_TEXTURE0 for video stream comes from SurfaceTexture
        // http://developer.android.com/reference/android/graphics/SurfaceTexture.html
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);

        // Set up filter - GL_LINEAR for better image quality
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }

    public int getTextureHandle() {
        return textureHandle[0];
    }

    public void setResolution(int w, int h) {
        surfaceWidth = w;
        surfaceHeight = h;
    }

    public void screenshot(String fileName) {
        screenshotName = fileName;
    }
}
