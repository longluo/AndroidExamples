package com.example.learnmediacodec

import android.media.*
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.reflect.Field


@RunWith(AndroidJUnit4::class)
class CodecCapabilitiesTest {
    private val TAG = "CodecCapabilitiesTest"
    private val mRegularCodes = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    private val mCodecInfos = mRegularCodes.codecInfos

    private val mAudioMime = "audio/mp4a-latm"
    private val mAudioFormat = MediaFormat.createAudioFormat(mAudioMime, 44100, 2)
    private val mAudioDecoderCodecName = mRegularCodes.findDecoderForFormat(mAudioFormat)
    private val mAudioEncoderCodecName = mRegularCodes.findEncoderForFormat(mAudioFormat)
    private val mAudioDecoderInfo = mCodecInfos.find { it.name == mAudioDecoderCodecName }
    private val mAudioEncoderInfo = mCodecInfos.find { it.name == mAudioEncoderCodecName }

    private val mAVCMime = "video/avc"
    private val mAVCFormat = MediaFormat.createVideoFormat(mAVCMime, 1280, 720)
    private val mAVCDecoderCodecName = mRegularCodes.findDecoderForFormat(mAVCFormat)
    private val mAVCEncoderCodecName = mRegularCodes.findEncoderForFormat(mAVCFormat)

    private val mHEVCFormat = MediaFormat.createVideoFormat("video/hevc", 1280, 720)
    private val mHEVCDecoderCodecName = mRegularCodes.findDecoderForFormat(mHEVCFormat)
    private val mHEVCEncoderCodecName = mRegularCodes.findEncoderForFormat(mHEVCFormat)

    private val mAVCDecoderInfo = mCodecInfos.find { it.name == mAVCDecoderCodecName }
    private val mAVCEncoderInfo = mCodecInfos.find { it.name == mAVCEncoderCodecName }

    private val mHEVCDecoderInfo = mCodecInfos.find { it.name == mHEVCDecoderCodecName }
    private val mHEVCEncoderInfo = mCodecInfos.find { it.name == mHEVCEncoderCodecName }

    private val codecProfileLevelFields: Array<Field> =
        MediaCodecInfo.CodecProfileLevel::class.java.fields
    private val capabilitiesFields: Array<Field> =
        MediaCodecInfo.CodecCapabilities::class.java.fields


    @Test
    fun getCapabilitiesFromCodecInfo() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNotNull(decoderCal)
        assertNotNull(encoderCal)
    }

    @Test
    fun canGetMimeType() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertEquals(mAVCMime, decoderCal?.mimeType)
    }

    @Test
    fun canGetColorFormats() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        val decoderColorFormats = decoderCal?.colorFormats
        val encoderColorFormats = encoderCal?.colorFormats

        assertNotNull(decoderColorFormats)
        assertNotNull(encoderColorFormats)
    }

    @Test
    fun canCreateFromProfileAndLevel() {
        val profile = MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline
        val level = MediaCodecInfo.CodecProfileLevel.AVCLevel31
        val cap =
            MediaCodecInfo.CodecCapabilities.createFromProfileLevel("video/avc", profile, level)

        assertNotNull(cap)
    }

    @Test
    fun createFailedIfProfileAndLevelNotUnderstoodByFramework() {
        val profile = 100
        val level = MediaCodecInfo.CodecProfileLevel.H263Level70
        val cap =
            MediaCodecInfo.CodecCapabilities.createFromProfileLevel("video/avc", profile, level)

        assertNull(cap)
    }

    @Test
    fun canCheckIsFeatureSupportedOrNot() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertTrue(decoderCal?.isFeatureSupported(MediaCodecInfo.CodecCapabilities.FEATURE_AdaptivePlayback)!!)
        assertFalse(decoderCal.isFeatureSupported(MediaCodecInfo.CodecCapabilities.FEATURE_SecurePlayback))
    }

    @Test
    fun canGetFeaturesFromDefaultFormat() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val defaultFormat = decoderCal?.defaultFormat

        for (f in defaultFormat?.features!!) {
            assertTrue(decoderCal.isFeatureSupported(f))
        }
    }

    @Test
    fun canGetVideoCapabilities() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNotNull(decoderCal?.videoCapabilities)
    }

    @Test
    fun getVideoCapabilitiesFailedIfNotVideoCodec() {
        val decoderCal = mAudioDecoderInfo?.getCapabilitiesForType(mAudioMime)

        assertNull(decoderCal?.videoCapabilities)
    }

    @Test
    fun canGetAudioCapabilities() {
        val decoderCal = mAudioDecoderInfo?.getCapabilitiesForType(mAudioMime)

        assertNotNull(decoderCal?.audioCapabilities)
    }

    @Test
    fun getAudioCapabilitiesFailedIfNotAudioCodec() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNull(decoderCal?.audioCapabilities)
    }

    @Test
    fun canGetEncoderCapabilities() {
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNotNull(encoderCal?.encoderCapabilities)
    }

    @Test
    fun getEncoderCapabilitiesFailedIfNotEncoder() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNull(decoderCal?.encoderCapabilities)
    }

    @Test
    fun canCheckIsFormatSupportedOrNot() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        assertTrue(decoderCal?.isFormatSupported(mAVCFormat)!!)
        assertTrue(encoderCal?.isFormatSupported(mAVCFormat)!!)
    }

    @Test
    fun printAllCapabilities() {
        val str = MediaClassJsonUtils.toJsonArray(mCodecInfos).toString()
        // save str to cache file
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val cacheDir = context.cacheDir
        val cacheFile = File(cacheDir, "capabilities.json")
        Log.d(TAG, "printAllCapabilities: ${cacheFile.absolutePath}")
        cacheFile.writeText(str)
    }

    @Test
    fun createMediaCodecDecoderVideo() {
        val mime = "video/avc"
        val width = 3840
        val height = 2160
        val videoFormat = MediaFormat.createVideoFormat(mime, width, height)

        // find decoder name
        val decoderCodecName = mRegularCodes.findDecoderForFormat(videoFormat)
        if(decoderCodecName != null){
            Log.i(TAG, "createMediaCodecDecoderVideo: decoderCodecName=$decoderCodecName")
        }else{
            Log.i(TAG, "createMediaCodecDecoderVideo: decoderCodecName=null")
            return
        }

        // create decoder
        val decoder = MediaCodec.createByCodecName(decoderCodecName)
        decoder.configure(videoFormat, null, null, 0)

        // ... decoding

        // release
        decoder.stop()
        decoder.release()
    }

    @Test
    fun createMediaCodecEncoder(){
        val mime = "video/avc"
        val width = 3840
        val height = 2161
        val videoFormat = MediaFormat.createVideoFormat(mime, width, height)

        // find decoder name
        val encoderCodecName = mRegularCodes.findEncoderForFormat(videoFormat)
        if(encoderCodecName != null) {
            Log.i(TAG, "createMediaCodecEncoder: encoderCodecName=$encoderCodecName")
        }else{
            Log.i(TAG, "createMediaCodecEncoder: encoderCodecName=null")
            return
        }

        // create decoder
        val encoder = MediaCodec.createByCodecName(encoderCodecName)
        encoder.configure(videoFormat, null, null, 0)

        // encoding...

        // release
        encoder.stop()
        encoder.release()
    }
}