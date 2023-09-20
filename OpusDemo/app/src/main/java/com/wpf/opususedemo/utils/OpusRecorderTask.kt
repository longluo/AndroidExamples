package com.wpf.opususedemo.utils

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.wpf.opususedemo.presenter.DecodeOpusPresenter.Companion.BUFFER_LENGTH
import com.wpf.opususedemo.presenter.DecodeOpusPresenter.Companion.DEFAULT_AUDIO_SAMPLE_RATE
import com.wpf.opususedemo.presenter.DecodeOpusPresenter.Companion.DEFAULT_OPUS_CHANNEL
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class OpusRecorderTask(private val opusAudioOpusPath: String, private val opusAudioPcmPath: String) : Runnable {
    
    companion object {
        private var channelConfig = AudioFormat.CHANNEL_IN_MONO
        val bufferSize: Int = AudioRecord.getMinBufferSize(DEFAULT_AUDIO_SAMPLE_RATE, channelConfig, AudioFormat.ENCODING_PCM_16BIT)
    }

    private var audioRecord: AudioRecord
    private var isRecorder = false
    private var audioBuffer: ByteArray = ByteArray(BUFFER_LENGTH * 8)//每次编码640的数据也就是10ms

    init {
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_AUDIO_SAMPLE_RATE, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
    }

    fun stop() {
        isRecorder = false
    }

    override fun run() {
        isRecorder = true
        audioRecord.startRecording()
        val file = File(opusAudioOpusPath)
        val filePcm = File(opusAudioPcmPath)
        val fileDir = File(file.parent)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        if (file.exists()) {
            file.delete()
        }
        if (filePcm.exists()) {
            filePcm.delete()
        }
        file.createNewFile()
        filePcm.createNewFile()
        val fileOutputStream = FileOutputStream(file, true)
        val filePcmOutputStream = FileOutputStream(filePcm, true)

        val fileOpusBufferedOutputStream = BufferedOutputStream(fileOutputStream)//默认buffer大小8192
        val filePcmBufferedOutputStream = BufferedOutputStream(filePcmOutputStream)

        val opusUtils = OpusUtils()
        val createEncoder = opusUtils.createEncoder(DEFAULT_AUDIO_SAMPLE_RATE, DEFAULT_OPUS_CHANNEL, 3)

        while (isRecorder) {
            val curShortSize = audioRecord.read(audioBuffer, 0, audioBuffer.size)
            if (curShortSize > 0 && curShortSize <= audioBuffer.size) {
                filePcmBufferedOutputStream.write(audioBuffer)//同时保存PCM以对比检查问题

                val byteArray = ByteArray(audioBuffer.size / 8)//编码后大小减小8倍
                val encodeSize = opusUtils.encode(createEncoder,
                    Uilts.byteArrayToShortArray(audioBuffer), 0, byteArray)
                if (encodeSize > 0) {
                    val decodeArray = ByteArray(encodeSize)
                    System.arraycopy(byteArray, 0, decodeArray, 0, encodeSize)
                    fileOpusBufferedOutputStream.write(decodeArray)//写入OPUS
                } else {

                }
            }
        }
        opusUtils.destroyEncoder(createEncoder)
        audioRecord.stop()
        audioRecord.release()
        filePcmBufferedOutputStream.close()
        filePcmOutputStream.close()
        fileOpusBufferedOutputStream.close()
        fileOutputStream.close()
    }

}