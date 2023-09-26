package com.myl.mediacodedemo.encode.demo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GLRecordView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : GLSurfaceView(context, attrs) {
    companion object{
        private const val TAG = "CameraGLView"
    }

}