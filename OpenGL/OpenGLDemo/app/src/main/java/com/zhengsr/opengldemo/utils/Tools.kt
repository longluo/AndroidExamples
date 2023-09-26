package com.zhengsr.opengldemo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.annotation.ReturnThis
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author by zhengshaorui 2022/12/3
 * describe：
 */

data class TextureBean(var id: Int, var width: Int, var height: Int) {
    constructor() : this(-1, 0, 0)
}

data class FboBean(var fboId: Int, val textureId: Int, var width: Int, var height: Int) {
    constructor() : this(-1, -1, 0, 0)

    var rboId: Int = 0
}


fun loadTexture(TAG: String, context: Context, resId: Int): TextureBean? {
    val bean = TextureBean()
    val buffer = IntArray(1)
    //创建纹理对象
    GLES30.glGenTextures(1, buffer, 0)

    if (buffer[0] == 0) {
        Log.e(TAG, "创建对象失败")
        return null
    }
    //绑定纹理到上下文
    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, buffer[0])

    BitmapFactory.Options().apply {
        //不允许放大
        inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, this)
        if (bitmap == null) {
            //删除纹理对象
            GLES30.glDeleteTextures(1, buffer, 0)
            Log.d(TAG, "loadTexture fail,bitmap is null ")
            return null
        }

        //纹理环绕
        //  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT)
        //  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT)

        //纹理过滤
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        //绑定数据
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)

        //生成 mip 位图 多级渐远纹理
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)

        //回收bitmap
        bean.id = buffer[0]
        bean.width = bitmap.width
        bean.height = bitmap.height

        //解绑纹理对象
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

    }

    return bean
}

//不受虚拟按键影响，如华为mate 9将出现 1080 * 1920 这样正常的分辨率
fun getRealHeight(context: Context): Int {
    var height = 0
    val display = (context
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val dm = DisplayMetrics()
    val c: Class<*>
    try {
        c = Class.forName("android.view.Display")
        val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
        method.invoke(display, dm)
        height = dm.heightPixels
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return height
}

//不受虚拟按键影响，如华为mate 9将出现 1080 * 1920 这样正常的分辨率
fun getRealWidth(context: Context): Int {
    var width = 0
    val display = (context
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val dm = DisplayMetrics()
    val c: Class<*>
    try {
        c = Class.forName("android.view.Display")
        val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
        method.invoke(display, dm)
        width = dm.widthPixels
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return width
}


fun GLES30.gl(block: GLES30.() -> Unit) {
    block.invoke(this)
}

fun readBufferPixelToBitmap(width: Int, height: Int): Bitmap {
    val buf = ByteBuffer.allocateDirect(width * height * 4)
    buf.order(ByteOrder.LITTLE_ENDIAN)
    GLES30.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf)
    buf.rewind()
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.copyPixelsFromBuffer(buf)
    return bmp
}
