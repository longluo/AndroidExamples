package com.example.learnmediacodec

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.opengl.*
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.opengles.GL10

class DecodeEditPlay : AppCompatActivity(), SurfaceTexture.OnFrameAvailableListener {
    private val TAG = "DecodeEditPlay"
    private var mOutputSurface: Surface? = null
    private var mSurfaceTexture : SurfaceTexture? = null
    private var mTextureRenderer: TextureRenderer2? = null
    private var width = 0
    private var height = 0
    private val texMatrix = FloatArray(16)
    private val lock = Object()

    @Volatile
    private var frameAvailable = false

    inner class MyGLSurfaceRender : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(
            gl: GL10?,
            config: javax.microedition.khronos.egl.EGLConfig?
        ) {
            mTextureRenderer = TextureRenderer2()
            Matrix.setIdentityM(texMatrix, 0)

            // create a thread to decode video
            Thread {
                decodeSync()
            }.start()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            Log.d(TAG, "onSurfaceChanged")
        }

        override fun onDrawFrame(gl: GL10?) {
            Log.d(TAG, "onDrawFrame")
            if(frameAvailable){
                mSurfaceTexture?.updateTexImage()
                mSurfaceTexture?.getTransformMatrix(texMatrix)
            }

            // draw oes texture to screen
            mTextureRenderer?.draw(width, height, texMatrix, getMvp())
        }
    }

    private fun getMvp(): FloatArray {
        val mvp = FloatArray(16)
        Matrix.setIdentityM(mvp, 0)

        // Set your transformations here
        // Matrix.scaleM(mvp, 0, 1f, -1f, 1f)
        //

        return mvp
    }

    private var glRenderer = MyGLSurfaceRender()
    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_edit_play)
        Log.d(TAG, "onCreate")

        glSurfaceView = findViewById<GLSurfaceView>(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(glRenderer)
        Log.d(TAG, "config glSurfaceView")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun decodeSync() {
        mSurfaceTexture = SurfaceTexture(mTextureRenderer!!.texId)
        mSurfaceTexture!!.setOnFrameAvailableListener(this)
        mOutputSurface = Surface(mSurfaceTexture)

        // create and configure media extractor
        val mediaExtractor = MediaExtractor()
        resources.openRawResourceFd(R.raw.h264_720p).use {
            mediaExtractor.setDataSource(it)
        }
        val videoTrackIndex = 0
        mediaExtractor.selectTrack(videoTrackIndex)
        val videoFormat = mediaExtractor.getTrackFormat(videoTrackIndex)
        width = videoFormat.getInteger(MediaFormat.KEY_WIDTH)
        height = videoFormat.getInteger(MediaFormat.KEY_HEIGHT)

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

                    // sleep for 30ms to simulate 30fps
                    Thread.sleep(30)
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

        // start decoding
        codec.start()

        // wait for processing to complete
        while (!outputEnd.get()) {
            Thread.sleep(10)
        }

        mediaExtractor.release()
        codec.stop()
        codec.release()

        mOutputSurface!!.release()
        mOutputSurface = null
        mSurfaceTexture!!.release()
        mSurfaceTexture = null
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

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        Log.d(TAG, "onFrameAvailable")
        synchronized(lock) {
            frameAvailable = true
            lock.notifyAll()
        }
    }
}