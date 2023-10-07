package com.example.sampleopengl;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SampleGLRenderer implements GLSurfaceView.Renderer {
    private Resources resources;
    //private SampleGLTriangle mTriangle;
    //private SampleGLTexture mTexture;
    private SampleGLES20Texture mTexture20;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set background color
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        //mTriangle = new SampleGLTriangle();
        //mTexture = new SampleGLTexture(gl, resources);
        mTexture20 = new SampleGLES20Texture(resources);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        //mTriangle.draw();
        //mTexture.draw(gl);
        mTexture20.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    public SampleGLRenderer(Resources res) {
        resources = res;
    }
}
