package com.zhengsr.opengldemo.codec.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import com.zhengsr.opengldemo.MainApplication
import com.zhengsr.opengldemo.utils.getRealHeight
import com.zhengsr.opengldemo.utils.getRealWidth
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingDeque

/**
 * @author by zhengshaorui 2022/12/26
 * describe：视频解码
 */
class VideoDncoder {

    companion object {
        internal val instance: VideoDncoder by lazy { VideoDncoder() }
        private const val MSG_INIT = 1;
        private const val MSG_QUERY = 2;
        private const val DECODE_NAME = "video/avc"
        private const val TAG = "VideoEncoder"
    }

    private var handleThread: HandlerThread? = null
    private var handler: Handler? = null
    private var surface: Surface? = null
    private var decoder: MediaCodec? = null
    private val indexQueue = LinkedBlockingDeque<Int>();
    private val handlerCallback = Handler.Callback { msg ->
        when (msg.what) {
            MSG_INIT -> {
                configAndStart()
            }

            MSG_QUERY -> {

                // handler?.sendEmptyMessageDelayed(MSG_QUERY, 10)
            }
        }
        false
    }
    private var listener: IDecoderListener? = null
    fun start(surface: Surface, iDecoderListener: IDecoderListener) {
        listener = iDecoderListener
        this.surface = surface
        if (handleThread == null) {
            handleThread = HandlerThread("VideoEncoder").apply {
                start()
                handler = Handler(this.looper, handlerCallback)
            }
        }
        handler?.let {
            it.removeMessages(MSG_INIT)
            it.sendEmptyMessage(MSG_INIT)
        }
    }

    /**
     * 喂数据
     */
    fun feedData(buffer: ByteArray, offset: Int, length: Int) {
        val index = indexQueue.take()
        if (index != -1) {
            decoder?.let {
                it.getInputBuffer(index)?.apply {
                    clear()
                    val time = System.nanoTime() / 1000000
                    put(buffer, offset, length)


                    it.queueInputBuffer(index, 0, length, time, 0)
                }

            }
        }
    }

    private fun configAndStart() {
        var width = getRealWidth(MainApplication.context)
        var height = getRealHeight(MainApplication.context)
        if (null == surface || !surface!!.isValid || width < 1 || height < 1) {
            throw IllegalArgumentException("Some argument is invalid");
        }
        Log.d(TAG, "configAndStart() called: $width,$height")
        decoder = MediaCodec.createDecoderByType(DECODE_NAME)
        val format = MediaFormat()
        format.setString(MediaFormat.KEY_MIME, DECODE_NAME)
        format.setInteger(MediaFormat.KEY_WIDTH, width)
        format.setInteger(MediaFormat.KEY_HEIGHT, height)
        decoder?.let {
            it.reset()
            it.configure(format, surface, null, 0)
            it.setCallback(decodeCallback)
            it.start()
            Log.d(TAG, "解码器启动成功")
            listener?.onReady()
            handler?.sendEmptyMessage(MSG_QUERY)
        }
    }

    public interface IDecoderListener {
        fun onReady()
    }

    private val decodeCallback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
            indexQueue.add(index)
        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {

            decoder?.releaseOutputBuffer(index, true)
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            Log.e(TAG, "onError() called with: codec = $codec, e = $e")
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            var width = format.getInteger(MediaFormat.KEY_WIDTH)
            if (format.containsKey("crop-left") && format.containsKey("crop-right")) {
                width = format.getInteger("crop-right") + 1 - format.getInteger("crop-left")
            }
            var height = format.getInteger(MediaFormat.KEY_HEIGHT)
            if (format.containsKey("crop-top") && format.containsKey("crop-bottom")) {
                height = format.getInteger("crop-bottom") + 1 - format.getInteger("crop-top")
            }
            Log.d(TAG, "视频解码后的宽高：$width,$height")

        }

    }

    fun release() {
        handleThread?.quitSafely()
        handleThread = null
        handler = null
        surface?.release()
        try {
            decoder?.let {
                it.stop()
                it.release()
            }
        } catch (e: Exception) {
        }
    }


}