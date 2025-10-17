package com.xinging.opengltest

import android.content.Context
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TriangleDrawer : AbstractDrawer() {
    companion object {
        val vertexShaderSource =
            """
            #version 300 es
            layout (location = 0) in vec3 aPos;
            void main()
            {
                gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
            }
            """.trimIndent()

        val fragmentShaderSource = """
            #version 300 es
            precision mediump float;
            out vec4 FragColor;
            void main()
            {
                FragColor = vec4(0.0f, 0.5f, 0.2f, 1.0f);
            }
            """.trimIndent()
    }
    private val sharer = Shader(
        vertexShaderSource,
        fragmentShaderSource
    )
    private val vertices = floatArrayOf(
        -0.5f, -0.5f, 0.0f, // left
        0.5f, -0.5f, 0.0f, // right
        0.0f, 0.5f, 0.0f  // top
    )

    val vaos: IntArray = intArrayOf(0)
    val vbos: IntArray = intArrayOf(0)

    override fun prepare(context: Context) {
        sharer.prepareShaders()

        // prepare vbo data
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // generate vao and vbo
        GLES30.glGenVertexArrays(1, vaos, 0)
        GLES30.glGenBuffers(1, vbos, 0)

        GLES30.glBindVertexArray(vaos[0])

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbos[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            Float.SIZE_BYTES * vertices.size,
            vertexBuffer,
            GLES30.GL_STATIC_DRAW
        )

        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT, false,
            3 * Float.SIZE_BYTES, 0
        )
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

    override fun draw(){
        GLES30.glViewport(0, 0, screenWidth, screenHeight)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glUseProgram(sharer.id)
        GLES30.glBindVertexArray(vaos[0])

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)

        GLES30.glBindVertexArray(0)
    }
}