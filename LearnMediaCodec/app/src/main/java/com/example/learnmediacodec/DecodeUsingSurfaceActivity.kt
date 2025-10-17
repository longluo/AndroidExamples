package com.example.learnmediacodec

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class DecodeUsingSurfaceActivity : AppCompatActivity() {
    private val TAG = "DecodeUsingSurface"
    private var stopDecoding = false
    private var thread0: Thread? = null
    private var startTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_using_surface)

        val surfaceView = findViewById<SurfaceView>(R.id.surface_view)

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                stopDecodingThread()

                thread0 = Thread {
                    stopDecoding = false
                    decodeToSurfaceAsync(holder.surface) // or decodeToSurface(holder.surface)
                }
                thread0?.start()
                startTime = System.nanoTime()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopDecodingThread()
            }
        })

    }

    private fun stopDecodingThread() {
        stopDecoding = true
        thread0?.join()
    }

    private fun decodeToSurface(surface: Surface){
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
        // configure with surface
        codec.configure(videoFormat, surface, null, 0)

        // start decoding
        val maxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        val inputBuffer = ByteBuffer.allocate(maxInputSize)
        val bufferInfo = MediaCodec.BufferInfo()
        val timeoutUs = 10000L // 10ms
        var inputEnd = false
        var outputEnd = false

        codec.start()
        while (!outputEnd && !stopDecoding) {
            val isExtractorReadEnd =
                getInputBufferFromExtractor(mediaExtractor, inputBuffer, bufferInfo)
            if (isExtractorReadEnd) {
                inputEnd = true
            }

            // get codec input buffer and fill it with data from extractor
            // timeoutUs is -1L means wait forever
            val inputBufferId = codec.dequeueInputBuffer(-1L)
            if (inputBufferId >= 0) {
                if (inputEnd) {
                    codec.queueInputBuffer(inputBufferId, 0, 0, 0,
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
                        0
                    )
                }
            }

            // get output buffer from codec and render it to image view
            // NOTE! dequeueOutputBuffer with -1L is will stuck here,  so wait 10ms here
            val outputBufferId = codec.dequeueOutputBuffer(bufferInfo, timeoutUs)
            if (outputBufferId >= 0) {
                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    outputEnd = true
                }
                if (bufferInfo.size > 0) {
                    val pts = bufferInfo.presentationTimeUs * 1000L + startTime
                    codec.releaseOutputBuffer(outputBufferId, pts)
                }
            }

            mediaExtractor.advance()
        }


        mediaExtractor.release()
        codec.stop()
        codec.release()
    }

    private fun decodeToSurfaceAsync(surface: Surface) {
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
                val isExtractorReadEnd =
                    getInputBufferFromExtractor(mediaExtractor, inputBuffer, bufferInfo)
                if (isExtractorReadEnd) {
                    inputEnd.set(true)
                    codec.queueInputBuffer(inputBufferId, 0, 0, 0,
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
                if(info.size > 0){
                    // render the decoded frame
                    val pts = info.presentationTimeUs * 1000L + startTime
                    codec.releaseOutputBuffer(outputBufferId, pts)
                }
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                e.printStackTrace()
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                // do nothing
            }
        })

        // configure with surface
        codec.configure(videoFormat, surface, null, 0)

        // start decoding
        codec.start()

        // wait for processing to complete
        while (!outputEnd.get() && !stopDecoding) {
            Thread.sleep(10)
        }

        mediaExtractor.release()
        codec.stop()
        codec.release()
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