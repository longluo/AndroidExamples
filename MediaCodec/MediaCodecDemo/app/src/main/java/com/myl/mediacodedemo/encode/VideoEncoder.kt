package com.myl.mediacodedemo.encode

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import com.myl.mediacodedemo.encode.Constants.DECODE_TIME_OUT
import com.myl.mediacodedemo.encode.Constants.RESOLUTION_1080P
import com.myl.mediacodedemo.encode.record.FRAME_RATE
import com.myl.mediacodedemo.encode.record.I_FRAME_INTERVAL
import com.myl.mediacodedemo.encode.record.MIME_TYPE_VIDEO_AVC
import com.myl.mediacodedemo.encode.record.MIME_TYPE_VIDEO_HEVC
import com.myl.mediacodedemo.encode.record.VideoConfig

class VideoEncoder(val mVideoParams: VideoConfig, val onEncodingListener: OnEncodingListener) {

    companion object {
        private const val TAG = "VideoEncoder"
    }

    private var mBufferInfo: MediaCodec.BufferInfo? = null
    private var mMediaCodec: MediaCodec? = null
    var mInputSurface: Surface? = null
    private var mMediaMuxer: MediaMuxer? = null
    private var mTrackIndex = 0
    private var mMuxerStarted = false

    // 录制起始时间戳
    private var mStartTimeStamp: Long = 0

    // 记录上一个时间戳
    private var mLastTimeStamp: Long = 0

    // 录制时长
    var mDuration: Long = 0


    init {
        mBufferInfo = MediaCodec.BufferInfo()

        // 设置编码格式
        val videoWidth: Int =
            if (mVideoParams.videoWidth % 2 === 0) mVideoParams.videoWidth else mVideoParams.videoWidth - 1
        val videoHeight: Int =
            if (mVideoParams.videoHeight % 2 === 0) mVideoParams.videoHeight else mVideoParams.videoHeight - 1
        val format = MediaFormat.createVideoFormat(mVideoParams.mineType, videoWidth, videoHeight)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, mVideoParams.bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
        if (Build.VERSION.SDK_INT >= 21) {
            var profile = 0
            var level = 0
            if (mVideoParams.mineType == MIME_TYPE_VIDEO_AVC) {
                profile = MediaCodecInfo.CodecProfileLevel.AVCProfileHigh
                level = if (videoWidth * videoHeight >= RESOLUTION_1080P) {
                    MediaCodecInfo.CodecProfileLevel.AVCLevel4
                } else {
                    MediaCodecInfo.CodecProfileLevel.AVCLevel31
                }
            } else if (mVideoParams.mineType == MIME_TYPE_VIDEO_HEVC) {
                profile = MediaCodecInfo.CodecProfileLevel.HEVCProfileMain
                level = if (videoWidth * videoHeight >= RESOLUTION_1080P) {
                    MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel4
                } else {
                    MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel31
                }
            }
            format.setInteger(MediaFormat.KEY_PROFILE, profile)
            // API 23以后可以设置AVC的编码level，低于23设置了但不生效
//            if (Build.VERSION.SDK_INT >= 23) {
            format.setInteger(MediaFormat.KEY_LEVEL, level)
//            }
        }
        Log.d(TAG, "format: $format")
        // 创建编码器
        mMediaCodec = MediaCodec.createEncoderByType(mVideoParams.mineType)
        mMediaCodec?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mInputSurface = mMediaCodec?.createInputSurface()
        mMediaCodec?.start()

        // 创建封装器
        mMediaMuxer =
            MediaMuxer(mVideoParams.videoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        mTrackIndex = -1
    }

    /**
     * 编码一帧数据到复用器中
     * @param endOfStream
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun drainEncoder(endOfStream: Boolean) {
        Log.d(TAG, "drainEncoder($endOfStream)")
        if (endOfStream) {
            Log.d(TAG, "sending EOS to encoder")
            mMediaCodec?.signalEndOfInputStream()
        }
        while (true) {
            val encoderStatus =
                mMediaCodec!!.dequeueOutputBuffer(mBufferInfo!!, DECODE_TIME_OUT)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break // out of while
                } else {
                    Log.d(TAG, "no output available, spinning to await EOS")
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val newFormat = mMediaCodec!!.outputFormat
                Log.d(
                    TAG, "encoder output format changed: " +
                            newFormat.getString(MediaFormat.KEY_MIME)
                )
                //取视频轨道并打开复用器
                mTrackIndex = mMediaMuxer!!.addTrack(newFormat)
                mMediaMuxer!!.start()
                mMuxerStarted = true
            } else if (encoderStatus < 0) {
                Log.w(
                    TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                            encoderStatus
                )
            } else {
                val encodedData = mMediaCodec!!.getOutputBuffer(encoderStatus)
                    ?: throw RuntimeException(
                        "encoderOutputBuffer " + encoderStatus +
                                " was null"
                    )
                if (mBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG")
                    mBufferInfo!!.size = 0
                }
                if (mBufferInfo!!.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }

                    // 计算录制时钟
                    if (mLastTimeStamp > 0 && mBufferInfo!!.presentationTimeUs < mLastTimeStamp) {
                        mBufferInfo!!.presentationTimeUs = mLastTimeStamp + DECODE_TIME_OUT
                    }
                    calculatePTSUs(mBufferInfo!!)
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo!!.offset)
                    encodedData.limit(mBufferInfo!!.offset + mBufferInfo!!.size)
                    // 将编码数据写入复用器中
                    mMediaMuxer!!.writeSampleData(mTrackIndex, encodedData, mBufferInfo!!)
                    Log.d(
                        TAG, "sent " + mBufferInfo!!.size + " bytes to muxer, ts=" +
                                mBufferInfo!!.presentationTimeUs
                    )

                    // 录制时长回调
                    onEncodingListener.onEncoding(mDuration)
                }
                mMediaCodec!!.releaseOutputBuffer(encoderStatus, false)
                if (mBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly")
                    } else {
                        Log.d(
                            TAG,
                            "end of stream reached"
                        )
                    }
                    break // out of while
                }
            }
        }
    }

    /**
     * 计算pts
     * @param info
     */
    private fun calculatePTSUs(info: MediaCodec.BufferInfo) {
        mLastTimeStamp = info.presentationTimeUs
        if (mStartTimeStamp == 0L) {
            mStartTimeStamp = info.presentationTimeUs
        } else {
            mDuration = info.presentationTimeUs - mStartTimeStamp
        }
    }

    /**
     * 释放编码器资源
     */
    fun release() {
        Log.d(TAG, "releasing encoder objects")
        mMediaCodec?.apply {
            stop()
            release()
        }
        mMediaCodec = null
        mMediaMuxer?.apply {
            if (mMuxerStarted) {
                stop()
            }
            release()
        }
        mMediaMuxer = null
    }


    /**
     * 编码监听器
     */
    interface OnEncodingListener {
        fun onEncoding(duration: Long)
    }
}