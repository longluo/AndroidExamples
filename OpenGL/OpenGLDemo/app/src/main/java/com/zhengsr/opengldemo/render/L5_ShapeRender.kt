package com.zhengsr.opengldemo.render

import android.opengl.GLES30
import android.opengl.Matrix
import com.zhengsr.opengldemo.utils.BufferUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/16
 * describe：VBO,VAO,EBO
 *
 */
class L5_ShapeRender : BaseRender() {


    companion object {
        private const val TAG = "L4_ShapeRender"

        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         * 修改顶部着色器的坐标值，即增加个举证x向量
         */
        private const val VERTEX_SHADER = """#version 300 es
                layout(location = 0) in vec4 a_Position;
                // mat4：4×4的矩阵
                uniform mat4 u_Matrix;
                //定义可以给外部赋值的顶点数据
                layout(location = 1) in vec4 a_Color;
                //给片段着色器的颜色顶点
                out vec4 vTextColor;
                void main()
                {
                    // 矩阵与向量相乘得到最终的位置
                    gl_Position = u_Matrix * a_Position;
                    gl_PointSize = 500.0;
                    //传递给片段着色器的颜色
                    vTextColor = a_Color;
                
                }
        """

        /**
         * 片段着色器
         */
        private const val FRAGMENT_SHADER = """#version 300 es
                precision mediump float;
                out vec4 FragColor;
                //接收端顶点着色器的数据，名字要相同
                in vec4 vTextColor;
                void main()
                {
                  FragColor = vTextColor;
                }
        """


        //第一个三角形
        private val POINT_COLOR_DATA = floatArrayOf(
            // positions         // colors
            0.5f, 0.5f, 0.0f,   1.0f, 0.5f, 0.5f,// 右上角
            0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 1.0f,// 右下角
            -0.5f, 0.5f, 0.0f,  0.0f, 0.5f, 1.0f,// 左上角
        )
        //第二个三角形
        private val POINT_COLOR_DATA2 = floatArrayOf(
            // positions         // colors
            0.5f, -0.5f, 0.0f,  1.0f, 0.5f, 0.5f,// 右下角
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f,// 左下角
            -0.5f, 0.5f, 0.0f,   0.0f, 0.5f, 1.0f,// 左上角
        )

        private val POINT_RECT_DATA = floatArrayOf(
            // 第一个三角形
            0.5f, 0.5f, 0.0f,   1.0f, 0.5f, 0.5f,// 右上角
            0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 1.0f,// 右下角
            -0.5f, 0.5f, 0.0f,  0.0f, 0.5f, 1.0f,// 左上角
            // 第二个三角形
            0.5f, -0.5f, 0.0f,  1.0f, 0.5f, 0.5f,// 右下角
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f,// 左下角
            -0.5f, 0.5f, 0.0f,   0.0f, 0.5f, 1.0f,// 左上角
        )
        private val POINT_RECT_DATA2 = floatArrayOf(
            // 矩形4个顶点
            0.5f, 0.5f, 0.0f,   1.0f, 0.5f, 0.5f,// 右上角
            0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 1.0f,// 右下角

            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f,// 左下角
            -0.5f, 0.5f, 0.0f,   0.0f, 0.5f, 1.0f,// 左上角
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
        private val UnitMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }


   // private var colorData = BufferUtil.createFloatBuffer(COLOR_DATA)


    /**
     * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
     */
    private val POSITION_COMPONENT_COUNT = 2
    //颜色分量为3
    private val COLOR_COMPONENT_COUNT = 3
    private var uMatrix = 0
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        uMatrix = getUniform(U_MATRIX)


       // useVbo()
        //useEboAndVbo()
      //  useVaoVbo()
        useVaoVboAndEbo()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspectRatio = if (width > height) {
            width.toFloat() / height
        } else {
            height.toFloat() / width
        }
        // 1. 矩阵数组
        // 2. 结果矩阵起始的偏移量
        // 3. left：x的最小值
        // 4. right：x的最大值
        // 5. bottom：y的最小值
        // 6. top：y的最大值
        // 7. near：z的最小值
        // 8. far：z的最大值
        if (width > height){
           Matrix.orthoM(UnitMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f)
        }else{
            Matrix.orthoM(UnitMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f)
        }
        //更新 matrix 的值，即把 UnitMatrix 值，更新到 uMatrix 这个索引
        GLES30.glUniformMatrix4fv(uMatrix,1,false, UnitMatrix,0)
    }

    override fun onDrawFrame(gl: GL10?) {
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        //useVbo()
     //   GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[0])
     //   GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,3)

        //useEboAndVbo
        // GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[0])
        // GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,ebo[0])
        // GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP,6,GLES30.GL_UNSIGNED_INT,0)


       // useVaoVbo
//        GLES30.glBindVertexArray(vao[0])
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,3)


        //useVaoVboAndEbo
        GLES30.glBindVertexArray(vao[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP,6,GLES30.GL_UNSIGNED_INT,0)
    }

    val vbo = IntArray(2)
    private fun useVbo(){

        val vertexData = BufferUtil.createFloatBuffer(POINT_COLOR_DATA)
        //创建缓存区
        GLES30.glGenBuffers(1,vbo,0)
        //绑定缓存区到上下文
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[0])
        //将顶点数据存在缓冲区
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexData.capacity() * 4,
            vertexData,
            GLES30.GL_STATIC_DRAW)
        //绘制位置，注意这里，我们不再填入 vertexData，而是填入数据偏移地址
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 24, 0
        )
        GLES30.glEnableVertexAttribArray(0)

        //绘制颜色，颜色地址偏移量从3开始，前面3个为位置
        vertexData.position(3)
        GLES30.glVertexAttribPointer(
            1, 3, GLES30.GL_FLOAT,
            false, 24, 12 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)

        //解绑数据，因为我们不需要动态更新
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0)
    }
    val ebo = IntArray(1)
    private fun useEboAndVbo(){
        val vertexData = BufferUtil.createFloatBuffer(POINT_RECT_DATA2)
        val indexData = BufferUtil.createIntBuffer(indeices)

        GLES30.glGenBuffers(1,ebo,0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,ebo[0])
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indexData.capacity() * 4,
            indexData,
            GLES30.GL_STATIC_DRAW
        )
        //使用 vbo 优化数据传递
        GLES30.glGenBuffers(1,vbo,0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexData.capacity() * 4,
            vertexData,
            GLES30.GL_STATIC_DRAW)
        //绘制位置
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 24, 0
        )
        GLES30.glEnableVertexAttribArray(0)

        //绘制颜色，颜色地址偏移量从3开始，前面3个为位置
        vertexData.position(3)
        GLES30.glVertexAttribPointer(
            1, 3, GLES30.GL_FLOAT,
            false, 24, 12 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0)

    }
    private val vao = IntArray(2)
    private fun useVaoVbo(){
         val vbo = IntArray(2)
        val vertexData = BufferUtil.createFloatBuffer(POINT_COLOR_DATA)
        //创建 VAO
        GLES30.glGenVertexArrays(2,vao,0)
        // //创建 VBO
        GLES30.glGenBuffers(2,vbo,0)
        //绑定 VAO ,之后再绑定 VBO
        GLES30.glBindVertexArray(vao[0])
        //绑定VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexData.capacity() * 4,
            vertexData,
            GLES30.GL_STATIC_DRAW)
        //绘制位置
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 24, 0
        )
        GLES30.glEnableVertexAttribArray(0)

        //绘制颜色，颜色地址偏移量从3开始，前面3个为位置
        vertexData.position(3)
        GLES30.glVertexAttribPointer(
            1, 3, GLES30.GL_FLOAT,
            false, 24, 12 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)
        //解绑数据
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0)
        GLES30.glBindVertexArray(0)


        //绑定第二个 vbo
        val vertexData2 = BufferUtil.createFloatBuffer(POINT_COLOR_DATA2)

       // GLES30.glBindVertexArray(vao[1])
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[1])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexData2.capacity() * 4,
            vertexData2,
            GLES30.GL_STATIC_DRAW)
        //绘制位置
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 24, 0
        )
        GLES30.glEnableVertexAttribArray(0)

        //绘制颜色，颜色地址偏移量从3开始，前面3个为位置
        vertexData2.position(3)
        GLES30.glVertexAttribPointer(
            1, 3, GLES30.GL_FLOAT,
            false, 24, 12 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)

        //解绑数据
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0)
        GLES30.glBindVertexArray(0)
    }

    private fun useVaoVboAndEbo(){
        val vertexData = BufferUtil.createFloatBuffer(POINT_RECT_DATA2)
        val indexData = BufferUtil.createIntBuffer(indeices)


        //使用 vbo,vao 优化数据传递
        //创建 VAO
        GLES30.glGenVertexArrays(1,vao,0)
        // //创建 VBO
        GLES30.glGenBuffers(1,vbo,0)
        //绑定 VAO ,之后再绑定 VBO
        GLES30.glBindVertexArray(vao[0])
        //绑定VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexData.capacity() * 4,
            vertexData,
            GLES30.GL_STATIC_DRAW)
        //创建 ebo
        GLES30.glGenBuffers(1,ebo,0)
        //绑定 ebo 到上下文
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,ebo[0])
        //昂丁
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indexData.capacity() * 4,
            indexData,
            GLES30.GL_STATIC_DRAW
        )

        //绘制位置
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 24, 0
        )
        GLES30.glEnableVertexAttribArray(0)

        //绘制颜色，颜色地址偏移量从3开始，前面3个为位置
        vertexData.position(3)
        GLES30.glVertexAttribPointer(
            1, 3, GLES30.GL_FLOAT,
            false, 24, 12 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0)
        GLES30.glBindVertexArray(0)
        //注意顺序，ebo 要在 eao 之后
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0)
    }




}