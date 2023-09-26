package com.zhengsr.opengldemo.render

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.zhengsr.opengldemo.AutoNextLineLinearLayout
import com.zhengsr.opengldemo.MainApplication
import com.zhengsr.opengldemo.R
import com.zhengsr.opengldemo.utils.BufferUtil
import com.zhengsr.opengldemo.utils.TextureBean
import com.zhengsr.opengldemo.utils.loadTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/16
 * describe：3D 效果
 *
 */
class L7_ShapeRender_2 : BaseRender() {


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
        private val TAG = L7_ShapeRender_2::class.java.simpleName


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
        val glView = GLSurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setEGLContextClientVersion(3)
            setEGLConfigChooser(false)
            setRenderer(this@L7_ShapeRender_2)
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

}

