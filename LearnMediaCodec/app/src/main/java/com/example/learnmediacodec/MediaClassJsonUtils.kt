package com.example.learnmediacodec

import android.media.MediaCodecInfo
import android.media.MediaCodecInfo.AudioCapabilities
import android.media.MediaCodecInfo.CodecProfileLevel
import android.media.MediaCodecInfo.EncoderCapabilities
import android.media.MediaCodecInfo.VideoCapabilities
import android.media.MediaCodecInfo.VideoCapabilities.PerformancePoint
import org.json.JSONArray
import org.json.JSONObject


class MediaClassJsonUtils {

    companion object {
        fun toJson(codecInfo: MediaCodecInfo): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("name", codecInfo.name)
            jsonObject.put("canonicalName", codecInfo.canonicalName)
            jsonObject.put("isEncoder", codecInfo.isEncoder)
            jsonObject.put("isHardwareAccelerated", codecInfo.isHardwareAccelerated)
            jsonObject.put("isSoftwareOnly", codecInfo.isSoftwareOnly)
            jsonObject.put("isVendor", codecInfo.isVendor)
            jsonObject.put("isAlias", codecInfo.isAlias)

            val capabilitiesJsonArray = JSONArray()
            codecInfo.supportedTypes.forEach {
                val cal = codecInfo.getCapabilitiesForType(it)
                capabilitiesJsonArray.put(toJson(cal))
            }

            jsonObject.put("capabilities", capabilitiesJsonArray)

            return jsonObject
        }

        fun toJsonArray(infos: Array<MediaCodecInfo>): JSONArray {
            val jsonArray = JSONArray()
            infos.forEach {
                jsonArray.put(toJson(it))
            }
            return jsonArray
        }

        fun toJson(cal: MediaCodecInfo.CodecCapabilities): JSONObject {
            val jsonObject = JSONObject()

            jsonObject.put("mimeType", cal.mimeType)
            jsonObject.put("maxSupportedInstances", cal.maxSupportedInstances)
            jsonObject.put("defaultFormat", cal.defaultFormat.toString())
            jsonObject.put("colorFormats", toJsonArray(cal.colorFormats))
            jsonObject.put("profileLevels", toJsonArray(cal.profileLevels))
            jsonObject.put("videoCapabilities", toJson(cal.videoCapabilities))
            jsonObject.put("audioCapabilities", toJson(cal.audioCapabilities))
            jsonObject.put("encoderCapabilities", toJson(cal.encoderCapabilities))

            return jsonObject
        }

        fun toJson(ac: AudioCapabilities?): JSONObject {
            if (ac == null) {
                return JSONObject()
            }
            return JSONObject().apply {
                put("supportedSampleRates", ac.supportedSampleRates.contentToString())
                put("supportedSampleRateRanges", ac.supportedSampleRateRanges.contentToString())
                put("maxInputChannelCount", ac.maxInputChannelCount)
                put("bitrateRange", ac.bitrateRange.toString())
            }
        }

        fun toJson(ec: EncoderCapabilities?): JSONObject {
            if (ec == null) {
                return JSONObject()
            }
            return JSONObject().apply {
                put("complexityRange", ec.complexityRange.toString())
                put("qualityRange", ec.qualityRange.toString())
            }
        }

        fun toJson(vc: VideoCapabilities?): JSONObject {
            if (vc == null) {
                return JSONObject()
            }

            return JSONObject().apply {
                put("supportedWidths", vc.supportedWidths.toString())
                put("supportedHeights", vc.supportedHeights.toString())
                put("supportedFrameRates", vc.supportedFrameRates.toString())
                put("bitrateRange", vc.bitrateRange.toString())
                put("widthAlignment", vc.widthAlignment)
                put("heightAlignment", vc.heightAlignment)
                put("performancePoints", toJsonArray(vc.supportedPerformancePoints))
            }
        }

        fun toJson(p: PerformancePoint): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("PerformancePoint", p.toString())
            return jsonObject
        }

        fun toJsonArray(p: MutableList<PerformancePoint>?): JSONArray {
            if (p == null) {
                return JSONArray()
            }
            val jsonObject = JSONArray()
            for (performancePoint in p) {
                jsonObject.put(toJson(performancePoint))
            }
            return jsonObject
        }

        fun toJsonArray(profileLevels: Array<CodecProfileLevel>): JSONArray {
            val jsonObject = JSONArray()
            for (profileLevel in profileLevels) {
                jsonObject.put(toJson(profileLevel))
            }
            return jsonObject
        }

        fun toJson(profileLevel: CodecProfileLevel): JSONObject {
            val jsonObject = JSONObject()

            jsonObject.put("profile", profileLevel.profile)
            jsonObject.put("level", profileLevel.level)

            return jsonObject
        }

        fun toJsonArray(intArray: IntArray): JSONArray {
            val jsonObject = JSONArray()
            for (i in intArray) {
                jsonObject.put(i)
            }
            return jsonObject
        }
    }
}