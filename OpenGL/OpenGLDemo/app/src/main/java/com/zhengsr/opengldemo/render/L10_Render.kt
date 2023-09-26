package com.zhengsr.opengldemo.render

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.zhengsr.opengldemo.AutoNextLineLinearLayout
import com.zhengsr.opengldemo.codec.H264ParseThread
import com.zhengsr.opengldemo.codec.decoder.VideoDncoder
import com.zhengsr.opengldemo.utils.BufferUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.thread

/**
 * @author by zhengshaorui 2022/9/16
 * describe：视频渲染
 *
 */
class L10_Render : BaseRender() {


    companion object {
        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         * 修改顶部着色器的坐标值，即增加个举证x向量
         */
        private const val VERTEX_SHADER = """#version 300 es
                layout(location = 0) in vec4 a_Position;
                layout(location = 1) in vec2 aTexture;
                out vec2 vTexture;
                void main()
                {
                    // 矩阵与向量相乘得到最终的位置
                    gl_Position = a_Position;
                    vTexture = aTexture;
                
                }
        """
        private val TAG = L10_Render::class.java.simpleName


        /**
         * 片段着色器
         */
        private var FRAGMENT_SHADER = """#version 300 es
            #extension GL_OES_EGL_image_external_essl3 : require
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform samplerExternalOES ourTexture;
            void main() 
            {
                FragColor = texture(ourTexture,vTexture);
            }
        """


        private val POINT_RECT_DATA2 = floatArrayOf(
            // positions           // texture coords
            1f, 1f, 0.0f, 1.0f, 0.0f, // top right
            1f, -1f, 0.0f, 1.0f, 1.0f, // bottom right
            -1f, -1f, 0.0f, 0.0f, 1.0f, // bottom left
            -1f, 1f, 0.0f, 0.0f, 0.0f  // top left
        )

        private val indeices = intArrayOf(
            // 注意索引从0开始!
            // 此例的索引(0,1,2,3)就是顶点数组vertices的下标，
            // 这样可以由下标代表顶点组合成矩形

            0, 1, 3, // 第一个三角形
            1, 2, 3  // 第二个三角形
        )

        //  private const val U_COLOR = "u_Color"
        private const val U_MATRIX = "u_Matrix"

        //单位矩阵，单位矩阵乘以任何数都等于乘数本身
        private var UnitMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )

    }


    // private var colorData = BufferUtil.createFloatBuffer(COLOR_DATA)


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        GLES30.glGenTextures(1, textures, 0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])

        //纹理环绕
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_REPEAT
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_REPEAT
        )

        //纹理过滤
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR
        )

        //解绑纹理对象
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        useVaoVboAndEbo()


    }

    private val textures = IntArray(1)
    private var decoder: VideoDncoder? = null
    private var surfaceTexture: SurfaceTexture? = null
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        //更新 matrix 的值，即把 UnitMatrix 值，更新到 uMatrix 这个索引
        //GLES30.glUniformMatrix4fv(uMatrix, 1, false, UnitMatrix, 0)
        var time = -1L
        decoder = VideoDncoder().apply {
            surfaceTexture = SurfaceTexture(textures[0]).apply {
                setDefaultBufferSize(width, height)
                setOnFrameAvailableListener {
                }
            }
            start(Surface(surfaceTexture), object : VideoDncoder.IDecoderListener {
                override fun onReady() {
                    readFile()
                }

            })
        }

    }

    override fun onDrawFrame(gl: GL10?) {
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        surfaceTexture?.updateTexImage()
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
        GLES30.glBindVertexArray(vao[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, 6, GLES30.GL_UNSIGNED_INT, 0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    }

    private var h264ParseThread: H264ParseThread? = null

    /**
     * 读取文件
     */
    private fun readFile() {
        val stream = context.resources.assets.open("video.h264")
        h264ParseThread = H264ParseThread(stream, object : H264ParseThread.IFrameListener {
            override fun onLog(msg: String) {
                Log.d(TAG, "H264ParseThread msg: $msg")
            }

            override fun onFrame(byteArray: ByteArray, offset: Int, count: Int) {
                decoder?.feedData(byteArray, offset, count)
                Thread.sleep(55)

            }
        })
        h264ParseThread?.start()

    }

    private lateinit var glView: GLSurfaceView
    override fun show(context: Context) {
        super.show(context)
        val frame = FrameLayout(context)
        glView = GLSurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setEGLContextClientVersion(3)
            setEGLConfigChooser(false)
            setRenderer(this@L10_Render)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            frame.addView(this)
        }
        val linear = AutoNextLineLinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            frame.addView(this)
        }
        linear.addBtn("还原") {
            val msg = "FragColor = texture(ourTexture,vTexture);"
            loadFragmentVertex(msg, frame, linear)
        }
        linear.addBtn("灰色") {
            val msg = """
                    vec4 temColor = texture(ourTexture,vTexture);
                    float gray = temColor.r * 0.2126 + temColor.g * 0.7152 + temColor.b * 0.0722;
                    FragColor = vec4(gray,gray,gray,1.0);
            """
            loadFragmentVertex(msg, frame, linear)
        }

        linear.addBtn("反色") {
            val msg = """
                    vec4 temColor = texture(ourTexture,vTexture);
                    float r = 1.0- temColor.r;
                    float g = 1.0- temColor.g;
                    float b = 1.0- temColor.b;
                    FragColor = vec4(r,g,b,1.0);
            """
            loadFragmentVertex(msg, frame, linear)
        }


        view = frame
    }

    private fun loadFragmentVertex(vertex: String, frame: ViewGroup, linear: ViewGroup) {
        thread {
            release()
            Thread.sleep(100)
            FRAGMENT_SHADER = changeVertex(vertex)
            Log.d(TAG, "loadFragmentVertex: $FRAGMENT_SHADER")
            frame.post {
                frame.removeAllViews()
                frame.addView(glView)
                frame.addView(linear)
            }

        }
    }

    private fun changeVertex(fragColor: String) = """#version 300 es
            #extension GL_OES_EGL_image_external_essl3 : require
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform samplerExternalOES ourTexture;
            void main() 
            {
               $fragColor
            }
        """

    override fun dismiss() {
        super.dismiss()
        release()
    }

    private fun release() {
        h264ParseThread?.release()
        h264ParseThread = null
        decoder?.release()
        decoder = null
        surfaceTexture?.release()
        surfaceTexture = null
    }

    val vbo = IntArray(2)
    val ebo = IntArray(1)
    private val vao = IntArray(2)
    private fun useVaoVboAndEbo() {
        val vertexData = BufferUtil.createFloatBuffer(POINT_RECT_DATA2)
        val indexData = BufferUtil.createIntBuffer(indeices)


        //使用 vbo,vao 优化数据传递
        //创建 VAO
        GLES30.glGenVertexArrays(1, vao, 0)
        // //创建 VBO
        GLES30.glGenBuffers(1, vbo, 0)
        //绑定 VAO ,之后再绑定 VBO
        GLES30.glBindVertexArray(vao[0])
        //绑定VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexData.capacity() * 4,
            vertexData,
            GLES30.GL_STATIC_DRAW
        )
        //创建 ebo
        GLES30.glGenBuffers(1, ebo, 0)
        //绑定 ebo 到上下文
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        //昂丁
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indexData.capacity() * 4,
            indexData,
            GLES30.GL_STATIC_DRAW
        )

        //绘制位置
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 5 * 4, 0
        )
        GLES30.glEnableVertexAttribArray(0)


        //纹理在位置，偏移量为3
        vertexData.position(3)
        GLES30.glVertexAttribPointer(
            1, 2, GLES30.GL_FLOAT,
            false, 5 * 4, 3 * 4 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)


        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
        //注意顺序，ebo 要在 eao 之后
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
    }


    private fun readH264File() {
        val stream = context.resources.assets.open("video.h264")
        //开始肯定是 sps，所以，帧的起始位置为0
        val frameStart = 0

        //先读取头部4个自己，判断h264 是哪种格式
        val head = ByteArray(4)
        stream.read(head)
    }

}