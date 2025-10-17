package com.xinging.opengltest

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyOpenGLSurfaceRender(
    private val drawer: IDrawer,
    private val context: Context
) : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        //设置背景颜色
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)

        drawer.prepare(context)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        drawer.setScreenSize(width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        drawer.draw()
    }
}