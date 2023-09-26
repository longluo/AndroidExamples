package com.myl.mediacodedemo.encode.camera


/**
 * 相机配置参数
 */
object CameraParam {

    // 最大权重
    const val MAX_FOCUS_WEIGHT = 1000

    // 录制时长(毫秒)
    const val DEFAULT_RECORD_TIME = 15000

    // 16:9的默认宽高(理想值)
    const val DEFAULT_16_9_WIDTH = 1280
    const val DEFAULT_16_9_HEIGHT = 720

    // 4:3的默认宽高(理想值)
    const val DEFAULT_4_3_WIDTH = 1024
    const val DEFAULT_4_3_HEIGHT = 768

    // 期望fps
    const val DESIRED_PREVIEW_FPS = 30

    // 这里反过来是因为相机的分辨率跟屏幕的分辨率宽高刚好反过来
    const val Ratio_4_3 = 0.75f
    const val Ratio_16_9 = 0.5625f

    // 对焦权重最大值
    const val Weight = 100

    // 摄像头id
    var cameraId = 0

    // 是否支持闪光灯
    var supportFlash = false

    // 实际帧率
    var previewFps = 0

    // 当前长宽比
    var currentRatio = 0f
}