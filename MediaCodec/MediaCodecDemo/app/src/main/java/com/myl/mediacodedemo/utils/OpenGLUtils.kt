package com.myl.mediacodedemo.utils

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object OpenGLUtils {

    private const val TAG = "OpenGLUtils"
    private const val SIZEOF_FLOAT = 4

    // 从初始化失败
    const val GL_NOT_INIT = -1

    // 没有Texture
    const val GL_NOT_TEXTURE = -1

    /**
     * 创建program
     * @param vertexSource
     * @param fragmentSource
     * @return
     */
    @Synchronized
    fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentShader == 0) {
            return 0
        }
        var program = GLES30.glCreateProgram()
        checkGlError("glCreateProgram")
        if (program == 0) {
            Log.e(OpenGLUtils.TAG, "Could not create program")
        }
        GLES30.glAttachShader(program, vertexShader)
        checkGlError("glAttachShader")
        GLES30.glAttachShader(program, fragmentShader)
        checkGlError("glAttachShader")
        GLES30.glLinkProgram(program)
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES30.GL_TRUE) {
            Log.e(TAG, "Could not link program: ")
            Log.e(TAG, GLES30.glGetProgramInfoLog(program))
            GLES30.glDeleteProgram(program)
            program = 0
        }
        if (vertexShader > 0) {
            GLES30.glDetachShader(program, vertexShader)
            GLES30.glDeleteShader(vertexShader)
        }
        if (fragmentShader > 0) {
            GLES30.glDetachShader(program, fragmentShader)
            GLES30.glDeleteShader(fragmentShader)
        }
        return program
    }

    /**
     * 加载Shader
     * @param shaderType
     * @param source
     * @return
     */
    private fun loadShader(shaderType: Int, source: String?): Int {
        var shader = GLES30.glCreateShader(shaderType)
        checkGlError("glCreateShader type=$shaderType")
        GLES30.glShaderSource(shader, source)
        GLES30.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader $shaderType:")
            Log.e(TAG, " " + GLES30.glGetShaderInfoLog(shader))
            GLES30.glDeleteShader(shader)
            shader = 0
        }
        return shader
    }

    /**
     * 创建Sampler2D的Framebuffer 和 Texture
     * @param frameBuffer
     * @param frameBufferTexture
     * @param width
     * @param height
     */
    fun createFrameBuffer(
        frameBuffer: IntArray, frameBufferTexture: IntArray,
        width: Int, height: Int
    ) {
        GLES30.glGenFramebuffers(frameBuffer.size, frameBuffer, 0)
        GLES30.glGenTextures(frameBufferTexture.size, frameBufferTexture, 0)
        for (i in frameBufferTexture.indices) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, frameBufferTexture[i])
            GLES30.glTexImage2D(
                GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null
            )
            GLES30.glTexParameterf(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat()
            )
            GLES30.glTexParameterf(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR.toFloat()
            )
            GLES30.glTexParameterf(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES30.glTexParameterf(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer[i])
            GLES30.glFramebufferTexture2D(
                GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, frameBufferTexture[i], 0
            )
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        }
        checkGlError("createFrameBuffer")
    }


    /**
     * 创建FloatBuffer
     * @param coords
     * @return
     */
    fun createFloatBuffer(coords: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(coords.size * SIZEOF_FLOAT)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(coords)
        fb.position(0)
        return fb
    }

    /**
     * 检查是否出错
     * @param op
     */
    fun checkGlError(op: String) {
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            val msg = op + ": glError 0x" + Integer.toHexString(error)
            Log.e(TAG, msg)
        }
    }

    /**
     * 从Assets文件夹中读取shader字符串
     * @param context
     * @param path      shader相对路径
     * @return
     */
    fun getShaderFromAssets(context: Context, path: String?): String? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.resources.assets.open(path!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return getShaderStringFromStream(inputStream)
    }

    /**
     * 从输入流中读取shader字符创
     * @param inputStream
     * @return
     */
    private fun getShaderStringFromStream(inputStream: InputStream?): String? {
        if (inputStream == null) {
            return null
        }
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val builder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line).append("\n")
            }
            reader.close()
            return builder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * 删除纹理
     * @param texture
     */
    fun deleteTexture(texture: Int) {
        val textures = IntArray(1)
        textures[0] = texture
        GLES30.glDeleteTextures(1, textures, 0)
    }

    /**
     * 创建OES 类型的Texture
     * @return
     */
    fun createOESTexture(): Int {
        return createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
    }

    /**
     * 创建Texture对象
     * @param textureType
     * @return
     */
    fun createTexture(textureType: Int): Int {
        val textures = IntArray(1)
        GLES30.glGenTextures(1, textures, 0)
        checkGlError("glGenTextures")
        val textureId = textures[0]
        GLES30.glBindTexture(textureType, textureId)
        checkGlError("glBindTexture $textureId")
        GLES30.glTexParameterf(
            textureType,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST.toFloat()
        )
        GLES30.glTexParameterf(
            textureType,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        GLES30.glTexParameterf(
            textureType,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES30.glTexParameterf(
            textureType,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE.toFloat()
        )
        checkGlError("glTexParameter")
        return textureId
    }
}