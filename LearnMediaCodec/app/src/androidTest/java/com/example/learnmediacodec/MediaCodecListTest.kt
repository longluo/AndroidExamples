package com.example.learnmediacodec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaCodecListTest {
    private val TAG = "MediaCodecListTest"
    private val mRegularCodes = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    private val mAllCodes = MediaCodecList(MediaCodecList.ALL_CODECS)

    @Test
    fun canCreateCodecFromAllCodecInfo(){
        for(info in mAllCodes.codecInfos) {
            val codec = MediaCodec.createByCodecName(info.name)

            assertEquals(info.name, codec.name)
            assertEquals(info.canonicalName, codec.canonicalName)

            codec.release()
        }
    }

    @Test
    fun canCreateCodecFromRegularCodecInfo(){
        for(info in mRegularCodes.codecInfos) {
            val codec = MediaCodec.createByCodecName(info.name)

            assertEquals(info.name, codec.name)
            assertEquals(info.canonicalName, codec.canonicalName)

            codec.release()
        }
    }

    @Test
    fun canFindEncoderWithFormat()
    {
        val videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720)
        val foundVideoCodecName = mRegularCodes.findEncoderForFormat(videoFormat)
        assertNotNull(foundVideoCodecName)
        Log.d(TAG, "foundVideoCodecName: $foundVideoCodecName")

        val audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 2)
        val foundAudioCodecName = mRegularCodes.findEncoderForFormat(audioFormat)
        assertNotNull(foundAudioCodecName)
        Log.d(TAG, "foundAudioCodecName: $foundAudioCodecName")
    }

    @Test
    fun canFindDecoderWithFormat()
    {
        val videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720)
        val foundVideoCodecName = mRegularCodes.findDecoderForFormat(videoFormat)
        assertNotNull(foundVideoCodecName)

        val audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 2)
        val foundAudioCodecName = mRegularCodes.findDecoderForFormat(audioFormat)
        assertNotNull(foundAudioCodecName)
    }


    private fun getLegacyInfos(): Array<MediaCodecInfo?> {
        val codecCount = MediaCodecList.getCodecCount()
        val infos = arrayOfNulls<MediaCodecInfo>(codecCount)

        for(i in 0 until codecCount){
            infos[i] = MediaCodecList.getCodecInfoAt(i)
        }

        return infos
    }

    @Test
    fun canCreateCodecFromLegacyInfos(){
        val legacyInfo = getLegacyInfos()
        for(info in legacyInfo) {
            if(info == null) continue

            val codec = MediaCodec.createByCodecName(info.name)

            assertEquals(info.name, codec.name)
            assertEquals(info.canonicalName, codec.canonicalName)

            codec.release()
        }
    }

    @Test
    fun LegacyInfosAndRegularCodecInfosAreSame() {
        val legacyInfo = getLegacyInfos()
        val regularInfo = mRegularCodes.codecInfos

        assertEquals(legacyInfo.size, regularInfo.size)

        for(i in 0 until legacyInfo.size) {
            assertEquals(legacyInfo[i]?.name, regularInfo[i].name)
            assertEquals(legacyInfo[i]?.canonicalName, regularInfo[i].canonicalName)
        }
    }

    @Test
    fun canGetCocdecCapabilitiesFromCodecInfo(){
        for(info in mAllCodes.codecInfos) {
            val types = info.supportedTypes

            // assert no throw exception
            for(type in types) {
                val caps = info.getCapabilitiesForType(type)
                assertNotNull(caps)
            }
        }
    }

    private fun buildInfoString(info: MediaCodecInfo): String {
        return StringBuilder().apply {
            append("codec: ")
            append(info.name)
            append(", canonicalName: ")
            append(info.canonicalName)
            append(", types: ")
            append(info.supportedTypes.contentToString())
            append(", isEncoder = ")
            append(info.isEncoder)
            append(", isVendor = ")
            append(info.isVendor)
            append(", isAlias = ")
            append(info.isAlias)
            append(", isSoftwareOnly = ")
            append(info.isSoftwareOnly)
            append(", isHardwareAccelerated = ")
            append(info.isHardwareAccelerated)
        }.toString()
    }
    private fun printInfo(info: MediaCodecInfo){
        val infoString = buildInfoString(info)
        Log.d(TAG, "$infoString")
    }

    @Test
    fun printRegularCodecNames() {
        Log.d(TAG, "all codecs")
        for(info in mAllCodes.codecInfos) {
            printInfo(info)
        }

//        Log.d(TAG, "regular codecs")
//        for(info in mRegularCodes.codecInfos) {
//            printInfo(info)
//        }
    }
}