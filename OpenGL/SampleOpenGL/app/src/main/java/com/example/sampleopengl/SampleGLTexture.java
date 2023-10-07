package com.example.sampleopengl;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class SampleGLTexture {
    private static final int VERTEX_PER_COORDS = 3;
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
    // http://www.opengl-tutorial.org/beginners-tutorials/tutorial-5-a-textured-cube/
    private float textureCoords[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    public void draw(GL10 gl) {
        // Enable Vertex and Texture arrays
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // Set white background
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glVertexPointer(VERTEX_PER_COORDS, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexCoords.length / VERTEX_PER_COORDS);

        // Disable Vertex and Texture arrays
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public SampleGLTexture(GL10 gl, Resources res) {
        // Enable Texture 2D feature
        gl.glEnable(GL10.GL_TEXTURE_2D);

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

        // Create and bind texture
        gl.glGenTextures(1, textureHandle, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureHandle[0]);

        // Set up filter - GL_LINEAR for better image quality
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        // Load bitmap and set up texture with the bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.flower);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }
}
