package com.example.learnmediacodec

import android.graphics.BitmapFactory
import android.media.*
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean


class EncodeUsingEGLAndSurfaceActivity : AppCompatActivity() {
    private val TAG = "EncodeUsingEGLAndSurfaceActivity"
    private val videoWidth = 1280
    private val videoHeight = 720
    private val videoBitrate = 2000000
    private var encodedFrameIndex = 0
    private var inputFrameIndex = 0
    private val FRAME_RATE = 30
    private val IFRAME_INTERVAL = 5
    private val NUM_FRAMES = FRAME_RATE * 2
    private lateinit var muxer : MediaMuxer
    private var isMuxerStarted = false
    private var videoTrackIndex = 0
    private val outputEnd = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode_using_egland_surface)

        val btnStartEncoding = findViewById<android.widget.Button>(R.id.btn_start_encoding_async)
        btnStartEncoding.setOnClickListener{
            Thread{
                encodeUsingEGLAndSurface()
            }.start()
        }
    }

    private fun encodeUsingEGLAndSurface() {
        val mimeType = MediaFormat.MIMETYPE_VIDEO_AVC
        val format = MediaFormat.createVideoFormat(mimeType, videoWidth, videoHeight)
        val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val encodeCodecName = codecList.findEncoderForFormat(format)
        val encoder = MediaCodec.createByCodecName(encodeCodecName)
        encoder.setCallback(object: MediaCodec.Callback(){

            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                // do nothing
            }

            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                Log.d(TAG, "encoder output buffer available: $index")
                // output eos
                val isDone = (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                if(isDone || encodedFrameIndex == NUM_FRAMES)
                {
                    Log.d(TAG, "encoder output eos")
                    outputEnd.set(true)
                    info.size = 0
                    return
                }

                // got encoded frame, write it to muxer
                if(info.size > 0){
                    if (!isMuxerStarted) {
                        throw RuntimeException("muxer hasn't started");
                    }
                    val encodedData = codec.getOutputBuffer(index)
                    encodedData?.position(info.offset)
                    encodedData?.limit(info.offset + info.size)

                    muxer.writeSampleData(videoTrackIndex, encodedData!!, info)
                    Log.d(TAG, "encoder output buffer: $index, size: ${info.size}, pts: ${info.presentationTimeUs} encodedFrameIndex: ${encodedFrameIndex}")
                    codec.releaseOutputBuffer(index, false)

                    ++encodedFrameIndex
                }
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                e.printStackTrace()
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                Log.d(TAG, "encoder output format changed: $format")
                videoTrackIndex = muxer.addTrack(format)
                muxer.start()
                isMuxerStarted = true
            }

        })

        // configure the encoder
        Log.d(TAG, "codec info: ${MediaClassJsonUtils.toJson(encoder.codecInfo).toString()}")
        val colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        assert(encoder.codecInfo.getCapabilitiesForType(mimeType).colorFormats.contains(colorFormat))
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
        format.setInteger(MediaFormat.KEY_BIT_RATE, videoBitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)
        Log.d(TAG, "format: $format")
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        // create input surface
        val inputSurface = InputSurface(encoder.createInputSurface())
        inputSurface.makeCurrent()

        // create texture render
        val renderer = TextureRenderer()

        // create muxer
        val outputDir = externalCacheDir
        val outputName = "test_0.mp4"
        val outputFile = File(outputDir, outputName)
        Log.d(TAG, "output file: ${outputFile.absolutePath}")
        muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        // start encoder and wait it to finish
        encoder.start()

        // draw image to surface
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(resources, R.raw.test_img_1280x853, options)
        while (!outputEnd.get())
        {
            renderer.draw(videoWidth, videoHeight, bitmap, getMvp())
            val nanoPts = computePresentationTime(inputFrameIndex) * 1000 // us
            inputSurface.setPresentationTime(nanoPts)
            inputSurface.swapBuffers()
            Thread.sleep(10)
            inputFrameIndex++
        }

        Log.d(TAG, "encoding finished")
        encoder.stop()
        muxer.stop()
        encoder.release()
    }

    private fun computePresentationTime(frameIndex: Int): Long {
        return (frameIndex * 1000000 / FRAME_RATE).toLong()
    }

    private fun getMvp(): FloatArray {
        val mvp = FloatArray(16)
        Matrix.setIdentityM(mvp, 0)
        Matrix.scaleM(mvp, 0, 1f, -1f, 1f)

        return mvp
    }
}