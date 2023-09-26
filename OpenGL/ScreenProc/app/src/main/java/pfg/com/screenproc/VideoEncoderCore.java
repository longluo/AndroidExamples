package pfg.com.screenproc;

import android.media.MediaCodec;
import android.media.MediaMuxer;
import android.view.Surface;
import android.media.MediaFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaCodecInfo;

import pfg.com.screenproc.util.MyLog;

/**
 * Created by FPENG3 on 2018/7/16.
 */

public class VideoEncoderCore {

    private String TAG = "VideoEncoderCore";

    private MediaMuxer mediaMuxer;
    private MediaCodec mediaCodec;

    private Surface inputSurface;

    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int BIT_RATE = 2072576;            //
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames

    boolean mediaMuxerStarted = false;

    private MediaCodec.BufferInfo mBufferInfo;
    private int mTrackIndex = -1;

    VideoEncoderCore(int width, int height, String filePath) {
        mBufferInfo = new MediaCodec.BufferInfo();
        try {
            mediaMuxer = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = mediaCodec.createInputSurface();
            mediaCodec.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Surface getInputSurface() {
        return inputSurface;
    }

    public void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        MyLog.logd(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            MyLog.logd(TAG, "sending EOS to encoder");
            mediaCodec.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mediaCodec.getOutputBuffers();
        while (true) {
            int encoderStatus = mediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    MyLog.logd(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mediaMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mediaCodec.getOutputFormat();
                MyLog.logd(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mediaMuxer.addTrack(newFormat);
                mediaMuxer.start();
                mediaMuxerStarted = true;
            } else if (encoderStatus < 0) {
                MyLog.logd(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    MyLog.logd(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mediaMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    mediaMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    {
                        MyLog.logd(TAG, "drainEncoder sent " + mBufferInfo.size + " bytes to muxer, ts=" +
                                mBufferInfo.presentationTimeUs);
                    }
                }

                mediaCodec.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        MyLog.logd(TAG, "reached end of stream unexpectedly");
                    } else {
                        MyLog.logd(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }

    public void release() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        }
        if (mediaMuxer != null) {
            // TODO: stop() throws an exception if you haven't fed it any data.  Keep track
            //       of frames submitted, and don't call stop() if we haven't written anything.
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;
        }
    }


}
