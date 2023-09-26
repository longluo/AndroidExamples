package com.myl.mediacodedemo.encode

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import androidx.annotation.RequiresApi

class AudioEncoder(
    private val bitrate: Int,
    private val sampleRate: Int,
    private val channelCount: Int
) {

    companion object {
        private const val AUDIO_MIME_TYPE = "audio/mp4a-latm"
        const val BUFFER_SIZE = 8192
    }

    private var mMediaCodec: MediaCodec? =
        MediaCodec.createEncoderByType(AUDIO_MIME_TYPE)
    private var mMediaMuxer: MediaMuxer? = null
    private var mBufferSize: Int = BUFFER_SIZE
    private lateinit var mOutputPath: String
    private var mMediaFormat: MediaFormat? = null
    private var mBufferInfo: MediaCodec.BufferInfo? = null
    private var mTotalBytesRead = 0
    private var mPresentationTimeUs = 0L // 编码的时长

    /**
     * 设置缓冲区大小
     * @param size
     */
    fun setBufferSize(size: Int) {
        mBufferSize = size
    }

    /**
     * 设置音频输出路径
     * @param path 输出路径
     */
    fun setOutputPath(path: String) {
        mOutputPath = path
    }

    /**
     * 准备编码器
     * @throws Exception
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun prepare() {
        mMediaFormat = MediaFormat.createAudioFormat(
            AUDIO_MIME_TYPE,
            sampleRate,
            channelCount
        )
        mMediaFormat?.apply {
            setInteger(
                MediaFormat.KEY_AAC_PROFILE,
                MediaCodecInfo.CodecProfileLevel.AACObjectLC
            )
            setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
            setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, mBufferSize)
        }
        mMediaCodec =
            MediaCodec.createEncoderByType(AUDIO_MIME_TYPE)
        mMediaCodec?.apply {
            configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            start()
        }
        mBufferInfo = MediaCodec.BufferInfo()
        mMediaMuxer = MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        mTotalBytesRead = 0
        mPresentationTimeUs = 0
    }


    /**
     * 释放资源
     */
    fun release() {
        try {
            mMediaCodec?.apply {


                stop()
                release()
            }
            mMediaCodec = null
            mMediaMuxer?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    stop()
                    release()
                }
            }
            mMediaMuxer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}