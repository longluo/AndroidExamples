package com.zhengsr.opengldemo.render

import android.content.Context
import android.opengl.EGL14
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.zhengsr.opengldemo.AutoNextLineLinearLayout
import com.zhengsr.opengldemo.MainApplication
import com.zhengsr.opengldemo.R
import com.zhengsr.opengldemo.utils.BufferUtil
import com.zhengsr.opengldemo.utils.TextureBean
import com.zhengsr.opengldemo.utils.loadTexture
import java.lang.RuntimeException
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/16
 * describe：3D 效果
 *
 */
class L8_ShapeRender : BaseRender() {


    companion object {
        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         * 修改顶部着色器的坐标值，即增加个举证x向量
         */
        private const val VERTEX_SHADER = """#version 300 es
                layout(location = 0) in vec4 a_Position;
                layout(location = 1) in vec2 aTexture;
                uniform mat4 model;
                uniform mat4 view;
                uniform mat4 projection;
                 uniform mat4 u_Matrix;
                out vec4 vTextColor;
                out vec2 vTexture;
                void main()
                {
                    // 矩阵与向量相乘得到最终的位置
                    gl_Position = u_Matrix * a_Position;
                    vTexture = aTexture;
                
                }
        """
        private val TAG = L8_ShapeRender::class.java.simpleName


        /**
         * 片段着色器
         */
        private const val FRAGMENT_SHADER = """#version 300 es
                precision mediump float;
                out vec4 FragColor;
                in vec2 vTexture;
                uniform sampler2D ourTexture;
                void main()
                {
                  FragColor = texture(ourTexture,vTexture);
                }
        """


        /**
         * 6个面 x 每个面有2个三角形组成 x 每个三角形有3个顶点,共36个顶点
         */
        private val POINT_RECT_DATA2 = floatArrayOf(
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
        )

        private val indeices = intArrayOf(
            // 注意索引从0开始!
            // 此例的索引(0,1,2,3)就是顶点数组vertices的下标，
            // 这样可以由下标代表顶点组合成矩形

            0, 1, 3, // 第一个三角形
            1, 2, 3  // 第二个三角形
        )


        private fun getIdentity() = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )

    }


    var mulPosition = floatArrayOf(

        0.0f, 0.0f, 0.0f,
        1.2f, 1.2f, -1.0f,
        -1.5f, -1.3f, -2.5f,
        -1.3f, 1.3f, -1.5f
    )


    // private var colorData = BufferUtil.createFloatBuffer(COLOR_DATA)


    /**
     * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
     */
    private val POSITION_COMPONENT_COUNT = 2

    //颜色分量为3
    private val COLOR_COMPONENT_COUNT = 3
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        useVaoVboAndEbo()
    }

    private var angle = -55f
    private var aspectRatio = 1.1f
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        aspectRatio = if (width > height) {
            height.toFloat() / width
        } else {
            width.toFloat() / height
        }


        //开启z轴缓冲,深度测试
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }

    //获取矩阵
    val modelMatrix = getIdentity()
    val viewMatrix = getIdentity()
    val projectionMatrix = getIdentity()
    val mvpMatrix = getIdentity()
    override fun onDrawFrame(gl: GL10?) {
      // notUseDeepTest()
        useDeepTest()

    }

    val vbo = IntArray(2)
    val ebo = IntArray(1)
    private val vao = IntArray(2)
    private var texture: TextureBean? = null
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
        /* GLES30.glGenBuffers(1, ebo, 0)
         //绑定 ebo 到上下文
         GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
         //昂丁
         GLES30.glBufferData(
             GLES30.GL_ELEMENT_ARRAY_BUFFER,
             indexData.capacity() * 4,
             indexData,
             GLES30.GL_STATIC_DRAW
         )
 */
        //绘制位置
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 5 * 4, 0
        )
        GLES30.glEnableVertexAttribArray(0)



        texture = loadTexture(TAG, MainApplication.context, R.mipmap.wuliuqi)
        //纹理在位置和颜色之后，偏移量为6
        vertexData.position(3)
        GLES30.glVertexAttribPointer(
            1, 2, GLES30.GL_FLOAT,
            false, 5 * 4, 3 * 4 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)


        Log.d(TAG, " useVaoVboAndEbo,get texture $texture")
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
        //注意顺序，ebo 要在 eao 之后
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    private var boxCount = 0
    override fun show(context: Context) {
        //super.show(context)
        val frame = FrameLayout(context)
        EglSurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
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
        linear.addBtn("单个3D") {
            boxCount = 0
        }

        linear.addBtn("多个3D") {
            boxCount = 3
        }


        view = frame
    }

    private fun notUseDeepTest() {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT )

        GLES30.glBindVertexArray(vao[0])
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(mvpMatrix, 0)

        angle += 1
        angle %= 360
        //设置 M
        Matrix.rotateM(
            modelMatrix, 0,
            angle,
            0.5f,
            1.0f,
            0f
        )

        //设置 V
        Matrix.translateM(
            viewMatrix,
            0,
            0f,
            0f,
            -4f
        )

        //设置 P
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 0.3f, 100f)

        //组合成 mvp,先 v x m
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        //然后是 p x v x m
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        val u_Matrix = getUniform("u_Matrix")
        GLES30.glUniformMatrix4fv(u_Matrix, 1, false, mvpMatrix, 0)
        //useVaoVboAndEbo
        texture?.apply {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id)
        }

        //GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, 6, GLES30.GL_UNSIGNED_INT, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36)
    }
    private fun useDeepTest(){
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        GLES30.glBindVertexArray(vao[0])
        for (i in 0..boxCount) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.setIdentityM(viewMatrix, 0)
            Matrix.setIdentityM(projectionMatrix, 0)
            Matrix.setIdentityM(mvpMatrix, 0)

            angle += 1
            angle %= 360
            //设置 M
            Matrix.rotateM(
                modelMatrix, 0,
                angle,
                mulPosition[i * 3] + 0.5f,
                mulPosition[i * 3 + 1] + 1.0f,
                mulPosition[i * 3 + 2]
            )

            //设置 V
            Matrix.translateM(
                viewMatrix,
                0,
                mulPosition[i * 3],
                mulPosition[i * 3 + 1],
                mulPosition[i * 3 + 2] - 4f - boxCount
            )

            //设置 P
            Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 0.3f, 100f)

            //组合成 mvp,先 v x m
            Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            //然后是 p x v x m
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

            val u_Matrix = getUniform("u_Matrix")
            GLES30.glUniformMatrix4fv(u_Matrix, 1, false, mvpMatrix, 0)
            //useVaoVboAndEbo
            texture?.apply {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id)
            }

            //GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, 6, GLES30.GL_UNSIGNED_INT, 0)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36)
        }
    }


    /**
     * 自定义一个 SurfaceView，里面的线程模拟 EGL 环境
     */
    inner class EglSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

        init {
            holder.addCallback(this)
        }


        private var eglThread: EglThread? = null

        override fun surfaceCreated(holder: SurfaceHolder) {
            eglThread = EglThread(holder.surface)
            eglThread?.start()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            eglThread?.changeSize(width, height)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            eglThread?.release()
        }

        /**
         * gl线程，里面包含 EGL 创建流程
         */
        inner class EglThread(private val surface: Surface) : Thread() {
            private var isExit = false
            private var isFirst = true
            private var isSizeChange = false
            private var eglHelper: EglHelper? = null
            private var width = 0
            private var height = 0
            override fun run() {
                super.run()
                eglHelper = EglHelper().apply {
                    initEgl(surface)
                }
                while (true) {
                    if (isExit) {
                        release()
                        break
                    }

                    if (isFirst) {
                        isFirst = false
                        //回调给主类
                        this@L8_ShapeRender.onSurfaceCreated(null, null)
                    }
                    if (isSizeChange) {
                        isSizeChange = false
                        this@L8_ShapeRender.onSurfaceChanged(null, width, height)
                    }
                    eglHelper?.let {
                        this@L8_ShapeRender.onDrawFrame(null)
                        it.swapBuffers()
                    }
                    try {
                        sleep(16)
                    } catch (e: Exception) {
                    }
                }
            }

            fun release() {
                isExit = true
                eglHelper?.destroy()
                surface.release()
            }

            fun changeSize(width: Int, height: Int) {
                this.width = width
                this.height = height
                isSizeChange = true
            }
        }
    }
    inner class EglHelper {
        private var egl: EGL10? = null
        private var eglDisplay: EGLDisplay? = null
        private var eglSurface: EGLSurface? = null
        private var eglContext: EGLContext? = null
        fun initEgl(surface: Surface) {
            //1、得到Egl实例：
            val egl = EGLContext.getEGL() as EGL10
            //2、得到默认的显示设备（就是窗口）
            val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
            if (display === EGL10.EGL_NO_DISPLAY) {
                throw RuntimeException("eglGetDisplay failed")
            }

            //3、初始化默认显示设备
            val displayVersions = IntArray(2)
            if (!egl.eglInitialize(display, displayVersions)) {
                throw RuntimeException("eglInitialize failed")
            }
            //4、设置显示设备的属性
            val attr = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 8,
                EGL10.EGL_STENCIL_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
            )
            //5、从系统中获取对应属性的配置
            val num_config = IntArray(1)
            if(!egl.eglChooseConfig(display, attr, null, 1, num_config)) {
                throw RuntimeException("eglChooseConfig failed")
            }

            val numConfigs = num_config[0]

            if (numConfigs <= 0) {
                throw RuntimeException("No configs match configSpec")
            }

            val configs = arrayOfNulls<EGLConfig>(numConfigs)
            if (!egl.eglChooseConfig(display, attr, configs, numConfigs, num_config)) {
                throw RuntimeException("eglChooseConfig#2 failed")
            }

            //6、创建EglContext，3 表示OpenGL 版本号
            val  attrib_list = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                EGL10.EGL_NONE
            )
            val eglContext = egl.eglCreateContext(display,
                configs[0],
                EGL10.EGL_NO_CONTEXT, attrib_list)

            //7、创建渲染的Surface
            val eglSurface = egl.eglCreateWindowSurface(display, configs[0], surface, null)

            //8、绑定EglContext和Surface到显示设备中
            if (!egl.eglMakeCurrent(display, eglSurface, eglSurface, eglContext)) {
                throw RuntimeException("eglMakeCurrent fail")
            }
            this.egl = egl
            this.eglDisplay = display
            this.eglSurface = eglSurface
            this.eglContext = eglContext

        }

        fun swapBuffers() {
            egl?.eglSwapBuffers(eglDisplay, eglSurface)
        }

        fun destroy() {
            egl?.apply {
                eglMakeCurrent(
                    eglDisplay,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT
                )
                eglDestroySurface(eglDisplay, eglSurface)
                eglSurface = null
                eglDestroyContext(eglDisplay, eglContext)
                eglContext = null
                eglTerminate(eglDisplay)
                eglDisplay = null
                egl = null
            }
        }
    }
}

