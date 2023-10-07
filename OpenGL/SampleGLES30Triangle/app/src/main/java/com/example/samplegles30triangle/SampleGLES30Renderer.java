package com.example.samplegles30triangle;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class SampleGLES30Renderer implements GLSurfaceView.Renderer {
    private Resources resources;
    private SampleGLES30Triangle mTriangle;
    int screenHeight = 0;
    int screenWidth = 0;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set background color
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        mTriangle = new SampleGLES30Triangle();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        mTriangle.Draw(screenWidth, screenHeight);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenHeight = height;
        screenWidth = width;
    }

    public SampleGLES30Renderer(Resources res) {
        resources = res;
    }
}
