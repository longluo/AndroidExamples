package com.example.learnmediacodec

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.opengl.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean

class DecodeToTextureOESActivity : AppCompatActivity(), SurfaceTexture.OnFrameAvailableListener {
    private val TAG = "DecodeEditActivity"
    private var mEGLHelper = EGLHelper()
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mOutputSurface: Surface? = null
    private var count = 0
    private var thread: HandlerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_to_texture_oes)

        val btn = findViewById<Button>(R.id.btn_start_decoding_to_texture_oes_async)
        btn.setOnClickListener {
            Thread {
                decodeToSurfaceAsync()
            }.start()
        }

    }

    private fun decodeToSurfaceAsync() {
        val width = 720
        val height = 1280
        mEGLHelper.setupEGL(width, height)
        mEGLHelper.makeCurrent()
        count = 0

        // allocate texture id
        val numTexId = 1
        val textureHandles = IntArray(numTexId)
        GLES20.glGenTextures(numTexId, textureHandles, 0)
        checkGlError("glGenTextures")

        // bind texture id to oes and config it
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandles[0])
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        checkGlError("init oes texture")

        // create SurfaceTexture with texture id
        mSurfaceTexture = SurfaceTexture(textureHandles[0])

        thread = HandlerThread("FrameHandlerThread")
        thread!!.start()

//        mSurfaceTexture!!.setOnFrameAvailableListener(this)

        mSurfaceTexture!!.setOnFrameAvailableListener({
            synchronized(lock) {

                // New frame available before the last frame was process...we dropped some frames
                if (frameAvailable)
                    Log.d(TAG, "Frame available before the last frame was process...we dropped some frames")

                frameAvailable = true
                lock.notifyAll()
            }
        }, Handler(thread!!.looper))

        // create Surface With SurfaceTexture
        mOutputSurface = Surface(mSurfaceTexture)

        // create and configure media extractor
        val mediaExtractor = MediaExtractor()
        resources.openRawResourceFd(R.raw.h264_720p).use {
            mediaExtractor.setDataSource(it)
        }
        val videoTrackIndex = 0
        mediaExtractor.selectTrack(videoTrackIndex)
        val videoFormat = mediaExtractor.getTrackFormat(videoTrackIndex)

        // create and configure media codec
        val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val codecName = codecList.findDecoderForFormat(videoFormat)
        val codec = MediaCodec.createByCodecName(codecName)

        val maxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        val inputBuffer = ByteBuffer.allocate(maxInputSize)
        val bufferInfo = MediaCodec.BufferInfo()
        val inputEnd = AtomicBoolean(false)
        val outputEnd = AtomicBoolean(false)

        // set codec callback in async mode
        codec.setCallback(object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, inputBufferId: Int) {
                Log.d(TAG, "onInputBufferAvailable")
                val isExtractorReadEnd =
                    getInputBufferFromExtractor(mediaExtractor, inputBuffer, bufferInfo)
                if (isExtractorReadEnd) {
                    inputEnd.set(true)
                    codec.queueInputBuffer(
                        inputBufferId, 0, 0, 0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                } else {
                    val codecInputBuffer = codec.getInputBuffer(inputBufferId)
                    codecInputBuffer!!.put(inputBuffer)
                    codec.queueInputBuffer(
                        inputBufferId,
                        0,
                        bufferInfo.size,
                        bufferInfo.presentationTimeUs,
                        bufferInfo.flags
                    )
                    mediaExtractor.advance()
                }
            }

            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                outputBufferId: Int,
                info: MediaCodec.BufferInfo
            ) {
                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    outputEnd.set(true)
                }
                if (info.size > 0) {
                    Log.i(TAG, "onOutputBufferAvailable")
                    codec.releaseOutputBuffer(outputBufferId, true)

                    waitTillFrameAvailable()
                    mEGLHelper.makeCurrent()
                    mSurfaceTexture!!.updateTexImage()
                    saveTextureToImage(textureHandles[0], width, height, count)
                    count++
                }
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                e.printStackTrace()
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                Log.e(TAG, "onOutputFormatChanged")
            }
        })

        // configure with surface
        codec.configure(videoFormat, mOutputSurface, null, 0)

        // release EGL context in this thread
        mEGLHelper.releaseEGL()

        // start decoding
        codec.start()

        // wait for processing to complete
        while (!outputEnd.get() && count < 10) {
            Log.i(TAG, "count: $count")
            Thread.sleep(10)
        }

        mediaExtractor.release()
        codec.stop()
        codec.release()
    }

    private fun saveTextureToImage(textureId: Int, width: Int, height: Int, count: Int) {
        // 创建一个Bitmap来保存图像数据
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 创建一个FrameBuffer，并将纹理绑定到FrameBuffer上
        val frameBuffer = IntArray(1)
        GLES20.glGenFramebuffers(1, frameBuffer, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0])
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId, 0)

        // 创建一个buffer来保存从FrameBuffer中读取的像素数据
        val buffer = ByteBuffer.allocateDirect(width * height * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        // 从FrameBuffer中读取像素数据
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)
        buffer.rewind()

        // 将buffer中的数据复制到Bitmap中
        bitmap.copyPixelsFromBuffer(buffer)

        // 解绑FrameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glDeleteFramebuffers(1, frameBuffer, 0)

        // 保存Bitmap到文件
        val file = File(externalCacheDir, "texture_$count.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
    }

    @SuppressLint("WrongConstant")
    private fun getInputBufferFromExtractor(
        mediaExtractor: MediaExtractor,
        inputBuffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo
    ): Boolean {
        val sampleSize = mediaExtractor.readSampleData(inputBuffer, 0)
        if (sampleSize < 0) {
            return true
        }

        bufferInfo.size = sampleSize
        bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
        bufferInfo.offset = 0
        bufferInfo.flags = mediaExtractor.sampleFlags

        return false
    }


    fun checkGlError(op: String) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Log.e(
                TAG,
                "$op: glError $error"
            )
            throw java.lang.RuntimeException("$op: glError $error")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mOutputSurface!!.release()
        mEGLHelper.release()
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        Log.d(TAG, "onFrameAvailable")
        synchronized(lock) {
            frameAvailable = true
            lock.notifyAll()
        }
    }

    private val lock = Object()

    @Volatile
    private var frameAvailable = false
    private fun waitTillFrameAvailable() {
        synchronized(lock) {
            while (!frameAvailable) {
                lock.wait(500)
                if (!frameAvailable)
                    Log.e(TAG, "Surface frame wait timed out")
            }
            frameAvailable = false
        }
    }

    inner class EGLHelper {
        private val TAG = "EGLHelper"

        private var mEGLDisplay = EGL14.EGL_NO_DISPLAY
        private var mEGLSurface = EGL14.EGL_NO_SURFACE
        private var mEGLContext: EGLContext? = null

        fun setupEGL(width: Int, height: Int) {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
                getError()
                throw java.lang.RuntimeException("unable to get EGL14 display")
            }
            checkEglError("eglGetDisplay")

            val version = IntArray(2)
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                mEGLDisplay = null
                getError()
                throw java.lang.RuntimeException("unable to initialize EGL14")
            }
            checkEglError("eglInitialize")


            // Configure EGL for pbuffer and OpenGL ES 2.0.  We want enough RGB bits
            // to be able to tell if the frame is reasonable.
            val attribList = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGLExt.EGL_RECORDABLE_ANDROID, 1,
                EGL14.EGL_NONE
            )

            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            if (!EGL14.eglChooseConfig(
                    mEGLDisplay,
                    attribList,
                    0,
                    configs,
                    0,
                    configs.size,
                    numConfigs,
                    0
                )
            ) {
                getError()
                throw RuntimeException("eglChooseConfig failed")
            }
            checkEglError("eglCreateContext RGB888+recordable ES2")


            // 创建 EGLContext
            val contextAttrs = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
            )
            mEGLContext =
                EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT, contextAttrs, 0)
            if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
                getError()
                throw RuntimeException("eglCreateContext failed")
            }

            // 创建 EGLSurface
            val surfaceAttrib = intArrayOf(
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
            )
            mEGLSurface = EGL14.eglCreatePbufferSurface(mEGLDisplay, configs[0], surfaceAttrib, 0)
            if (mEGLSurface == EGL14.EGL_NO_SURFACE) {
                getError()
                throw RuntimeException("eglCreateWindowSurface failed")
            }
        }

        fun makeCurrent() {
            if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
                getError()
                throw RuntimeException("eglMakeCurrent failed")
            }
        }

        fun releaseEGL() {
            if (!EGL14.eglMakeCurrent(
                    mEGLDisplay,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT
                )
            ) {
                throw java.lang.RuntimeException("eglMakeCurrent failed")
            }
        }

        fun release() {
            if (EGL14.eglGetCurrentContext() == mEGLContext) {
                // Clear the current context and surface to ensure they are discarded immediately.
                EGL14.eglMakeCurrent(
                    mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT
                )
            }
            EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface)
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext)
            EGL14.eglTerminate(mEGLDisplay)

            mEGLDisplay = null
            mEGLContext = null
            mEGLSurface = null
        }

        private fun checkEglError(msg: String) {
            var failed = false
            var error: Int
            while (EGL14.eglGetError().also { error = it } != EGL14.EGL_SUCCESS) {
                Log.e(TAG, msg + ": EGL error: 0x" + Integer.toHexString(error))
                failed = true
            }
            if (failed) {
                throw RuntimeException("EGL error encountered (see log)")
            }
        }

        private fun getError() {
            val errorCode = EGL14.eglGetError()
            when (errorCode) {
                EGL14.EGL_SUCCESS -> println("函数执行成功，无错误---没有错误")
                EGL14.EGL_NOT_INITIALIZED -> println("对于特定的 Display, EGL 未初始化，或者不能初始化---没有初始化")
                EGL14.EGL_BAD_ACCESS -> println("EGL 无法访问资源(如 Context 绑定在了其他线程)---访问失败")
                EGL14.EGL_BAD_ALLOC -> println("对于请求的操作，EGL 分配资源失败---分配失败")
                EGL14.EGL_BAD_ATTRIBUTE -> println("未知的属性，或者属性已失效---错误的属性")
                EGL14.EGL_BAD_CONTEXT -> println("EGLContext(上下文) 错误或无效---错误的上下文")
                EGL14.EGL_BAD_CONFIG -> println("EGLConfig(配置) 错误或无效---错误的配置")
                EGL14.EGL_BAD_DISPLAY -> println("EGLDisplay(显示) 错误或无效---错误的显示设备对象")
                EGL14.EGL_BAD_SURFACE -> println("未知的属性，或者属性已失效---错误的Surface对象")
                EGL14.EGL_BAD_CURRENT_SURFACE -> println("窗口，缓冲和像素图(三种 Surface)的调用线程的 Surface 错误或无效---当前Surface对象错误")
                EGL14.EGL_BAD_MATCH -> println("参数不符(如有效的 Context 申请缓冲，但缓冲不是有效的 Surface 提供)---无法匹配")
                EGL14.EGL_BAD_PARAMETER -> println("错误的参数")
                EGL14.EGL_BAD_NATIVE_PIXMAP -> println("NativePixmapType 对象未指向有效的本地像素图对象---错误的像素图")
                EGL14.EGL_BAD_NATIVE_WINDOW -> println("NativeWindowType 对象未指向有效的本地窗口对象---错误的本地窗口对象")
                EGL14.EGL_CONTEXT_LOST -> println("电源错误事件发生，Open GL重新初始化，上下文等状态重置---上下文丢失")
                else -> {}
            }
        }
    }
}

