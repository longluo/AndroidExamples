package com.wpf.opususedemo.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

object Uilts {

    fun byteArrayToShortArray(byteArray: ByteArray): ShortArray {
        val shortArray = ShortArray(byteArray.size / 2)
        ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder()).asShortBuffer().get(shortArray)
        return shortArray
    }

    fun shortArrayToByteArray(shortArray: ShortArray): ByteArray {
        val count = shortArray.size
        val dest = ByteArray(count shl 1)
        for (i in 0 until count) {
            dest[i * 2] = ((shortArray[i] and 0xFFFF.toShort()).toLong() shr 0).toByte()
            dest[i * 2 + 1] = ((shortArray[i] and 0xFFFF.toShort()).toLong() shr 8).toByte()
        }
        return dest
    }
}