package com.myl.mediacodedemo.encode.record;

/**
 * 录制监听器, MediaRecorder内部使用
 */
public interface OnRecordListener {

    // 录制开始
    void onRecordStart(MediaType type);

    // 录制进度
    void onRecording(MediaType type, long duration);

    // 录制完成
    void onRecordFinish(RecordInfo info);
}
