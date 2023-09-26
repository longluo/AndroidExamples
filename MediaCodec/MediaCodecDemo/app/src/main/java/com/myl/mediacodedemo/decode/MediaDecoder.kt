package com.myl.mediacodedemo.decode

import android.content.res.AssetFileDescriptor
import android.media.MediaCodec
import android.media.MediaExtractor
import android.os.Build
import androidx.annotation.RequiresApi


open class MediaDecoder() : Thread() {
    protected lateinit var extractor: MediaExtractor
    protected lateinit var decoder: MediaCodec

    @RequiresApi(Build.VERSION_CODES.N)
    open fun init(file: AssetFileDescriptor): Boolean {
        extractor = MediaExtractor()
        extractor.setDataSource(file)
        return true
    }

    companion object {
        const val TIME_OUT_US = 1000L
    }

    fun release() {
        interrupt()
        decoder.stop()
        decoder.release()
        extractor.release()
    }
}