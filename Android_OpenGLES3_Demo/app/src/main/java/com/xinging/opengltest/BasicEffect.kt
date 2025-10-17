package com.xinging.opengltest

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

enum class BasicEffectType {
    // 动态网格
    DYNAMIC_MESH,

    // 动态圆
    DYNAMIC_CIRCLE,

    // 四分屏
    QUAD_SPLIT,

    // 百叶窗
    BLINDS,

    // 溶解渐入
    DISSOLVE,

    // 劈裂
    SPLITTING,

    // 轮子
    WHEEL,

    // 马赛克
    MOSAIC
}

class BasicEffect(private val effectType: BasicEffectType) : AbstractDrawer() {

    companion object {
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

        private val DYNAMIC_MESH_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform vec2 resolution;
            uniform float offset;

            uniform sampler2D texture0;

            in vec4 v_position;
            in vec2 v_texcoord;

            out vec4 fragColor;

            void main()
            {
                vec2 imgTextCoord = v_texcoord * resolution;
                float sideLength = resolution.y / 6.0;
                float maxOffset = 0.15 * sideLength;
                float x = mod(imgTextCoord.x, floor(sideLength));
                float y = mod(imgTextCoord.y, floor(sideLength));
                
                float offsetLength = offset * maxOffset;
                if(offsetLength <= x && x <= sideLength-offsetLength
                && offsetLength <= y && y <= sideLength-offsetLength)
                {
                    fragColor = texture(texture0, v_texcoord);
                }else{
                    fragColor = vec4(1.0, 1.0, 1.0, 1.0);
                }
            }

            """.trimIndent()

        private val DYNAMIC_CIRCLE_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform vec2 resolution;
            uniform float offset;

            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                float minR = 0.2;
                float maxR = 1.0;
                float r = (minR - maxR)*offset + maxR;
                vec2 circlePoint = vec2(0.5, 0.5);
                
                vec2 circlePointRes = circlePoint * resolution;
                vec2 texcoordRes = v_texcoord * resolution;
                float rRes = r * resolution.x * 0.5;
                float dis = distance(circlePointRes, texcoordRes);
                if(dis < rRes){
                    fragColor = texture(texture0, v_texcoord); 
                }else
                {
                    fragColor = vec4(1,1,1,1);
                }
            }
        """.trimIndent()

        private val QUAD_SPLIT_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform vec2 resolution;

            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                float N = 2.0;
                vec2 uv = v_texcoord;
                uv *= N;
                uv = fract(uv);
                
                fragColor = texture(texture0, uv);
            }
            """.trimIndent()

        private val BLINDS_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform vec2 resolution;
            uniform float offset;

            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                float shuttersMaxH = 1.0 / 10.0;
                float shuttersH = -shuttersMaxH*offset + shuttersMaxH;
                float y = mod(v_texcoord.y, shuttersMaxH);
                
                if(y < shuttersH)
                {
                    fragColor = vec4(1.0,1.0,1.0,1.0);
                }else
                {
                    fragColor = texture(texture0, v_texcoord);
                }
            }
        """.trimIndent()

        private val DISSOLVE_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform sampler2D texture0;
            uniform float offset;

            in vec2 v_texcoord;

            out vec4 fragColor;

            float rand2(vec2 co){
                return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
            }

            void main(void)
            {

                float randomValue = rand2(v_texcoord);
                if(randomValue < offset){
                    fragColor = texture(texture0, v_texcoord);
                }else{
                    fragColor = vec4(1.0,1.0,1.0,1.0);
                }
            }
        """.trimIndent()

        private val SPLITTING_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform float offset;

            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                float w = -offset + 1.0;
                float dis = abs(v_texcoord.x - 0.5);
                if(dis < w/2.0){
                  fragColor = vec4(1.0,1.0,1.0,1.0);
                }else{
                  fragColor = texture(texture0, v_texcoord);
                }
            }
        """.trimIndent()

        private val WHEEL_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform float offset;

            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                vec2 circlePos = vec2(0.5, 0.5); // 圆心位置
                vec2 direction = v_texcoord - circlePos; // 从圆心指向当前片元的向量

                // 计算当前片元相对于圆心的角度
                float angle = atan(direction.y, direction.x);

                // 将角度范围从 [-π, π] 映射到 [0, 2π]
                if (angle < 0.0) {
                    angle += 2.0 * 3.14159265358979323846;
                }

                // 当前阈值角度，offset 控制动画进度，范围为 [0, 2π]
                float curAngle = offset * 2.0 * 3.14159265358979323846;

                // 根据当前片元的角度和阈值角度决定片元颜色
                if (angle < curAngle) {
                    fragColor = texture(texture0, v_texcoord); // 显示纹理
                } else {
                    fragColor = vec4(1.0, 1.0, 1.0, 1.0); // 显示白色
                }
            }
        """.trimIndent()

        private val MOSAIC_SOURCE = """
            #version 300 es
            precision mediump float;

            uniform sampler2D texture0;

            in vec2 v_texcoord;

            out vec4 fragColor;

            void main(void)
            {
                float numBlockX = 150.0;
                float numBlockY = 150.0;
                float stepX = 1.0 / numBlockX;
                float stepY = 1.0 / numBlockY;
                float indexBlockX = floor(v_texcoord.x / stepX);
                float indexBlockY = floor(v_texcoord.y / stepY);
                
                vec2 currentBlockLeftBottom = vec2(indexBlockX*stepX, indexBlockY*stepY);
                
                fragColor = texture(texture0, currentBlockLeftBottom);
            }
        """.trimIndent()

        val fragmentShaderSources: Map<BasicEffectType, String> = mapOf(
            BasicEffectType.DYNAMIC_MESH to DYNAMIC_MESH_SOURCE,
            BasicEffectType.DYNAMIC_CIRCLE to DYNAMIC_CIRCLE_SOURCE,
            BasicEffectType.QUAD_SPLIT to QUAD_SPLIT_SOURCE,
            BasicEffectType.BLINDS to BLINDS_SOURCE,
            BasicEffectType.DISSOLVE to DISSOLVE_SOURCE,
            BasicEffectType.SPLITTING to SPLITTING_SOURCE,
            BasicEffectType.WHEEL to WHEEL_SOURCE,
            BasicEffectType.MOSAIC to MOSAIC_SOURCE
        )
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
    var offset = 0.0f
    var offsetStep = 0.005f
    var imageWidth: Int = 0
    var imageHeight: Int = 0

    private lateinit var shader: Shader
    override fun prepare(context: Context) {
        shader = Shader(vertexShaderSource, fragmentShaderSources[effectType]!!)
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
        val options = BitmapFactory.Options()
        options.inScaled = false   // No pre-scaling
        var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.lye, options)

        // Flip the bitmap vertically
        val matrix = android.graphics.Matrix()
        matrix.preScale(1.0f, -1.0f)
        bitmap = android.graphics.Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            false
        )
        imageWidth = bitmap.width
        imageHeight = bitmap.height

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        MyGLUtils.checkGlError("texImage2D")
        bitmap.recycle()

        // unbind texture
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        // use share program
        shader.use()
        // set uniform values
        shader.setInt("texture0", 0)

    }

    override fun draw() {
        // update offset
        offset += offsetStep
        if (offset >= 1.0f) {
            offset = 0.0f
        }
        shader.setFloat("offset", offset)

        val resolution = floatArrayOf(imageWidth.toFloat(), imageHeight.toFloat())
        shader.setVec2("resolution", resolution)

        GLES30.glViewport(0, 0, screenWidth, screenHeight)

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glBindVertexArray(vaos[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIds[0])

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size, GLES30.GL_UNSIGNED_INT, 0)

        // unbind vao
        GLES30.glBindVertexArray(0)
    }
}