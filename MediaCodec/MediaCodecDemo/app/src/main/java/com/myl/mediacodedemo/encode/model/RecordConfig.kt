package com.myl.mediacodedemo.encode.record

import android.media.AudioFormat
import android.opengl.EGLContext

const val MIME_TYPE_AUDIO = "audio/mp4a-latm"

const val SAMPLE_RATE =
    44100 // 44.1[KHz] is only setting guaranteed to be available on all devices.

// 与抖音相同的音频比特率
const val BIT_RATE_AUDIO = 128000


/**
 * 音频参数
 */
data class AudioConfig(
    var sampleRate: Int = SAMPLE_RATE,
    var channel: Int = AudioFormat.CHANNEL_IN_STEREO,
    var bitRate: Int = BIT_RATE_AUDIO,
    var audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
    var audioPath: String = "",
    var maxDuration: Long = 0,
)


// 录制视频的类型
const val MIME_TYPE_VIDEO_AVC: String = "video/avc" //H264
const val MIME_TYPE_VIDEO_HEVC: String = "video/hevc"//H265

// 帧率
const val FRAME_RATE = 25

// I帧时长
const val I_FRAME_INTERVAL = 1

/**
 * 16*1000 bps：可视电话质量
 * 128-384 * 1000 bps：视频会议系统质量
 * 1.25 * 1000000 bps：VCD质量（使用MPEG1压缩）
 * 5 * 1000000 bps：DVD质量（使用MPEG2压缩）
 * 8-15 * 1000000 bps：高清晰度电视（HDTV） 质量（使用H.264压缩）
 * 29.4  * 1000000 bps：HD DVD质量
 * 40 * 1000000 bps：蓝光光碟质量（使用MPEG2、H.264或VC-1压缩）
 */
// 与抖音相同的视频比特率
const val BIT_RATE_VIDEO = 6693560 // 1280 * 720

const val BIT_RATE_LOW_VIDEO = 3921332 // 576 * 1024

const val DEFAULT_HEIGHT = 720
const val DEFAULT_WIDTH = 1280

/**
 * 视频参数
 */
data class VideoConfig(
    var videoWidth: Int = DEFAULT_WIDTH,
    var videoHeight: Int = DEFAULT_HEIGHT,
    var bitRate: Int = BIT_RATE_VIDEO,
    var maxDuration: Long = 0,
    var eglContext: EGLContext? = null,
    var videoPath: String = "",
    var mineType: String = MIME_TYPE_VIDEO_AVC
)

/**
 * 录制一段视频/音频的信息
 */
data class RecordInfo(
    val fileName: String = "",
    val duration: Long = 0,
    val mediaType: MediaType = MediaType.VIDEO
)

enum class MediaType {
    AUDIO, VIDEO
}