package com.myl.mediacodedemo.encode.gl.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import com.myl.mediacodedemo.encode.gl.GLImageFilter
import com.myl.mediacodedemo.utils.OpenGLUtils.getShaderFromAssets

/**
 * 外部纹理(OES纹理)输入
 */
class GLImageOESInputFilter(
    val context: Context,
    vertexShader: String? = getShaderFromAssets(context, "shader/base/vertex_oes_input.glsl"),
    fragmentShader: String? = getShaderFromAssets(context, "shader/base/fragment_oes_input.glsl")
) : GLImageFilter(vertexShader!!, fragmentShader!!) {
    private var mTransformMatrixHandle = 0
    private var mTransformMatrix: FloatArray? = null

    override fun initProgramHandle() {
        super.initProgramHandle()
        mTransformMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle, "transformMatrix")
    }

    override val textureType: Int
        get() = GLES11Ext.GL_TEXTURE_EXTERNAL_OES

    override fun onDrawFrameBegin() {
        super.onDrawFrameBegin()
        GLES30.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, mTransformMatrix, 0)
    }

    /**
     * 设置SurfaceTexture的变换矩阵
     * @param transformMatrix
     */
    fun setTextureTransformMatrix(transformMatrix: FloatArray) {
        mTransformMatrix = transformMatrix
    }
}