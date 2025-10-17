package com.example.learnmediacodec

import android.annotation.SuppressLint
import android.graphics.*
import android.media.*
import android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean


class DecodeUsingBuffersActivity : AppCompatActivity() {
    private val TAG = "DecodeUsingBuffersActivity"
    private lateinit var imageView: ImageView
    private var thread0: Thread? = null
    private var thread1: Thread? = null
    private var stopDecoding = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_using_buffers)

        imageView = findViewById(R.id.imageview_decode_to_bitmap)
        val btnStartDecoding = findViewById<android.widget.Button>(R.id.btn_start_decoding)
        btnStartDecoding.setOnClickListener {
            stopDecodingThreads()
            // start a new thread for decoding so that we don't block the main thread
            thread0 = Thread {
                stopDecoding = false
                decodeToBitmap()
            }
            thread0?.start()
        }

        val btnStartDecodingAsync =
            findViewById<android.widget.Button>(R.id.btn_start_decoding_async)
        btnStartDecodingAsync.setOnClickListener {
            stopDecodingThreads()
            thread1 = Thread {
                stopDecoding = false
                decodeToBitmapAsync()
            }
            thread1?.start()
        }
    }

    private fun stopDecodingThreads() {
        stopDecoding = true
        thread0?.join()
        thread1?.join()
    }


    private fun decodeToBitmap() {
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
        // configure with null surface so that we can get decoded bitmap easily
        codec.configure(videoFormat, null, null, 0)

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
                    codec.queueInputBuffer(inputBufferId, 0, 0, 0, BUFFER_FLAG_END_OF_STREAM)
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
                if (bufferInfo.flags and BUFFER_FLAG_END_OF_STREAM != 0) {
                    outputEnd = true
                }
                if (bufferInfo.size > 0) {
                    // get output image from codec, is a YUV image
                    val outputImage = codec.getOutputImage(outputBufferId)
                    // convert YUV image to bitmap so that we can render it to image view
                    val bitmap = yuvImage2Bitmap(outputImage!!)
                    // post to main thread to update image view
                    imageView.post {
                        imageView.setImageBitmap(bitmap)
                    }
                    // remember to release output buffer after rendering
                    codec.releaseOutputBuffer(outputBufferId, false)
                    // sleep 30ms to simulate 30fps
                    Thread.sleep(30)
                }
            }

            mediaExtractor.advance()
        }


        mediaExtractor.release()
        codec.stop()
        codec.release()
    }

    private fun decodeToBitmapAsync() {
        // create and configure media extractor
        val mediaExtractor = MediaExtractor()
        resources.openRawResourceFd(R.raw.h264_4k_30).use {
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
                    codec.queueInputBuffer(inputBufferId, 0, 0, 0, BUFFER_FLAG_END_OF_STREAM)
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
                if (info.flags and BUFFER_FLAG_END_OF_STREAM != 0) {
                    outputEnd.set(true)
                }

                if(info.size > 0){
                    val outputImage = codec.getOutputImage(outputBufferId)
                    val bitmap = yuvImage2Bitmap(outputImage!!)
                    runOnUiThread{
                        imageView.setImageBitmap(bitmap)
                    }
                    codec.releaseOutputBuffer(outputBufferId, false)
                    Thread.sleep(30)
                }
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                e.printStackTrace()
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                // do nothing
            }
        })

        codec.configure(videoFormat, null, null, 0)
        codec.start()

        // wait for processing to complete
        while (!outputEnd.get() && !stopDecoding) {
            Thread.sleep(10)
        }

        mediaExtractor.release()
        codec.stop()
        codec.release()
    }



    private fun yuvImage2Bitmap(image: Image): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return bitmap
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