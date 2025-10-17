package com.xinging.opengltest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer


class FBODrawer : AbstractDrawer() {

    companion object {
        val vertexShaderSource =
            """
            #version 300 es
            layout(location = 0) in vec3 a_position;
            layout(location = 1) in vec2 a_texcoord;
            
            out vec2 v_texcoord;
            
            void main()
            {
                gl_Position = vec4(a_position, 1.0);
                v_texcoord = a_texcoord;
            }
            """.trimIndent()

        val fragmentShaderSource =
            """
            #version 300 es
            precision mediump float;
            
            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                fragColor = texture(texture0, v_texcoord);
            }
            """.trimIndent()

        val fboFragmentShaderSource =
            """
            #version 300 es
            precision mediump float;
            
            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                vec4 tempColor = texture(texture0, v_texcoord);
                float gray = 0.299*tempColor.a + 0.587*tempColor.g + 0.114*tempColor.b;
                fragColor = vec4(vec3(gray), 1.0);
            }
            """.trimIndent()
    }

    private val shader = Shader(
        vertexShaderSource,
        fragmentShaderSource
    )

    private val fboShader = Shader(
        vertexShaderSource,
        fboFragmentShaderSource
    )

    private val vertices = floatArrayOf(
        // positions       // texture coords
        1f, 1f, 0.0f, 1.0f, 1.0f,   // top right
        1f, -1f, 0.0f, 1.0f, 0.0f,  // bottom right
        -1f, -1f, 0.0f, 0.0f, 0.0f, // bottom left
        -1f, 1f, 0.0f, 0.0f, 1.0f   // top left
    )

    private val indices = intArrayOf(
        0, 1, 3, // first triangle
        1, 2, 3  // second triangle
    )

    val vaos = IntBuffer.allocate(1)
    val vbos = IntBuffer.allocate(1)
    val ebo = IntBuffer.allocate(1)
    val imageTexIds = IntBuffer.allocate(1)
    val fboTexIds = IntBuffer.allocate(1)
    val fbo = IntBuffer.allocate(1)
    var imageWidth = 0
    var imageHeight = 0
    var context: Context? = null
    override fun prepare(context: Context) {
        this.context = context
        shader.prepareShaders()
        fboShader.prepareShaders()

        // generate vao, vbo, ebo
        val vertexBuffer = ByteBuffer
            .allocateDirect(vertices.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }

        // generate vao, vbo and ebo
        GLES30.glGenVertexArrays(1, vaos)
        GLES30.glGenBuffers(1, vbos)
        GLES30.glGenBuffers(1, ebo)
        MyGLUtils.checkGlError("gen vertex array and buffer")

        // bind and set vao
        GLES30.glBindVertexArray(vaos[0])

        // set vbo data
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbos[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            Float.SIZE_BYTES * vertices.size,
            vertexBuffer,
            GLES30.GL_STATIC_DRAW
        )
        MyGLUtils.checkGlError("glBufferData")

        // set ebo data
        val indexBuffer = ByteBuffer
            .allocateDirect(indices.size * Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .apply {
                put(indices)
                position(0)
            }
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            Int.SIZE_BYTES * indices.size,
            indexBuffer,
            GLES30.GL_STATIC_DRAW
        )
        MyGLUtils.checkGlError("glBufferData for indices")

        // set vao attribute
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(
            1,
            2,
            GLES30.GL_FLOAT,
            false,
            5 * Float.SIZE_BYTES,
            3 * Float.SIZE_BYTES
        )
        GLES30.glEnableVertexAttribArray(1)

        // unbind vao
        GLES30.glBindVertexArray(0)

        // prepare texture
        // generate texture id
        GLES30.glGenTextures(imageTexIds.capacity(), imageTexIds)
        MyGLUtils.checkGlError("glGenTextures")
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTexIds[0])
        MyGLUtils.checkGlError("glBindTexture")

        // set filtering
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        MyGLUtils.checkGlError("glTexParameteri")

        // set texture image data
        val options = BitmapFactory.Options()
        options.inScaled = false   // No pre-scaling
        var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.lye, options)
        imageWidth = bitmap.width
        imageHeight = bitmap.height

        // Flip the bitmap vertically
        val matrix = android.graphics.Matrix()
        matrix.preScale(1.0f, -1.0f)
        bitmap = android.graphics.Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            false
        )

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        MyGLUtils.checkGlError("texImage2D")
        bitmap.recycle()

        // unbind texture
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        // prepare fbo

        // generate fbo texture id and then config texture
        GLES30.glGenTextures(1, fboTexIds)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTexIds[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE)

        // generate fbo id and config fbo
        // 创建 FBO
        GLES30.glGenFramebuffers(1, fbo);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fbo[0])
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTexIds[0])
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, fboTexIds[0], 0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, imageWidth, imageHeight, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null)
        MyGLUtils.checkGlError("configure fbo")
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE)
    }

    private var done = false
    override fun draw() {
        // first, fbo off screen rendering
        GLES30.glViewport(0, 0, imageWidth, imageHeight)

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fbo[0])
        fboShader.use()
        fboShader.setInt("texture0", 0)
        GLES30.glBindVertexArray(vaos[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTexIds[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size, GLES30.GL_UNSIGNED_INT, 0)

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        GLES30.glBindVertexArray(0)

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)

        // second, draw texture to screen
        GLES30.glViewport(0, 0, screenWidth, screenHeight)
        shader.use()
        shader.setInt("texture0", 0)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glBindVertexArray(vaos[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTexIds[0]) // 用 fbo 渲染的结果作为纹理的输入

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size, GLES30.GL_UNSIGNED_INT, 0)

        // unbind vao
        GLES30.glBindVertexArray(0)
    }
}