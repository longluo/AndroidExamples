package com.example.learnmediacodec

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.media.*
import android.opengl.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class DecodeEditEncodeActivity : AppCompatActivity() {
    private val lock = Object()
    private var frameAvailable = false
    private val TAG = "DecodeEditEncodeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_edit_encode)

        val btnStart = findViewById<Button>(R.id.btn_start_decode_edit_encode)
        btnStart.setOnClickListener {
            Thread{
                decodeASync()
            }.start()
        }
    }

    private fun waitTillFrameAvailable(){
        synchronized(lock) {
            while (!frameAvailable) {
                lock.wait(500)
                if (!frameAvailable)
                    Log.e(TAG, "Surface frame wait timed out")
            }
            frameAvailable = false
        }
    }

    private fun decodeASync() {
        var done = AtomicBoolean(false)
        // setup extractor
        val mediaExtractor = MediaExtractor()
        resources.openRawResourceFd(R.raw.h264_720p).use {
            mediaExtractor.setDataSource(it)
        }
        val videoTrackIndex = 0
        mediaExtractor.selectTrack(videoTrackIndex)
        val inputVideoFormat = mediaExtractor.getTrackFormat(videoTrackIndex)
        val videoWidth = inputVideoFormat.getInteger(MediaFormat.KEY_WIDTH)
        val videoHeight = inputVideoFormat.getInteger(MediaFormat.KEY_HEIGHT)
        Log.i(TAG, "get video width: $videoWidth, height: $videoHeight")


        // setup muxer
        val outputDir = externalCacheDir
        val outputName = "decode_edit_encode_test.mp4"
        val outputFile = File(outputDir, outputName)
        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var muxerSelectVideoTrackIndex = 0

        // create encoder
        val mimeType = MediaFormat.MIMETYPE_VIDEO_AVC
        val outputFormat = MediaFormat.createVideoFormat(mimeType, videoWidth, videoHeight)
        val colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface

        val videoBitrate = 2000000
        val frameRate = 30
        val iFrameInterval = 60
        outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
        outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, videoBitrate)
        outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        outputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)

        val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val encodeCodecName = codecList.findEncoderForFormat(outputFormat)
        val encoder = MediaCodec.createByCodecName(encodeCodecName)
        Log.i(TAG, "create encoder with format: $outputFormat")

        // set encoder callback
        encoder.setCallback(object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                Log.d(TAG, "encoder input buffer available: $index")
            }

            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                Log.d(TAG, "encoder output buffer available: $index")
                val isEncodeDone = (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                if (isEncodeDone) {
                    Log.d(TAG, "encoder output eos")
                    info.size = 0
                    done.set(true)
                }

                // got encoded frame, write it to muxer
                if (info.size > 0) {
                    val encodedData = codec.getOutputBuffer(index)
                    muxer.writeSampleData(muxerSelectVideoTrackIndex, encodedData!!, info)
                    Log.d(
                        TAG,
                        "encoder output buffer: $index, size: ${info.size}, pts: ${info.presentationTimeUs}"
                    )
                    codec.releaseOutputBuffer(index, info.presentationTimeUs * 1000)
                    Log.d(TAG, "encoder release output buffer: $index")
                }
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                Log.e(TAG, "encoder error: $e")
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                Log.d(TAG, "encoder output format changed: $format")
                muxerSelectVideoTrackIndex = muxer.addTrack(format)
                muxer.start()
            }

        });

        encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        // create input surface and egl context for opengl rendering
        val inputSurface = InputSurface(encoder.createInputSurface())
        inputSurface.makeCurrent()

        // create decoder
        val decodeCodecName = codecList.findDecoderForFormat(inputVideoFormat)
        val decoder = MediaCodec.createByCodecName(decodeCodecName)

        // create output surface texture
        val textureRenderer = TextureRenderer2()
        val surfaceTexture = SurfaceTexture(textureRenderer.texId)
        val outputSurface = Surface(surfaceTexture)
        inputSurface.releaseEGLContext()

        val thread = HandlerThread("FrameHandlerThread")
        thread.start()

        surfaceTexture.setOnFrameAvailableListener({
            Log.d(TAG, "setOnFrameAvailableListener")
            synchronized(lock) {
                if (frameAvailable)
                    Log.d(
                        TAG,
                        "Frame available before the last frame was process...we dropped some frames"
                    )
                frameAvailable = true
                lock.notifyAll()
            }
        }, Handler(thread.looper))

        val texMatrix = FloatArray(16)

        // set callback
        val maxInputSize = inputVideoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        val inputBuffer = ByteBuffer.allocate(maxInputSize)
        val bufferInfo = MediaCodec.BufferInfo()
        // Create the decoder on a different thread, in order to have the callbacks there.
        // This makes sure that the blocking waiting and rendering in onOutputBufferAvailable
        // won't block other callbacks (e.g. blocking encoder output callbacks), which
        // would otherwise lead to the transcoding pipeline to lock up.

        // Since API 23, we could just do setCallback(callback, mVideoDecoderHandler) instead
        // of using a custom Handler and passing a message to create the MediaCodec there.
        val videoDecoderHandlerThread = HandlerThread("DecoderThread")
        videoDecoderHandlerThread.start()
        decoder.setCallback(object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, inputBufferId: Int) {
                Log.d(TAG, "decoder input buffer available: $inputBufferId")
                val isExtractorReadEnd =
                    getInputBufferFromExtractor(mediaExtractor, inputBuffer, bufferInfo)

                if (isExtractorReadEnd) {
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
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                Log.d(TAG, "decoder output buffer available: $index")
                if (info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    Log.d(TAG, "decoder MediaCodec.BUFFER_FLAG_CODEC_CONFIG")
                    codec.releaseOutputBuffer(index, false)
                    return
                }
                val render = info.size > 0
                codec.releaseOutputBuffer(index, render)
                if (render) {
                    waitTillFrameAvailable()
                    val ptsNs = info.presentationTimeUs * 1000
                    Log.d(TAG, "got frame: ${ptsNs}")
                    inputSurface.makeCurrent()
                    surfaceTexture.updateTexImage()
                    surfaceTexture.getTransformMatrix(texMatrix)

                    // draw oes text to input surface
                    Log.d(TAG, "draw texture to input surface...")
                    textureRenderer.draw(videoWidth, videoWidth, texMatrix, getMvp())

                    Log.d(TAG, "input surface set pts ${ptsNs}")
                    inputSurface.setPresentationTime(ptsNs)
                    Log.d(TAG, "input surface swap buffers")
                    inputSurface.swapBuffers()
                    Log.d(TAG, "input surface release egl context")
                    inputSurface.releaseEGLContext()
                }

                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    encoder.signalEndOfInputStream()
                }
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                Log.d(TAG, "decoder error: $e")
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                Log.d(TAG, "decoder output format changed: $format, input format:$inputVideoFormat")
            }

        }, Handler(videoDecoderHandlerThread.looper))

        // config decoder
        decoder.configure(inputVideoFormat, outputSurface, null, 0)
        decoder.start()
        encoder.start()

        // wait for done
        while(!done.get())
        {
            Thread.sleep(10)
        }
        Log.d(TAG, "finished")

        // release resources
        Log.d(TAG, "release resources...")
        mediaExtractor.release()

        decoder.stop()
        decoder.release()

        surfaceTexture.release()
        outputSurface.release()

        encoder.stop()
        encoder.release()

        muxer.stop()
        muxer.release()
        Log.d(TAG, "release resources end...")
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
}

private fun getMvp(): FloatArray {
    val mvp = FloatArray(16)
    Matrix.setIdentityM(mvp, 0)

    return mvp
}