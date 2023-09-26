package com.myl.mediacodedemo.encode.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaFormat
import android.media.MediaRecorder
import com.myl.mediacodedemo.encode.AudioEncoder

class AudioRecorder : Runnable {

    companion object {
        private const val CHANNEL_MONO = 1
        private const val CHANNEL_BINAURAL = 2
    }

    // 录制监听器
    var mRecordListener: OnRecordListener? = null

    // 录音器
    private var mAudioRecord: AudioRecord? = null

    // 音频参数
    private var mAudioParams: AudioConfig? = null

    // 音频编码器
    private var mAudioEncoder: AudioEncoder? = null

    // 最小缓冲大小
    private var minBufferSize = 0

    // 录制标志位
    @Volatile
    var mRecording = false

    private val mMediaFormat: MediaFormat? = null

    private var mBufferSize: Int = AudioEncoder.BUFFER_SIZE

    /**
     * 开始录制
     */
    fun startRecord() {
        mRecording = true
        Thread(this).start()
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        mRecording = false
    }

    fun prepare(audioConfig: AudioConfig) {
        mAudioParams = audioConfig
        if (mAudioRecord != null) {
            release()
        }
        mAudioEncoder?.release()
        minBufferSize = ((audioConfig.sampleRate * 4 * 0.02).toInt())
        mBufferSize = if (mBufferSize < minBufferSize / 2) {
            (minBufferSize / 2)
        } else {
            AudioEncoder.BUFFER_SIZE
        }
        mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, audioConfig.sampleRate,
            audioConfig.channel, audioConfig.audioFormat, minBufferSize
        )

        val channelCount =
            if (audioConfig.channel === AudioFormat.CHANNEL_IN_MONO) CHANNEL_MONO else CHANNEL_BINAURAL

        // 音频编码器

        // 音频编码器
        mAudioEncoder = AudioEncoder(audioConfig.bitRate, audioConfig.sampleRate, channelCount)
        mAudioEncoder?.apply {
            setBufferSize(mBufferSize)
            setOutputPath(audioConfig.audioPath)
            prepare()
        }
    }

    override fun run() {

    }

    /**
     * 释放数据
     */
    @Synchronized
    fun release() {
        try {
            mAudioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mAudioRecord = null
        }
        mAudioEncoder?.release()
        mAudioEncoder = null
    }
}