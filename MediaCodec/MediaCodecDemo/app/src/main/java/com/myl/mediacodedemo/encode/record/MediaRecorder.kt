package com.myl.mediacodedemo.encode.record

import android.util.Log
import com.myl.mediacodedemo.encode.OnRecordStateListener

class MediaRecorder(private val mRecordStateListener: OnRecordStateListener? = null) : OnRecordListener {

    companion object {
        private const val TAG = "MediaRecorder"
    }

    // 音频录制器
    private val mAudioRecorder: AudioRecorder = AudioRecorder()

    // 视频录制器
    private val mVideoRecorder: VideoRecorder = VideoRecorder()

    // 打开的录制器个数
    private var mRecorderCount = 0

    // 处理时长
    private var mProcessTime = 0L

    override fun onRecording(type: MediaType?, duration: Long) {
        if (type === MediaType.VIDEO) {
            mRecordStateListener?.onRecording(duration)
        }
    }

    override fun onRecordFinish(info: RecordInfo?) {
        info?.let {
            mRecordStateListener?.onRecordFinish(it)
        }
    }

    init {
        mVideoRecorder.mRecordListener = this
        mAudioRecorder.mRecordListener = this
    }


    /**
     * 开始录制
     *
     */
    fun startRecord(videoParams: VideoConfig, audioParams: AudioConfig) {
        Log.d(TAG, " start record")
        mAudioRecorder.prepare(audioParams)
        mVideoRecorder.startRecord(videoParams)
        mAudioRecorder.startRecord()
    }

    /**
     * 录制开始
     * @param type
     */
    override fun onRecordStart(type: MediaType?) {
        mRecorderCount++
        // 允许音频录制，则判断录制器打开的个数大于等于两个，则表示全部都打开了
        if (mRecorderCount >= 2) {
            mRecordStateListener?.onRecordStart()
            mRecorderCount = 0
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        mVideoRecorder.release()
        mAudioRecorder.release()
    }

    fun isRecording(): Boolean {
        if (mVideoRecorder != null) {
            return mVideoRecorder.isRecording();
        }

        return false
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        Log.d(TAG, "stop recording")
        val time = System.currentTimeMillis()
        mVideoRecorder.stopRecord()
        mAudioRecorder.stopRecord()
        mProcessTime += System.currentTimeMillis() - time
        Log.d(TAG, "sum of init and release time: " + mProcessTime + "ms")
        mProcessTime = 0
    }

    /**
     * 录制帧可用
     */
    fun frameAvailable(texture: Int, timestamp: Long) {
        mVideoRecorder.frameAvailable(texture, timestamp)
    }
}