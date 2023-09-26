package com.myl.mediacodedemo.encode

import com.myl.mediacodedemo.encode.record.RecordInfo

/**
 * 录制状态监听
 */
interface OnRecordStateListener {
    // 录制开始
    fun onRecordStart()

    // 录制进度
    fun onRecording(duration: Long)

    // 录制结束
    fun onRecordFinish(info: RecordInfo)
}