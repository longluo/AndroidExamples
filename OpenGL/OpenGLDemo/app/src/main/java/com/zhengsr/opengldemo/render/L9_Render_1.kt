package com.zhengsr.opengldemo.render

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
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
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.thread

/**
 * @author by zhengshaorui 2022/9/16
 * describe：视频渲染，抖音特效
 *
 */
class L9_Render_1 : BaseRender() {


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
        private val TAG = L9_Render_1::class.java.simpleName


        /**
         * 片段着色器
         */
        private var FRAGMENT_SHADER = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D textureY;
            uniform sampler2D textureU;
            uniform sampler2D textureV;
            void main() {
                //采样到的yuv向量数据  
                 float y,u,v;
                //yuv转化得到的rgb向量数据
                vec3 rgb;
                //分别取yuv各个分量的采样纹理
                y = texture(textureY, vTexture).r;
                u = texture(textureU, vTexture).g - 0.5;
                v = texture(textureV, vTexture).b - 0.5;
              
                //yuv转化为rgb， https://en.wikipedia.org/wiki/YUV
                rgb.r = y + 1.540*v;
                rgb.g = y - 0.183*u - 0.459*v;
                rgb.b = y + 1.818*u;
                FragColor = vec4(rgb, 1.0);
            
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
        //三个纹理，需要设置纹理的下标
        GLES30.glUniform1i(GLES30.glGetUniformLocation(programId, "textureY"), 0)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(programId, "textureU"), 1)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(programId, "textureV"), 2)

        GLES30.glGenTextures(3, textures, 0)
        for (i in 0..2) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[i])

            //纹理环绕
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)

            //纹理过滤
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR
            )

            //解绑纹理对象
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        }
        useVaoVboAndEbo()

    }

    private val w = 288
    private val h = 512
    private val textures = IntArray(3)
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        isExit = false
        //更新 matrix 的值，即把 UnitMatrix 值，更新到 uMatrix 这个索引
        //GLES30.glUniformMatrix4fv(uMatrix, 1, false, UnitMatrix, 0)
        thread {
            readYuvData(w, h)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        if (isExit) {
            return
        }

        //使用 y 数据
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_LUMINANCE,
            w,
            h,
            0,
            GLES30.GL_LUMINANCE,
            GLES30.GL_UNSIGNED_BYTE,
            bufferY
        )
        //使用 u 数据
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[1])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_LUMINANCE,
            w / 2,
            h / 2,
            0,
            GLES30.GL_LUMINANCE,
            GLES30.GL_UNSIGNED_BYTE,
            bufferU
        )
        //使用 v 数据
        GLES30.glActiveTexture(GLES30.GL_TEXTURE2)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[2])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_LUMINANCE,
            w / 2,
            h / 2,
            0,
            GLES30.GL_LUMINANCE,
            GLES30.GL_UNSIGNED_BYTE,
            bufferV
        )
        GLES30.glBindVertexArray(vao[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, 6, GLES30.GL_UNSIGNED_INT, 0)
        bufferY?.clear()
        bufferU?.clear()
        bufferV?.clear()
    }

    private var bufferY: ByteBuffer? = null
    private var bufferU: ByteBuffer? = null
    private var bufferV: ByteBuffer? = null
    private var isExit = false;
    private fun readYuvData(w: Int, h: Int) {
        val input = context.resources.assets.open(YUV_FILE)
        //视频时 yuv420p ,4 个 y 共用一个 uv
        val y = ByteArray(w * h)
        val u = ByteArray(w * h / 4)
        val v = ByteArray(w * h / 4)

        while (true) {
            if (isExit) {
                Log.d(TAG, "readYuvData,手动退出")
                return
            }
            val readY = input.read(y)
            val readU = input.read(u)
            val readV = input.read(v)
            //都读到分量
            if (readY > 0 && readU > 0 && readV > 0) {
                //从这里触发刷新
                bufferY = ByteBuffer.wrap(y)
                bufferU = ByteBuffer.wrap(u)
                bufferV = ByteBuffer.wrap(v)

                try {
                    glView.requestRender()
                    Thread.sleep(30)
                } catch (e: Exception) {
                }

            } else {
                Log.d(TAG, "readYuvData，文件末尾，退出")
                return
            }
        }
    }

    private lateinit var glView: GLSurfaceView
    override fun show(context: Context) {
        //super.show(context)
        val frame = FrameLayout(context)
        glView = GLSurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setEGLContextClientVersion(3)
            setEGLConfigChooser(false)
            setRenderer(this@L9_Render_1)
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
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
            loadFragmentVertex(normalFragmentVertex(), frame, linear)
        }
        linear.addBtn("灰色") {
            loadFragmentVertex(grayFragmentVertex(), frame, linear)
        }

        linear.addBtn("反色") {
            loadFragmentVertex(InvertColorFragmentVertex(), frame, linear)
        }
        linear.addBtn("交叉滤镜") {
            loadFragmentVertex(FourInvertFragmentVertex(), frame, linear)
        }
        linear.addBtn("二分屏") {
            loadFragmentVertex(TwoScreenFragmentVertex(), frame, linear)
        }
        linear.addBtn("三分屏") {
            loadFragmentVertex(ThreeScreenFragmentVertex(), frame, linear)
        }


        view = frame

    }

    private fun loadFragmentVertex(vertex: String, frame: ViewGroup, linear: ViewGroup) {
        isExit = true
        thread {
            bufferY?.clear()
            bufferU?.clear()
            bufferV?.clear()
            bufferY = null
            bufferU = null
            bufferV = null
            Thread.sleep(100)
            FRAGMENT_SHADER = vertex
            frame.post {
                frame.removeAllViews()
                frame.addView(glView)
                frame.addView(linear)
            }

        }

    }

    override fun dismiss() {
        super.dismiss()
        isExit = true
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

    private fun grayFragmentVertex() = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D textureY;
            uniform sampler2D textureU;
            uniform sampler2D textureV;
            void main() {
                //采样到的yuv向量数据  
                 float y,u,v;
                //yuv转化得到的rgb向量数据
                vec3 rgb;
                //分别取yuv各个分量的采样纹理
                y = texture(textureY, vTexture).r;
               // u = texture(textureU, vTexture).g - 0.5;
               // v = texture(textureV, vTexture).b - 0.5;
                u = 0.0;
                v = 0.0;
                //yuv转化为rgb， https://en.wikipedia.org/wiki/YUV
                rgb.r = y + 1.540*v;
                rgb.g = y - 0.183*u - 0.459*v;
                rgb.b = y + 1.818*u;
                FragColor = vec4(rgb, 1.0);
            
            }
        """

    private fun normalFragmentVertex() = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D textureY;
            uniform sampler2D textureU;
            uniform sampler2D textureV;
            void main() {
                //采样到的yuv向量数据  
                 float y,u,v;
                //yuv转化得到的rgb向量数据
                vec3 rgb;
                //分别取yuv各个分量的采样纹理
                y = texture(textureY, vTexture).r;
                u = texture(textureU, vTexture).g - 0.5;
                v = texture(textureV, vTexture).b - 0.5;
             
                //yuv转化为rgb， https://en.wikipedia.org/wiki/YUV
                rgb.r = y + 1.540*v;
                rgb.g = y - 0.183*u - 0.459*v;
                rgb.b = y + 1.818*u;
                FragColor = vec4(rgb, 1.0);
            
            }
        """

    private fun InvertColorFragmentVertex() = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D textureY;
            uniform sampler2D textureU;
            uniform sampler2D textureV;
            void main() {
                //采样到的yuv向量数据  
                 float y,u,v;
                //yuv转化得到的rgb向量数据
                vec3 rgb;
                //分别取yuv各个分量的采样纹理
                y = texture(textureY, vTexture).r;
                u = texture(textureU, vTexture).g - 0.5;
                v = texture(textureV, vTexture).b - 0.5;
             
                //yuv转化为rgb， https://en.wikipedia.org/wiki/YUV
                rgb.r = 1.0 - (y + 1.540*v);
                rgb.g = 1.0 - (y - 0.183*u - 0.459*v);
                rgb.b = 1.0 - (y + 1.818*u);
                FragColor = vec4(rgb, 1.0);
            
            }
        """

    private fun FourInvertFragmentVertex() = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D textureY;
            uniform sampler2D textureU;
            uniform sampler2D textureV;
            void main() {
                //采样到的yuv向量数据  
                 float y,u,v;
                //yuv转化得到的rgb向量数据
                vec3 rgb;
                //分别取yuv各个分量的采样纹理
                y = texture(textureY, vTexture).r;
                u = texture(textureU, vTexture).g - 0.5;
                v = texture(textureV, vTexture).b - 0.5;
             
                //yuv转化为rgb， https://en.wikipedia.org/wiki/YUV
                rgb.r = (y + 1.540*v);
                rgb.g = (y - 0.183*u - 0.459*v);
                rgb.b = (y + 1.818*u);
                
                if(vTexture.x <= 0.5 && vTexture.y <= 0.5){
                    //左上角，使用反色
                    float r = 1.0 - rgb.r;
                    float g = 1.0 - rgb.g;
                    float b = 1.0 - rgb.b;
                    FragColor = vec4(r,g,b, 1.0);
                }else if(vTexture.x > 0.5 && vTexture.y > 0.5){
                   
                     //右下角，使用灰度
                    float gray = rgb.r * 0.2126 + rgb.g * 0.7152 + rgb.b * 0.0722;
                    FragColor = vec4(gray,gray,gray, 1.0);
                }else{
                    FragColor = vec4(rgb, 1.0);
                }
                
               
            
            }
        """

    private fun TwoScreenFragmentVertex() = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D textureY;
            uniform sampler2D textureU;
            uniform sampler2D textureV;
            void main() {
                    //采样到的yuv向量数据  
            float y,u,v;
            //yuv转化得到的rgb向量数据
            vec3 rgb;
            //输入是不能被修改的，所以使用一个vec2 分量
            vec2 uv = vTexture.xy;
            if(uv.y >= 0.0 && uv.y <= 0.5){
                uv.y = uv.y + 0.25;
            }else{
                uv.y = uv.y - 0.25;
            }
            
            //分别取yuv各个分量的采样纹理
            y = texture(textureY, uv).r;
            u = texture(textureU, uv).g - 0.5;
            v = texture(textureV, uv).b - 0.5;
        
            //yuv转化为rgb， https://en.wikipedia.org/wiki/YUV
            rgb.r =  (y + 1.540*v);
            rgb.g = (y - 0.183*u - 0.459*v);
            rgb.b = (y + 1.818*u);
            FragColor = vec4(rgb, 1.0);
            }
        """

    private fun ThreeScreenFragmentVertex() = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D textureY;
            uniform sampler2D textureU;
            uniform sampler2D textureV;
            void main() {
            //采样到的yuv向量数据  
            float y,u,v;
            //yuv转化得到的rgb向量数据
            vec3 rgb;
            //输入是不能被修改的，所以使用一个vec2 分量
            vec2 uv = vTexture.xy;
            if(uv.y >= 0.0 && uv.y <= 0.2){
                uv.y = uv.y + 0.3;
            }else if(uv.y > 0.8){
                uv.y = uv.y - 0.5;
            }
            
            //分别取yuv各个分量的采样纹理
            y = texture(textureY, uv).r;
            u = texture(textureU, uv).g - 0.5;
            v = texture(textureV, uv).b - 0.5;
        
            //yuv转化为rgb， https://en.wikipedia.org/wiki/YUV
            rgb.r =  (y + 1.540*v);
            rgb.g = (y - 0.183*u - 0.459*v);
            rgb.b = (y + 1.818*u);
            FragColor = vec4(rgb, 1.0);
            }
        """

}