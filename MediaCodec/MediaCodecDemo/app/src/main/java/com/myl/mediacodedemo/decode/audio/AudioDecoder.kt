package com.myl.mediacodedemo.decode.audio

import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.myl.mediacodedemo.decode.MediaDecoder
import java.lang.Exception
import java.nio.ByteBuffer

class AudioDecoder : MediaDecoder() {

    companion object {
        private const val AUDIO = "audio/"
        private const val TAG = "AudioDecoder"
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun run() {
        super.run()
        val info = MediaCodec.BufferInfo()
        val bufferSize = AudioTrack.getMinBufferSize(
            mSampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            mSampleRate, AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT, bufferSize,
            AudioTrack.MODE_STREAM
        )
        audioTrack.play()
        var isEOS = false
        val startMs = System.currentTimeMillis()
        try {
            while (!interrupted()) {
                if (!isEOS) {
                    val inIndex: Int = decoder.dequeueInputBuffer(TIME_OUT_US)
                    if (inIndex >= 0) {
                        val buffer: ByteBuffer? = decoder.getInputBuffer(inIndex)
                        val sampleSize: Int = buffer?.let { extractor.readSampleData(it, 0) } ?: -1
                        if (sampleSize < 0) {
                            // We shouldn't stop the playback at this point,
                            // just pass the EOS
                            // flag to mediaDecoder, we will get it again from
                            // the
                            // dequeueOutputBuffer
                            Log.d(
                                TAG,
                                "InputBuffer BUFFER_FLAG_END_OF_STREAM"
                            )
                            decoder.queueInputBuffer(
                                inIndex, 0, 0, 0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            isEOS = true
                        } else {
                            decoder.queueInputBuffer(
                                inIndex, 0,
                                sampleSize, extractor.sampleTime, 0
                            )
                            extractor.advance()
                        }
                    }
                }
                when (val outIndex: Int = decoder.dequeueOutputBuffer(info, TIME_OUT_US)) {
                    MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                        Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                    }
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        val format: MediaFormat = decoder.outputFormat
                        Log.d(TAG, "New format $format")
                        audioTrack.playbackRate = format
                            .getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    }
                    MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(
                        TAG,
                        "dequeueOutputBuffer timed out!"
                    )
                    else -> {
                        val buffer: ByteBuffer? = decoder.getOutputBuffer(outIndex)
                        Log.v(
                            TAG,
                            "We can't use this buffer but render it due to the API limit, $buffer"
                        )
                        val chunk = ByteArray(info.size)
                        buffer?.get(chunk)
                        //clear buffer,otherwise get the same buffer which is the last buffer
                        buffer?.clear()
                        // We use a very simple clock to keep the video FPS, or the
                        // audio playback will be too fast
                        val sleepTime: Long =
                            info.presentationTimeUs / 1000 - (System.currentTimeMillis() - startMs)
                        Log.d(
                            TAG,
                            "info.presentationTimeMs : " + (info.presentationTimeUs / 1000).toString() + " playTime: " + (System.currentTimeMillis() - startMs).toString() + " sleepTime : " + sleepTime
                        )
                        if (sleepTime > 0) sleep(sleepTime)
                        // AudioTrack write data
                        audioTrack.write(
                            chunk, info.offset, info.offset
                                    + info.size
                        )
                        decoder.releaseOutputBuffer(outIndex, false)
                    }
                }
                // All decoded frames have been rendered, we can stop playing now
                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM")
                    break
                }
            }
            decoder.stop()
            decoder.release()
            extractor.release()
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var mSampleRate = 0
    private var channel = 0


    @RequiresApi(Build.VERSION_CODES.N)
    override fun init(file: AssetFileDescriptor): Boolean {
        super.init(file)
        for (i in 0 until extractor.trackCount) {
            val format: MediaFormat = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith(AUDIO) == true) {
                extractor.selectTrack(i)
                mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                decoder = MediaCodec.createDecoderByType(mime)
                decoder.configure(format, null, null, 0)
                break
            }
        }
        if (decoder == null) {
            Log.e(TAG, "Can't find audio info!")
            return false
        }
        decoder.start()
        return true
    }
}