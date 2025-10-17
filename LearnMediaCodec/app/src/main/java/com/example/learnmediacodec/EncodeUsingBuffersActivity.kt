package com.example.learnmediacodec

import android.media.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class EncodeUsingBuffersActivity : AppCompatActivity() {
    private val TAG = "EncodeUsingBuffersActivity"
    private val videoWidth = 1280
    private val videoHeight = 720
    private val videoBitrate = 2000000
    private var generateIndex = 0
    private val FRAME_RATE = 30
    private val IFRAME_INTERVAL = 5
    private val NUM_FRAMES = FRAME_RATE * 2
    private lateinit var muxer : MediaMuxer
    private var videoTrackIndex = 0
    private val outputEnd = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode_using_buffers)

        val btnStartEncodingAsync = findViewById<android.widget.Button>(R.id.btn_start_encoding_async)
        btnStartEncodingAsync.setOnClickListener {
            Thread {
                encodeUsingBuffersAsync()
            }.start()
        }
    }

    private fun encodeUsingBuffersAsync(){
        // create encoder
        val mimeType = MediaFormat.MIMETYPE_VIDEO_AVC
        val format = MediaFormat.createVideoFormat(mimeType, videoWidth, videoHeight)
        val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val encodeCodecName = codecList.findEncoderForFormat(format)
        val encoder = MediaCodec.createByCodecName(encodeCodecName)

        encoder.setCallback(object: MediaCodec.Callback(){

            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                val pts = computePresentationTime(generateIndex)
                Log.d(TAG, "encoder input buffer available: $index")
                // input eos
                if(generateIndex == NUM_FRAMES)
                {
                    codec.queueInputBuffer(index, 0, 0, pts, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                }else
                {
                    val frameData = ByteArray(videoWidth * videoHeight * 3 / 2)
                    generateFrame(generateIndex, codec.inputFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT), frameData)
                    val inputBuffer = codec.getInputBuffer(index)
                    assert(inputBuffer!!.capacity() >= frameData.size)
                    inputBuffer.put(frameData)
                    Log.d(TAG, "encoder input buffer: $index, size: ${frameData.size}, pts: $pts")
                    codec.queueInputBuffer(index, 0, frameData.size, pts, 0)
                    generateIndex++
                }
            }

            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                Log.d(TAG, "encoder output buffer available: $index")
                // output eos
                val isDone = (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                if(isDone)
                {
                    Log.d(TAG, "encoder output eos")
                    outputEnd.set(true)
                    info.size = 0
                }

                // got encoded frame, write it to muxer
                if(info.size > 0){
                    val encodedData = codec.getOutputBuffer(index)
                    muxer.writeSampleData(videoTrackIndex, encodedData!!, info)
                    Log.d(TAG, "encoder output buffer: $index, size: ${info.size}, pts: ${info.presentationTimeUs}")
                    codec.releaseOutputBuffer(index, false)
                }
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                e.printStackTrace()
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                Log.d(TAG, "encoder output format changed: $format")
                videoTrackIndex = muxer.addTrack(format)
                muxer.start()
            }

        })

        // configure the encoder
        Log.d(TAG, "codec info: ${MediaClassJsonUtils.toJson(encoder.codecInfo).toString()}")
        val colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        assert(encoder.codecInfo.getCapabilitiesForType(mimeType).colorFormats.contains(colorFormat))
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
        format.setInteger(MediaFormat.KEY_BIT_RATE, videoBitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)
        Log.d(TAG, "format: $format")
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        // create muxer
        val outputDir = externalCacheDir
        val outputName = "test.mp4"
        val outputFile = File(outputDir, outputName)
        Log.d(TAG, "output file: ${outputFile.absolutePath}")
        muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        // start encoder and wait it to finish
        encoder.start()
        while (!outputEnd.get())
        {
            Thread.sleep(10)
        }

        Log.d(TAG, "encoding finished")
        encoder.stop()
        muxer.stop()
        encoder.release()
    }

    private fun computePresentationTime(frameIndex: Int): Long {
        return (132 + frameIndex * 1000000 / FRAME_RATE).toLong()
    }

    private fun generateFrame(generateIndex: Int, colorFormat: Int, frameData: ByteArray) {
        var frameIndex = generateIndex
        val HALF_WIDTH: Int = videoWidth / 2
        val semiPlanar = true
        // Set to zero.  In YUV this is a dull green.
        frameData.fill(0.toByte())
        val startX: Int
        val startY: Int
        frameIndex %= 8
        //frameIndex = (frameIndex / 8) % 8;    // use this instead for debug -- easier to see
        if (frameIndex < 4) {
            startX = frameIndex * (videoWidth / 4)
            startY = 0
        } else {
            startX = (7 - frameIndex) * (videoWidth / 4)
            startY = videoHeight / 2
        }
        val TEST_Y = 120                  // YUV values for colored rect
        val TEST_U = 160
        val TEST_V = 200
        for (y in startY + videoHeight / 2 - 1 downTo startY) {
            for (x in startX + videoWidth / 4 - 1 downTo startX) {
                if (semiPlanar) {
                    // full-size Y, followed by UV pairs at half resolution
                    // e.g. Nexus 4 OMX.qcom.video.encoder.avc COLOR_FormatYUV420SemiPlanar
                    // e.g. Galaxy Nexus OMX.TI.DUCATI1.VIDEO.H264E
                    //        OMX_TI_COLOR_FormatYUV420PackedSemiPlanar
                    frameData[y * videoWidth + x] = TEST_Y.toByte()
                    if (x and 0x01 == 0 && y and 0x01 == 0) {
                        frameData[videoWidth * videoHeight + y * HALF_WIDTH + x] = TEST_U.toByte()
                        frameData[videoWidth * videoHeight + y * HALF_WIDTH + x + 1] = TEST_V.toByte()
                    }
                } else {
                    // full-size Y, followed by quarter-size U and quarter-size V
                    // e.g. Nexus 10 OMX.Exynos.AVC.Encoder COLOR_FormatYUV420Planar
                    // e.g. Nexus 7 OMX.Nvidia.h264.encoder COLOR_FormatYUV420Planar
                    frameData[y * videoWidth + x] = TEST_Y.toByte()
                    if (x and 0x01 == 0 && y and 0x01 == 0) {
                        frameData[videoWidth * videoHeight + y / 2 * HALF_WIDTH + x / 2] = TEST_U.toByte()
                        frameData[videoWidth * videoHeight + HALF_WIDTH * (videoHeight / 2) + y / 2 * HALF_WIDTH + x / 2] =
                            TEST_V.toByte()
                    }
                }
            }
        }
    }
}