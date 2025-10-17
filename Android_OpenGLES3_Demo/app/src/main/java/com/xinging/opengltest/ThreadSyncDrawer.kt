package com.xinging.opengltest

import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.util.concurrent.atomic.AtomicBoolean

class ThreadSyncDrawer: AbstractDrawer() {
    private val TAG = "ThreadSyncDrawer"
    companion object{
        val vertexShaderSource =
            """
            #version 300 es
            layout(location = 0) in vec4 a_position;
            layout(location = 1) in vec2 a_texcoord;
            out vec2 v_texcoord;
            void main()
            {
                gl_Position = a_position;
                v_texcoord = a_texcoord;
            }
            """.trimIndent()

        val fragmentShaderSource = """
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
    }
    private val vertices = floatArrayOf(
        // positions       // texture coords
        1.0f, 1.0f, 0.0f, 1.0f, 1.0f,   // top right
        1.0f, -1.0f, 0.0f, 1.0f, 0.0f,  // bottom right
        -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, // bottom left
        -1.0f, 1.0f, 0.0f, 0.0f, 1.0f   // top left
    )

    private val indices = intArrayOf(
        0, 1, 3, // first triangle
        1, 2, 3  // second triangle
    )

    val vaos = IntBuffer.allocate(1)
    val vbos = IntBuffer.allocate(1)
    val ebo = IntBuffer.allocate(1)
    val texIds = IntBuffer.allocate(1)

    private val shader = Shader(
        TextureDrawer.vertexShaderSource,
        TextureDrawer.fragmentShaderSource
    )

    private var useFence = AtomicBoolean(true)
    private var mainThreadSyncObject:Long = 0
    private var workingThreadSyncObject:Long = 0


    var col = 0
    var col1 = 1
    var col2 = 2
    var col3 = 3
    fun animateTexture() {
        val r1 = texHeight / 16.0f
        val r2 = texHeight / 8.0f
        val r3 = texHeight / 4.0f
        val r4 = texHeight / 2.0f
        val r5 = texHeight.toFloat()
        val r12 = r1 * r1
        val r22 = r2 * r2
        val r32 = r3 * r3
        val r42 = r4 * r4
        val r52 = r5 * r5
        var offset = 0
        var d2 = 0.0f
        for (y in 0 until texHeight) {
            for (x in 0 until texWidth) {
                d2 = ((y - texHeight / 2) * (y - texHeight / 2) + (x - texWidth / 2) * (x - texWidth / 2)).toFloat()
                col = col % 4
                col1 = col1 % 4
                col2 = col2 % 4
                col3 = col3 % 4
                when {
                    d2 < r12 -> {
                        textureData[offset] = bakedColours[4 * col]
                        textureData[offset + 1] = bakedColours[4 * col + 1]
                        textureData[offset + 2] = bakedColours[4 * col + 2]
                        textureData[offset + 3] = bakedColours[4 * col + 3]
                    }
                    d2 < r22 -> {
                        textureData[offset] = bakedColours[4 * col1]
                        textureData[offset + 1] = bakedColours[4 * col1 + 1]
                        textureData[offset + 2] = bakedColours[4 * col1 + 2]
                        textureData[offset + 3] = bakedColours[4 * col1 + 3]
                    }
                    d2 < r32 -> {
                        textureData[offset] = bakedColours[4 * col2]
                        textureData[offset + 1] = bakedColours[4 * col2 + 1]
                        textureData[offset + 2] = bakedColours[4 * col2 + 2]
                        textureData[offset + 3] = bakedColours[4 * col2 + 3]
                    }
                    d2 < r42 -> {
                        textureData[offset] = bakedColours[4 * col3]
                        textureData[offset + 1] = bakedColours[4 * col3 + 1]
                        textureData[offset + 2] = bakedColours[4 * col3 + 2]
                        textureData[offset + 3] = bakedColours[4 * col3 + 3]
                    }
                    d2 < r52 -> {
                        textureData[offset] = 128.toByte()
                        textureData[offset + 1] = 128.toByte()
                        textureData[offset + 2] = 128.toByte()
                        textureData[offset + 3] = 255.toByte()
                    }
                }
                offset += 4
            }
        }
        col++
        col1++
        col2++
        col3++
    }

    lateinit var textureData: ByteArray
    var texWidth = 512
    var texHeight = 512

    val bakedColours = byteArrayOf(
        255.toByte(), 0.toByte(), 0.toByte(), 255.toByte(),
        0.toByte(), 255.toByte(), 0.toByte(), 255.toByte(),
        255.toByte(), 255.toByte(), 0.toByte(), 255.toByte(),
        0.toByte(), 255.toByte(), 255.toByte(), 255.toByte()
    )
    override fun prepare(context: Context) {
        // prepare texture data
        textureData = ByteArray(texWidth * texHeight * 4) // RGBA

        // compile shader
        shader.prepareShaders()
        MyGLUtils.checkGlError("compile shader")

        // prepare vbo data
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
        GLES30.glGenTextures(texIds.capacity(), texIds)
        MyGLUtils.checkGlError("glGenTextures")
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[0])
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
        animateTexture()
        val byteBuffer: ByteBuffer = ByteBuffer.wrap(textureData)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, texWidth, texHeight, 0,
            GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer)
        MyGLUtils.checkGlError("texImage2D")

        // unbind texture
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        if(useFence.get()){
            mainThreadSyncObject = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
            Log.d(TAG,"Use of GL Fence enabled.")
        }

        createWorkingThread()
    }

    private val threadExit: AtomicBoolean = AtomicBoolean(false)

    private fun createWorkingThread(){
        // get shared context
        val sharedEGLContext = EGL14.eglGetCurrentContext()

        val workingThread = Thread{
            // shared egl context
            val eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            val version = IntArray(2)
            if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
                throw RuntimeException("unable to initialize EGL14")
            }

            val attribList = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE
            )

            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            if (!EGL14.eglChooseConfig(
                    eglDisplay, attribList, 0,
                    configs, 0, configs.size, numConfigs, 0
                )
            ) {
                throw RuntimeException("unable to find RGB888+recordable ES2 EGL config")
            }
            val attribListContext = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
            val eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], sharedEGLContext, attribListContext, 0)

            if (eglContext == null || eglContext == EGL14.EGL_NO_CONTEXT) {
                throw RuntimeException("Failed to create new context.")
            }

            // Create a new surface to make current
            val attribListSurface = intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE)
            val eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, configs[0], attribListSurface, 0)

            // eglMakeCurrent
            if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                throw RuntimeException("eglMakeCurrent failed")
            }

            // loop for updating text data

            while(!threadExit.get())
            {
                Thread.sleep(1000 / 100)

                animateTexture()

                if(useFence.get())
                {
                    if(mainThreadSyncObject != 0L)
                    {
                        GLES30.glWaitSync(mainThreadSyncObject, 0, GLES30.GL_TIMEOUT_IGNORED)
                    }else
                    {
                        continue
                    }

                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[0])
                    val byteBuffer: ByteBuffer = ByteBuffer.wrap(textureData)
                    GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, texWidth, texHeight, 0,
                        GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer)

                    workingThreadSyncObject = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
                }else
                {
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[0])
                    val byteBuffer: ByteBuffer = ByteBuffer.wrap(textureData)
                    GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, texWidth, texHeight, 0,
                        GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer)
                }


            }

        }
        workingThread.start()
    }

    override fun draw() {
        if(useFence.get())
        {
            if(workingThreadSyncObject != 0L)
            {
                GLES30.glWaitSync(workingThreadSyncObject, 0, GLES30.GL_TIMEOUT_IGNORED)
            }
            else
            {
                return
            }
        }

        shader.use()
        shader.setInt("texture0", 0)

        GLES30.glViewport(0, 0, screenWidth, screenHeight)

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glBindVertexArray(vaos[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[0])

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size, GLES30.GL_UNSIGNED_INT, 0)

        // unbind vao
        GLES30.glBindVertexArray(0)

        if(useFence.get())
        {
            if(mainThreadSyncObject == 0L)
            {
                Log.i(TAG, "mainThreadSynobj == NULL at the end of renderframe.")
            }

            mainThreadSyncObject = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
        }
    }
}