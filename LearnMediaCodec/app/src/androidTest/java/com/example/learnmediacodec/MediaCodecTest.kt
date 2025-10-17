package com.example.learnmediacodec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MediaCodecTest {
    private val TAG = "MediaCodecTest"
    private val mRegularCodes = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    private val mime = MediaFormat.MIMETYPE_VIDEO_AVC
    private val width = 1280
    private val height = 720
    private val videoFormat = MediaFormat.createVideoFormat(mime, width, height)
    private var codec: MediaCodec? = null
    private val decoderCodecName = mRegularCodes.findDecoderForFormat(videoFormat)
    private val encoderCodecName = mRegularCodes.findEncoderForFormat(videoFormat)
    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
        codec?.stop()
        codec?.release()
    }

    @Test
    fun canCreateCodecByName() {
        codec = MediaCodec.createByCodecName(decoderCodecName)

        assertNotNull(codec)
        assertEquals(decoderCodecName, codec?.name)
    }

    @Test
    fun throwsWhenCreateCodecByNameIfNameIsInvalid() {
        val codecName = "invalid codec name"
        assertThrows(Exception::class.java) {
            MediaCodec.createByCodecName(codecName)
        }
    }

    @Test
    fun canGetCodecName(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        assertEquals(decoderCodecName, codec?.name)
    }

    @Test
    fun canGetCodecCanonicalName(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        assertEquals(decoderCodecName, codec?.canonicalName)
    }

    @Test
    fun canGetCodecInfo(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        val codecInfo = codec?.codecInfo
        assertNotNull(codecInfo)
    }

    @Test
    fun canGetCodecMetrics(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        val m = codec?.metrics
        Log.i(TAG, "codec metrics: $m")
    }

    @Test
    fun configureFailedIfMediaFormatNotSupported(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        val invalidFormat = MediaFormat.createVideoFormat(mime, -100, height)
        assertThrows(Exception::class.java) {
            codec?.configure(invalidFormat, null, null, 0)
        }
    }

    @Test
    fun canConfigureWithNullSurface(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        codec?.configure(videoFormat, null, null, 0)
    }

    @Test
    fun configureDecoderFailedIfFlagIsEncoder(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        val flag = MediaCodec.CONFIGURE_FLAG_ENCODE
        assertThrows(Exception::class.java) {
            codec?.configure(videoFormat, null, null, flag)
        }
    }

    @Test
    fun configureEncoderFailedIfFlagIsDecoder(){
        codec = MediaCodec.createByCodecName(encoderCodecName)

        val flag = 0
        assertThrows(Exception::class.java) {
            codec?.configure(videoFormat, null, null, flag)
        }
    }

    @Test
    fun startThrowsIfNotConfigured(){
        codec = MediaCodec.createByCodecName(decoderCodecName)

        assertThrows(Exception::class.java) {
            codec?.start()
        }
    }

    @Test
    fun startThrowsIfAlreadyStarted(){
        codec = MediaCodec.createByCodecName(decoderCodecName)
        codec?.configure(videoFormat, null, null, 0)
        codec?.start()

        assertThrows(Exception::class.java) {
            codec?.start()
        }
    }
}