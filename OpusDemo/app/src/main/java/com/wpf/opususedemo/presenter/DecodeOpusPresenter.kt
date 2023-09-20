/*
 * Coding by Zhonghua. from 18-9-14 下午6:12.
 */

package com.wpf.opususedemo.presenter

import android.util.Log
import com.wpf.opususedemo.utils.OpusUtils
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


interface DecodeOpusPresenter {

    companion object {
        private val TAG: String get() = DecodeOpusPresenter::class.java.simpleName
        const val BUFFER_LENGTH = 80
        private var isCancel = false
        private var decodeOpusFilePath: String? = null
        const val DEFAULT_AUDIO_SAMPLE_RATE = 16000
        const val DEFAULT_OPUS_CHANNEL = 1//默认采用单声道
    }

    fun decodeOpusFile(path: String, newThreadRun: Boolean = true) {
        Log.i(TAG, "decodeOpusFile: $path, newThreadRun: $newThreadRun")
        decodeOpusFilePath = path
        isCancel = false
        if (!newThreadRun) {
            opusFileDecoder()
        } else {
            Thread(Runnable { opusFileDecoder() }).start()
        }
    }

    fun readFile(path: String, newThreadRun: Boolean = true) {
        Log.i(TAG, "decodeOpusFile: $path, newThreadRun: $newThreadRun")
        decodeOpusFilePath = path
        isCancel = false
        if (!newThreadRun) {
            opusFileDecoder(false)
        } else {
            Thread(Runnable { opusFileDecoder(false) }).start()
        }
    }

    fun cancelDecode() {
        isCancel = true
    }

    private fun opusFileDecoder(needDecoder: Boolean = true) {
        if (decodeOpusFilePath.isNullOrEmpty()) {
            opusDecodeFinish()
            return
        }
        val tntOpusUtils = OpusUtils.getInstant()
        val decoderHandler = tntOpusUtils.createDecoder(DEFAULT_AUDIO_SAMPLE_RATE, DEFAULT_OPUS_CHANNEL)

        val fis: FileInputStream
        try {
            fis = FileInputStream(decodeOpusFilePath)
        } catch (e: Exception) {
            opusDecodeFinish()
            return
        }

        val bis = BufferedInputStream(fis)
        while (!isCancel) {
            val bufferArray = ByteArray(BUFFER_LENGTH)
            var read: Int = -1
            try {
                read = bis.read(bufferArray, 0, bufferArray.size)
            } catch (e: Exception) {
            }

            if (read < 0) {//已经读完了
                Log.i(TAG, "OpusFileDecoder compare")
                break
            } else {
                if (needDecoder) {
                    val decodeBufferArray = ShortArray(bufferArray.size * 4)
                    val size = tntOpusUtils.decode(decoderHandler, bufferArray, decodeBufferArray)
                    if (size > 0) {
                        val decodeArray = ShortArray(size)
                        System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size)
                        opusDecode(decodeArray)//输出数据到接口
                    } else {
                        Log.e(TAG, "opusDecode error : $size")
                        break
                    }
                } else {
                    opusDecode(byteArrayToShortArray(bufferArray))
                }
            }
        }
        tntOpusUtils.destroyDecoder(decoderHandler)
        bis.close()
        fis.close()
        opusDecodeFinish()
    }

    fun byteArrayToShortArray(byteArray: ByteArray): ShortArray {
        val shortArray = ShortArray(byteArray.size / 2)
        ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder()).asShortBuffer().get(shortArray)
        return shortArray
    }

    fun opusDecode(formatShortArray: ShortArray) {}
    fun opusDecodeFinish() {}
}