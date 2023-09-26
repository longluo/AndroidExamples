package com.zhengsr.opengldemo.render

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author by zhengshaorui 2022/9/16
 * describe：
 */
abstract class BaseRender : GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "BaseRender"
        const val YUV_FILE = "video_288_512.yuv"
    }

    protected var programId = 0
    var view: View? = null
    protected lateinit var context: Context
    fun showUI(context: Context){
        this.context = context
        show(context)
    }


    protected open fun show(context: Context) {
        view = GLSurfaceView(context).apply {
            setEGLContextClientVersion(3)
            setEGLConfigChooser(false)
            setOnClickListener {
                requestRender()
            }
            visibility = View.VISIBLE
            setRenderer(this@BaseRender)
            //等待点击才会刷帧
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
    }

    open fun dismiss() {
        view?.let {
            it.parent?.let { parent->
                if (parent is ViewGroup) {
                    parent.removeView(it)
                }
            }
        }
        view?.visibility = View.GONE
        view = null
    }




    /**
     * 生成可执行程序，并使用该程序
     */
    protected fun makeProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
        //需要编译着色器，编译成一段可执行的bin，去与显卡交流
        val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        //步骤2，编译片段着色器
        val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 步骤3：将顶点着色器、片段着色器进行链接，组装成一个OpenGL程序
        programId = linkProgram(vertexShader, fragmentShader)
        //链接之后就可以删除着色器对象了，不需要了
        GLES30.glDeleteShader(vertexShader)
        GLES30.glDeleteShader(fragmentShader)

        //通过OpenGL 使用该程序
        GLES30.glUseProgram(programId)
        return programId
    }

    protected fun getUniform(name: String): Int {
        return GLES30.glGetUniformLocation(programId, name)
    }

    protected fun getAttrib(name: String): Int {
        return GLES30.glGetAttribLocation(programId, name)
    }

    /**
     * 编译着色器代码，获取代码Id
     */
    open fun compileShader(type: Int, shaderCode: String): Int {
        //创建一个shader 对象
        val shaderId = GLES30.glCreateShader(type)
        if (shaderId == 0) {
            Log.d(TAG, " 创建失败")
            return 0
        }
        //将着色器代码上传到着色器对象中
        GLES30.glShaderSource(shaderId, shaderCode)
        //编译对象
        GLES30.glCompileShader(shaderId)
        //获取编译状态，OpenGL 把想要获取的值放入长度为1的数据首位
        val compileStatus = intArrayOf(1)
        GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        Log.d(TAG, " compileShader: ${compileStatus[0]}")

        if (compileStatus[0] == 0) {
            Log.d(TAG, " 编译失败: ${GLES30.glGetShaderInfoLog(shaderId)}")
            GLES30.glDeleteShader(shaderId)
            return 0
        }

        return shaderId
    }

    /**
     * 关联着色器代码，组成可执行程序
     */
    open fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        //创建一个 OpenGL 程序对象
        val programId = GLES30.glCreateProgram()
        if (programId == 0) {
            Log.d(TAG, " 创建OpenGL程序对象失败")
            return 0
        }
        //关联顶点着色器
        GLES30.glAttachShader(programId, vertexShaderId)
        //关联片段周色漆
        GLES30.glAttachShader(programId, fragmentShaderId)
        //将两个着色器关联到 OpenGL 对象
        GLES30.glLinkProgram(programId)
        //获取链接状态，OpenGL 把想要获取的值放入长度为1的数据首位
        val linkStatus = intArrayOf(1)
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0)
        Log.d(TAG, " linkProgram: ${linkStatus[0]}")

        if (linkStatus[0] == 0) {
            GLES30.glDeleteProgram(programId)
            Log.d(TAG, " 编译失败")
            return 0
        }
        return programId;
    }
    fun ViewGroup.addBtn(msg:String,block:()->Unit){
        Button(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = msg
            setOnClickListener {
                block.invoke()
            }
            addView(this)
        }
    }
}