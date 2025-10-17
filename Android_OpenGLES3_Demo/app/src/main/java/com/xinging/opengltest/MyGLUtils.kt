package com.xinging.opengltest

import android.opengl.GLES30
import android.util.Log

class MyGLUtils {

    companion object {
        fun checkGlError(op: String) {
            var error: Int
            while (GLES30.glGetError().also { error = it } != GLES30.GL_NO_ERROR) {
                Log.e(
                    "GLES30Error",
                    "$op: glError $error"
                )
                throw java.lang.RuntimeException("$op: glError $error")
            }
        }
    }
}