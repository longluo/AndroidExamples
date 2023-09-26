package com.myl.mediacodedemo.utils

object TimeUtils {
    const val S_TO_MS = 1000
    const val S_TO_US = 1000_000

    const val MS_TO_US = 1000
    const val US_TO_MS = 1000

    fun sToMs(s: Float): Long {
        return (s * S_TO_MS).toLong()
    }

    fun sToMs(s: Long): Long {
        return s * S_TO_MS
    }

    fun sToUs(s: Float): Float {
        return s * S_TO_US
    }

    fun sToUs(s: Long): Long {
        return s * S_TO_US
    }

    fun msToS(ms: Long): Long {
        return ms / S_TO_MS
    }

    fun msToS(ms: Float): Float {
        return ms / S_TO_MS
    }

    fun usToS(us: Float): Float {
        return us / S_TO_US
    }

    fun usToS(us: Long): Long {
        return us / S_TO_US
    }

    fun usToMs(us: Long): Long {
        return us / MS_TO_US
    }

    fun msToUs(ms: Long): Long {
        return ms * MS_TO_US
    }

    fun currentTimeUs(): Long {
        return System.currentTimeMillis() * MS_TO_US
    }
}