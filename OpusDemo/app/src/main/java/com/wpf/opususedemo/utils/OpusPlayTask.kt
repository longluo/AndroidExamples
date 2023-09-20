package com.wpf.opususedemo.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.wpf.opususedemo.presenter.DecodeOpusPresenter
import com.wpf.opususedemo.presenter.DecodeOpusPresenter.Companion.DEFAULT_AUDIO_SAMPLE_RATE
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream


class OpusPlayTask(audioManager: AudioManager, private val opusAudioPath: String, recorderDecodedPcmFilePath: String?, private val isPCM: Boolean = false) : Runnable,
    DecodeOpusPresenter {
    private val bufferSize: Int = AudioTrack.getMinBufferSize(DEFAULT_AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
    private val audioAttributes: AudioAttributes = AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build()
    private val audioFormat: AudioFormat = AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(DEFAULT_AUDIO_SAMPLE_RATE).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()
    private val sessionId: Int = audioManager.generateAudioSessionId()
    private var audioTrack: AudioTrack
    var onOpusPlayListener: OnOpusPlayListener? = null
    var isPlay = false
    private var filePcmOutputStream: FileOutputStream? = null
    private var filePcmBufferedOutputStream: BufferedOutputStream? = null

    interface OnOpusPlayListener {
        fun onCompere()
    }

    init {
        audioTrack = AudioTrack(audioAttributes, audioFormat, bufferSize, AudioTrack.MODE_STREAM, sessionId)
//        audioTrack=AudioTrack(AudioManager.STREAM_MUSIC,ISampleRate.DEFAULT_HIGH_AUDIO_SAMPLE_RATE,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM)
        if (!recorderDecodedPcmFilePath.isNullOrEmpty() && !isPCM) {//保存解码后的PCM文件以检查问题
            val filePcm = File(recorderDecodedPcmFilePath)
            val fileDir = File(filePcm.parent)
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            if (filePcm.exists()) {
                filePcm.delete()
            }
            filePcm.createNewFile()
            filePcmOutputStream = FileOutputStream(filePcm, true)
            filePcmBufferedOutputStream = BufferedOutputStream(filePcmOutputStream)
        }
    }

    override fun opusDecode(formatShortArray: ShortArray) {
        super.opusDecode(formatShortArray)
        audioTrack.write(formatShortArray, 0, formatShortArray.size)
        if (filePcmBufferedOutputStream != null) {//保存解码后的PCM文件以检查问题
            filePcmBufferedOutputStream!!.write(Uilts.shortArrayToByteArray(formatShortArray))
        }
    }

    fun stop() {
        if (isPlay) {
            isPlay = false
            cancelDecode()
            audioTrack.stop()
            audioTrack.release()
            if (filePcmBufferedOutputStream != null) {
                filePcmBufferedOutputStream!!.close()
                filePcmBufferedOutputStream = null
            }
            if (filePcmOutputStream != null) {
                filePcmOutputStream!!.close()
                filePcmOutputStream = null
            }
        }
    }

    override fun run() {
        audioTrack.play()
        isPlay = true
        if (!isPCM) {
            decodeOpusFile(opusAudioPath, false)
        } else {
            readFile(opusAudioPath, false)
        }
        if (isPlay) {
            stop()
            if (onOpusPlayListener != null) {
                onOpusPlayListener!!.onCompere()
            }
        }
    }
}