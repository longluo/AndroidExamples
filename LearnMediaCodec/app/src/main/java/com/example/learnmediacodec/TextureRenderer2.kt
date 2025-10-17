package com.example.learnmediacodec

import android.opengl.EGL14
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * Copyright (c) 2019 by Roman Sisik. All rights reserved.
 */
class TextureRenderer2 {

    private val vertexShaderCode =
        """
        precision highp float;
        attribute vec3 vertexPosition;
        attribute vec2 uvs;
        varying vec2 varUvs;
        uniform mat4 texMatrix;
        uniform mat4 mvp;
      
        void main()
        {
            varUvs = (texMatrix * vec4(uvs.x, uvs.y, 0, 1.0)).xy;
            gl_Position = mvp * vec4(vertexPosition, 1.0);
        }
        """

    private val fragmentShaderCode =
        """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        
        varying vec2 varUvs;
        uniform samplerExternalOES texSampler;
        
        void main()
        {
            // Convert to greyscale here
            vec4 c = texture2D(texSampler, varUvs);
            float gs = 0.299*c.r + 0.587*c.g + 0.114*c.b;
            gl_FragColor = vec4(gs, gs, gs, c.a);
        }
        """

    private var vertices = floatArrayOf(
        // x, y, z, u, v
        -1.0f, -1.0f, 0.0f, 0f, 0f,
        -1.0f, 1.0f, 0.0f, 0f, 1f,
        1.0f, 1.0f, 0.0f, 1f, 1f,
        1.0f, -1.0f, 0.0f, 1f, 0f
    )

    private var indices = intArrayOf(
        2, 1, 0, 0, 3, 2
    )

    private var program: Int
    private var vertexHandle: Int = 0
    private var bufferHandles = IntArray(2)
    private var uvsHandle: Int = 0
    private var texMatrixHandle: Int = 0
    private var mvpHandle: Int = 0
    private var samplerHandle: Int = 0
    private val textureHandles = IntArray(1)

    val texId: Int
        get() = textureHandles[0]

    private var vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(vertices)
            position(0)
        }
    }

    private var indexBuffer: IntBuffer = ByteBuffer.allocateDirect(indices.size * 4).run {
        order(ByteOrder.nativeOrder())
        asIntBuffer().apply {
            put(indices)
            position(0)
        }
    }

    init {
        // Create program
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {

            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)

            vertexHandle = GLES20.glGetAttribLocation(it, "vertexPosition")
            uvsHandle = GLES20.glGetAttribLocation(it, "uvs")
            texMatrixHandle = GLES20.glGetUniformLocation(it, "texMatrix")
            mvpHandle = GLES20.glGetUniformLocation(it, "mvp")
            samplerHandle = GLES20.glGetUniformLocation(it, "texSampler")
        }

        // Initialize buffers
        GLES20.glGenBuffers(2, bufferHandles, 0)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandles[0])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.size * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW)

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandles[1])
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.size * 4, indexBuffer, GLES20.GL_DYNAMIC_DRAW)

        // Init texture that will receive decoded frames
        GLES20.glGenTextures(1, textureHandles, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandles[0])
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE)

        // Ensure I can draw transparent stuff that overlaps properly
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun draw(viewportWidth: Int, viewportHeight: Int, texMatrix:FloatArray, mvpMatrix: FloatArray) {
        checkGlError("before draw")
        Log.d("DecodeEditEncodeActivity", "before draw")
        if(EGL14.eglGetCurrentContext() == EGL14.EGL_NO_CONTEXT){
            throw RuntimeException("no egl context")
        }
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        checkGlError("glClear")
        Log.d("DecodeEditEncodeActivity", "glClear")

        GLES20.glUseProgram(program)

        // Pass transformations to shader
        GLES20.glUniformMatrix4fv(texMatrixHandle, 1, false, texMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)

        // Prepare buffers with vertices and indices & draw
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandles[0])
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandles[1])

        GLES20.glEnableVertexAttribArray(vertexHandle)
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 4 * 5, 0)

        GLES20.glEnableVertexAttribArray(uvsHandle)
        GLES20.glVertexAttribPointer(uvsHandle, 2, GLES20.GL_FLOAT, false, 4 * 5, 3 * 4)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_INT, 0)
        checkGlError("glDrawElements")
    }

    fun checkGlError(op: String) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Log.e(
                "TextureRenderer2",
                "$op: glError $error"
            )
            throw java.lang.RuntimeException("$op: glError $error")
        }
    }
}