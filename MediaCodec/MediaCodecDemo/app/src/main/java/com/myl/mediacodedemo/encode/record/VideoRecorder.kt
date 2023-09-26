package com.myl.mediacodedemo.encode.record

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.myl.mediacodedemo.encode.VideoEncoder
import com.myl.mediacodedemo.encode.gl.EglCore
import com.myl.mediacodedemo.encode.gl.GLImageFilter
import com.myl.mediacodedemo.encode.gl.WindowSurface
import com.myl.mediacodedemo.utils.OpenGLUtils
import com.myl.mediacodedemo.utils.TextureRotationUtils
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.FloatBuffer

class VideoRecorder : Runnable, VideoEncoder.OnEncodingListener {

    companion object {
        private const val TAG = "VideoRecorder"

        // 开始录制
        private const val MSG_START_RECORDING = 0

        // 停止录制
        private const val MSG_STOP_RECORDING = 1

        // 录制帧可用
        private const val MSG_FRAME_AVAILABLE = 2

        // 退出录制
        private const val MSG_QUIT = 3
    }

    // 录制状态监听器
    var mRecordListener: OnRecordListener? = null
    private var mRunning = false
    private var mFirstTime = 0L // 录制开始的时间，方便开始录制

    // 录制Handler;
    private var mHandler: RecordHandler? = null

    // 视频编码器
    private var mVideoEncoder: VideoEncoder? = null

    // 录制状态锁
    private val recordLock = Object()
    private var mVertexBuffer: FloatBuffer? = null
    private var mTextureBuffer: FloatBuffer? = null
    private var mImageFilter: GLImageFilter? = null
    private var mEglCore: EglCore? = null
    private var mReady = false

    // 录制用的OpenGL上下文和EGLSurface
    private var mInputWindowSurface: WindowSurface? = null

    override fun run() {
        Looper.prepare()
        synchronized(recordLock) {
            mHandler = RecordHandler(this)
            mReady = true
            recordLock.notify()
        }
        Looper.loop()

        Log.d(TAG, "Video record thread exiting")
        synchronized(recordLock) {
            mReady = false
            mRunning = false
            mHandler = null
        }
    }

    /**
     * 开始录制
     * @param params 录制参数
     */
    fun startRecord(params: VideoConfig) {
        Log.d(TAG, "VideoRecorder: startRecord()")
        synchronized(recordLock) {
            if (mRunning) {
                Log.w(TAG, "VideoRecorder thread already running")
                return
            }
            mRunning = true
            Thread(this, TAG).start()
            while (!mReady) {
                try {
                    recordLock.wait()
                } catch (ie: InterruptedException) {
                    // ignore
                }
            }
        }
        mFirstTime = -1
        Log.d(TAG, "mHandler:$mHandler")
        mHandler?.apply {
            sendMessage(obtainMessage(MSG_START_RECORDING, params))
        }
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        mHandler?.apply {
            sendMessage(obtainMessage(MSG_STOP_RECORDING))
            sendMessage(obtainMessage(MSG_QUIT))
        }
    }

    /**
     * 开始录制
     * @param params
     */
    private fun onStartRecord(params: VideoConfig) {
        Log.d(TAG, "onStartRecord $params")
        mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)
        try {
            mVideoEncoder = VideoEncoder(params, this)
            Log.d(TAG, "mVideoEncoder init")
        } catch (ioe: IOException) {
            throw java.lang.RuntimeException(ioe)
        }
        // 创建EGL上下文和Surface
        mEglCore = EglCore(params.eglContext, EglCore.FLAG_RECORDABLE)
        mInputWindowSurface = WindowSurface(mEglCore, mVideoEncoder!!.mInputSurface, true)
        mInputWindowSurface?.makeCurrent()
        // 创建录制用的GL
        mImageFilter = GLImageFilter()
        mImageFilter?.onInputSizeChanged(params.videoWidth, params.videoHeight)
        mImageFilter?.onDisplaySizeChanged(params.videoWidth, params.videoHeight)
        // 录制开始回调
        mRecordListener?.onRecordStart(MediaType.VIDEO)
    }

    /**
     * 停止录制
     */
    private fun onStopRecord() {
        if (mVideoEncoder == null) {
            return
        }
        Log.d(TAG, "onStopRecord")
        mVideoEncoder?.drainEncoder(true)
        mVideoEncoder?.release()
        mImageFilter?.release()
        mImageFilter = null
        mInputWindowSurface?.release()
        mInputWindowSurface = null
        mEglCore?.release()
        mEglCore = null
        // 录制完成回调
        mRecordListener?.onRecordFinish(
            RecordInfo(
                mVideoEncoder!!.mVideoParams.videoPath,
                mVideoEncoder!!.mDuration, MediaType.VIDEO
            )
        )
        mVideoEncoder = null
    }

    /**
     * 录制Handler
     */
    private class RecordHandler(encoder: VideoRecorder) : Handler() {
        private val mWeakRecorder: WeakReference<VideoRecorder> = WeakReference(encoder)
        override fun handleMessage(inputMessage: Message) {
            val what = inputMessage.what
            val obj = inputMessage.obj
            val encoder = mWeakRecorder.get()
            if (encoder == null) {
                Log.w(TAG, "RecordHandler.handleMessage: encoder is null")
                return
            }
            when (what) {
                MSG_START_RECORDING -> {
                    encoder.onStartRecord(obj as VideoConfig)
                }
                MSG_STOP_RECORDING -> {
                    encoder.onStopRecord()
                }
                MSG_FRAME_AVAILABLE -> {
                    val timestamp = inputMessage.arg1.toLong() shl 32 or
                            (inputMessage.arg2.toLong() and 0xffffffffL)
                    encoder.onRecordFrameAvailable(obj as Int, timestamp)
                }
                MSG_QUIT -> {
                    Looper.myLooper()!!.quit()
                }
                else -> throw RuntimeException("Unhandled msg what=$what")
            }
        }
    }

    /**
     * 录制帧可用
     * @param texture
     * @param timestampNanos
     */
    private fun onRecordFrameAvailable(texture: Int, timestampNanos: Long) {
        Log.d(TAG, "onRecordFrameAvailable mVideoEncoder:$mVideoEncoder")
        if (mVideoEncoder == null) {
            return
        }
        drawFrame(texture, timestampNanos)
    }

    /**
     * 绘制编码一帧数据
     * @param texture
     * @param timestampNanos
     */
    private fun drawFrame(texture: Int, timestampNanos: Long) {
        mInputWindowSurface!!.makeCurrent()
        mImageFilter!!.drawFrame(texture, mVertexBuffer!!, mTextureBuffer!!)
        mInputWindowSurface!!.setPresentationTime(timestampNanos)
        mInputWindowSurface!!.swapBuffers()
        mVideoEncoder!!.drainEncoder(false)
    }

    override fun onEncoding(duration: Long) {
        mRecordListener?.onRecording(MediaType.VIDEO, duration)
    }

    /**
     * 释放所有资源
     */
    fun release() {
        mHandler?.apply {
            sendMessage(obtainMessage(MSG_QUIT))
        }
    }

    /**
     * 录制帧可用状态
     * @param texture
     * @param timestamp
     */
    fun frameAvailable(texture: Int, timestamp: Long) {
        synchronized(recordLock) {
            if (!mReady) {
                return
            }
        }
        // 时间戳为0时，不可用
        if (timestamp == 0L) {
            return
        }
        mHandler?.apply {
            sendMessage(
                obtainMessage(
                    MSG_FRAME_AVAILABLE,
                    (timestamp shr 32).toInt(), timestamp.toInt(), texture
                )
            )
        }
    }

    /**
     * 判断是否正在录制
     * @return
     */
    fun isRecording(): Boolean {
        synchronized(recordLock) { return mRunning }
    }
}