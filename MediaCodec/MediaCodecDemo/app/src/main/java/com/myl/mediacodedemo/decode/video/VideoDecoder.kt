package com.myl.mediacodedemo.decode.video

import android.content.res.AssetFileDescriptor
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import com.myl.mediacodedemo.decode.MediaDecoder
import java.lang.Exception

class VideoDecoder : MediaDecoder() {

    companion object {
        private const val VIDEO = "video/"
        private const val TAG = "VideoDecoder"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun init(surface: Surface, file: AssetFileDescriptor): Boolean {
        super.init(file)
        // 视频的MediaExtractor
        extractor = MediaExtractor()
        extractor.setDataSource(file)
        (0..extractor.trackCount).forEach { index ->
            val format = extractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith(VIDEO) == true) {
                extractor.selectTrack(index)
                decoder = MediaCodec.createDecoderByType(mime)
                try {
                    Log.d(TAG, "format : $format")
                    decoder.configure(format, surface, null, 0)
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "codec $mime failed configuration. $e")
                    return false
                }
                decoder.start()
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun run() {
        val newBufferInfo = MediaCodec.BufferInfo()
        var isFirst = false
        var startWhen = 0L
        try {
            while (!interrupted()) {
                decoder.dequeueInputBuffer(TIME_OUT_US).takeIf { it >= 0 }?.let { index ->
                    // fill inputBuffers[inputBufferIndex] with valid data
                    val inputBuffer = decoder.getInputBuffer(index)
                    inputBuffer?.let {
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)

                        if (extractor.advance() && sampleSize > 0) {
                            decoder.queueInputBuffer(index, 0, sampleSize, extractor.sampleTime, 0)
                        } else {
                            Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                            decoder.queueInputBuffer(
                                index,
                                0,
                                0,
                                0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                        }
                    }
                }

                when (val outIndex = decoder.dequeueOutputBuffer(
                    newBufferInfo,
                    TIME_OUT_US
                )) {
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : " + decoder.outputFormat)
                    }
                    MediaCodec.INFO_TRY_AGAIN_LATER -> {
                        Log.d(TAG, "INFO_TRY_AGAIN_LATER")
                    }
                    MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                        Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                    }
                    else -> {
                        if (isFirst.not()) {
                            startWhen = System.currentTimeMillis()
                            isFirst = true
                        }
                        try {
                            val sleepTime: Long =
                                newBufferInfo.presentationTimeUs / 1000 - (System.currentTimeMillis() - startWhen)
                            Log.d(
                                TAG,
                                "info.presentationTimeUs : " + (newBufferInfo.presentationTimeUs / 1000).toString() + " playTime: " + (System.currentTimeMillis() - startWhen).toString() + " sleepTime : " + sleepTime
                            )
                            if (sleepTime > 0) sleep(sleepTime)

                        } catch (e: InterruptedException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }

                        decoder.releaseOutputBuffer(outIndex, true)
                    }
                }

                // All decoded frames have been rendered, we can stop playing now
                if (newBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM")
                    break
                }
            }
            decoder.stop()
            decoder.release()
            extractor.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}