package com.zhengsr.opengldemo.render

import android.opengl.GLES30
import android.opengl.Matrix
import com.zhengsr.opengldemo.utils.BufferUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/16
 * describe：正交投影
 *
 */
class L3_ShapeRender : BaseRender() {


    companion object {
        private const val TAG = "L2_ShapeRender"

        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         * 修改顶部着色器的坐标值，即增加个举证x向量
         */
        private const val VERTEX_SHADER = """#version 300 es
                layout(location = 0) in vec4 a_Position;
                // mat4：4×4的矩阵
                uniform mat4 u_Matrix;
                void main()
                {
                 // 矩阵与向量相乘得到最终的位置
                    gl_Position = u_Matrix * a_Position;
                    gl_PointSize = 30.0;
                     
                }
        """

        /**
         * 片段着色器
         */
        private const val FRAGMENT_SHADER = """#version 300 es
                // 定义所有浮点数据类型的默认精度；有lowp、mediump、highp 三种，但只有部分硬件支持片段着色器使用highp。(顶点着色器默认highp)
                precision mediump float;
                out vec4 FragColor;
                uniform vec4 u_Color;
                void main()
                {
                  FragColor = u_Color;
                }
        """

        private val POINT_DATA = floatArrayOf(
            //x,y 一个点，这里相当于一个棱形，自己画个坐标
            0f, 0f,
            0f, 0.5f,
            -0.5f, 0f,
            0f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0f,
        )
        private const val A_POSITION = "a_Position"
        private const val U_COLOR = "u_Color"
        private const val U_MATRIX = "u_Matrix"
        //单位矩阵，单位矩阵乘以任何数都等于乘数本身
        private val UnitMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }

    private var vertexData = BufferUtil.createFloatBuffer(POINT_DATA)


    /**
     * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
     */
    private val POSITION_COMPONENT_COUNT = 2
    private var uMatrix = 0
    private var uColor = 0;
    private var drawIndex = 0
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        uColor = getUniform(U_COLOR)
        uMatrix = getUniform(U_MATRIX)


        GLES30.glVertexAttribPointer(
            0, POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT,
            false, 0, vertexData
        )

        GLES30.glEnableVertexAttribArray(0)

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
        if (width > height) {
            Matrix.orthoM(UnitMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(UnitMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
        //更新 matrix 的值，即把 UnitMatrix 值，更新到 uMatrix 这个索引
        GLES30.glUniformMatrix4fv(uMatrix,1,false, UnitMatrix,0)
    }

    override fun onDrawFrame(gl: GL10?) {
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        drawIndex++
        // glDrawArrays 可以理解成绘制一个图层，多个图层可以叠加，然后通过onDrawFrame绘制到这一帧上
        drawTriangle()
        drawLine()
        drawPoint();

        if (drawIndex >= POINT_DATA.size / 2) {
            drawIndex = 0
        }
    }

    private fun drawLine() {
        // GL_LINES：每2个点构成一条线段
        // GL_LINE_LOOP：按顺序将所有的点连接起来，包括首位相连
        // GL_LINE_STRIP：按顺序将所有的点连接起来，不包括首位相连
        GLES30.glUniform4f(uColor, 1f, 0f, 0f, 1f)
        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, drawIndex)
    }

    private fun drawPoint() {
        GLES30.glUniform4f(uColor, 0f, 0f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, drawIndex)
    }

    private fun drawTriangle() {
        // GL_TRIANGLES：每3个点构成一个三角形
        // GL_TRIANGLE_STRIP：相邻3个点构成一个三角形,不包括首位两个点
        // GL_TRIANGLE_FAN：第一个点和之后所有相邻的2个点构成一个三角形
        GLES30.glUniform4f(uColor, 1f, 1f, 0f, 1f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, drawIndex)
    }
}