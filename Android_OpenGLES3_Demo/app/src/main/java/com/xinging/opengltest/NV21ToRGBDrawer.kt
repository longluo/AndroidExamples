package com.xinging.opengltest

import android.content.Context
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class NV21ToRGBDrawer : AbstractDrawer() {

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
            
            uniform sampler2D y_texture;
            uniform sampler2D uv_texture;
            
            in vec2 v_texcoord;
            
            out vec4 fragColor;
            
            void main()
            {
               vec3 yuv;
               yuv.x = texture(y_texture,v_texcoord).r-0.0625;  // y
               yuv.y = texture(uv_texture,v_texcoord).a-0.5;  // u
               yuv.z = texture(uv_texture,v_texcoord).r-0.5;  // v
               mat3 m = mat3(
                    1.164, 1.164, 1.164,  
                    0.0, -0.213, 2.112, 
                    1.793, -0.533, 0.0);
               
               vec3 rgb = m * yuv;
               fragColor = vec4(rgb, 1.0);
            }
            
            """.trimIndent()
    }

    private val vertices = floatArrayOf(
        // positions       // texture coords
        0.5f, 0.5f, 0.0f, 1.0f, 1.0f,   // top right
        0.5f, -0.5f, 0.0f, 1.0f, 0.0f,  // bottom right
        -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // bottom left
        -0.5f, 0.5f, 0.0f, 0.0f, 1.0f   // top left
    )

    private val indices = intArrayOf(
        0, 1, 3, // first triangle
        1, 2, 3  // second triangle
    )

    private val shader = Shader(
        vertexShaderSource,
        fragmentShaderSource
    )

    val vaos = IntBuffer.allocate(1)
    val vbos = IntBuffer.allocate(1)
    val ebo = IntBuffer.allocate(1)
    val texIds = IntBuffer.allocate(2)
    override fun prepare(context: Context) {
        // compile shader
        shader.prepareShaders()

        // generate vao, vbo, ebo
        GLES30.glGenVertexArrays(1, vaos)
        GLES30.glGenBuffers(1, vbos)
        GLES30.glGenBuffers(1, ebo)

        // bind vao
        GLES30.glBindVertexArray(vaos[0])

        // set vbo data
        val vertexBuffer = ByteBuffer
            .allocateDirect(vertices.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }
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
        GLES30.glGenTextures(2, texIds)
        MyGLUtils.checkGlError("glGenTextures")

        // load texture data
        val width = 700
        val height = 700
        val inputStream = context.resources.openRawResource(R.raw.rainbow_nv21)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()

        val byteBuffer = ByteBuffer.allocateDirect(buffer.size)
        byteBuffer.put(buffer)
        byteBuffer.flip() // 确保缓冲区准备好读取

        // configure Y texture
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[0])
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_LUMINANCE,
            width,
            height,
            0,
            GLES30.GL_LUMINANCE,
            GLES30.GL_UNSIGNED_BYTE,
            byteBuffer
            )
        MyGLUtils.checkGlError("configure Y texture")


        // configure UV texture
        val uvWidth = width / 2
        val uvHeight = height / 2
        val offset = width * height
        byteBuffer.position(offset)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[1])
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_LUMINANCE_ALPHA,
            uvWidth,
            uvHeight,
            0,
            GLES30.GL_LUMINANCE_ALPHA,
            GLES30.GL_UNSIGNED_BYTE,
            byteBuffer
        )
        MyGLUtils.checkGlError("configure UV texture")

        // use share program and set texture location
        shader.use()
        shader.setInt("y_texture", 0)
        shader.setInt("uv_texture", 1)
    }

    override fun draw() {
        GLES30.glViewport(0, 0, screenWidth, screenHeight)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glBindVertexArray(vaos[0])

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[1])

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size, GLES30.GL_UNSIGNED_INT, 0)

        // unbind vao
        GLES30.glBindVertexArray(0)
    }
}