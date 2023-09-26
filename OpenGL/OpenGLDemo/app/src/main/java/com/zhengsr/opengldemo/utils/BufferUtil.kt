package com.zhengsr.opengldemo.utils

import java.nio.*

/**
 * Buffer工具类
 *
 * @author Benhero
 */
object BufferUtil {
    /**
     * Float,Int类型占4Byte
     */
    val BYTES_PER_FLOAT = 4
    val BYTES_PER_INT = 4

    /**
     * Short类型占2Byte
     */
    val BYTES_PER_SHORT = 2

    /**
     * 创建一个FloatBuffer
     */
    fun createFloatBuffer(array: FloatArray): FloatBuffer {
        val buffer = ByteBuffer
            // 分配顶点坐标分量个数 * Float占的Byte位数
            .allocateDirect(array.size * BYTES_PER_FLOAT)
            // 按照本地字节序排序
            .order(ByteOrder.nativeOrder())
            // Byte类型转Float类型
            .asFloatBuffer()

        // 将Dalvik的内存数据复制到Native内存中
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    fun createIntBuffer(array: IntArray): IntBuffer {
        val buffer = ByteBuffer
            // 分配顶点坐标分量个数 * Float占的Byte位数
            .allocateDirect(array.size * BYTES_PER_INT)
            // 按照本地字节序排序
            .order(ByteOrder.nativeOrder())
            // Byte类型转Float类型
            .asIntBuffer()

        // 将Dalvik的内存数据复制到Native内存中
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    /**
     * 创建一个FloatBuffer
     */
    fun createShortBuffer(array: ShortArray): ShortBuffer {
        val buffer = ByteBuffer
            // 分配顶点坐标分量个数 * Float占的Byte位数
            .allocateDirect(array.size * BYTES_PER_SHORT)
            // 按照本地字节序排序
            .order(ByteOrder.nativeOrder())
            // Byte类型转Float类型
            .asShortBuffer()

        // 将Dalvik的内存数据复制到Native内存中
        buffer.put(array)
        return buffer
    }
}