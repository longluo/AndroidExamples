package com.zhengsr.opengldemo.render

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.zhengsr.opengldemo.utils.BufferUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/15
 * describe：
 */
class L1_PointRender : BaseRender() {
    companion object {
        private const val TAG = "PointRender"

        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         *         //关键字 概念：
        // 1. uniform 由外部程序传递给 shader，就像是C语言里面的常量，shader 只能用，不能改；
        // 2. attribute 是只能在 vertex shader 中使用的变量；
        // 3. varying 变量是 vertex shader 和 fragment shader 之间做数据传递用的。
        // 更多说明：http://blog.csdn.net/jackers679/article/details/6848085
         */
        //注意 #version 这里，一定要第一行，不然gl识别不到
        private val VERTEX_SHADER = """#version 300 es
           layout(location=0) in vec4 a_Position;
         
            void main(){
                gl_Position = a_Position;
                gl_PointSize = 30.0;
            }
            
        """

        /**
         * 片段着色器
         */
        private val FRAGMENT_SHADER = """#version 300 es
                // 定义所有浮点数据类型的默认精度；有lowp、mediump、highp 三种，但只有部分硬件支持片段着色器使用highp。(顶点着色器默认highp)
                precision mediump float;
                //颜色是4分量，如果没有设置，则默认黑色 RGBA
                out vec4 u_Color;
                void main(){
                    u_Color = vec4(1.0,0.0,0.0,1.0);
                }
        """

        //定点的数据，只有一个点，就放中心即可
        private val POINT_DATA = floatArrayOf(0f, 0f)

        /**
         * Float类型占4Byte
         */
        private val BYTES_PER_FLOAT = 4

        /**
         * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
         */
        private val POSITION_COMPONENT_COUNT = 2

    }


    //通过nio ByteBuffer把设置的顶点数据加载到内存
    private var vertexData = ByteBuffer
        // 分配顶点坐标分量个数 * Float占的Byte位数
        .allocateDirect(POINT_DATA.size * BYTES_PER_FLOAT)
        // 按照本地字节序排序
        .order(ByteOrder.nativeOrder())
        // Byte类型转Float类型
        .asFloatBuffer()
        .put(POINT_DATA)
        //将缓冲区的指针指到头部，保证数据从头开始
        .position(0)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //白色背景
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        // 编译着色器相关程序
        makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        // 关联顶点坐标属性和缓存数据，参数说明如下：
        GLES30.glVertexAttribPointer(
            0, //位置索引
            POSITION_COMPONENT_COUNT,//用几个分量描述一个顶点
            GLES30.GL_FLOAT,//分量类型
            false, //固定点数据值是否应该被归一化
            0, //指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0
            vertexData
        ) //顶点数据缓冲区

        //通知GL程序使用指定的顶点属性索引
        GLES30.glEnableVertexAttribArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //填充整个页面
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(TAG, "onDrawFrame() call")
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        // 1.绘制的图形类型；2.从顶点数组读取的起点；3.从顶点数组读取的顶点个数 ,这里只绘制一个点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1)
    }


}