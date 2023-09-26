package pfg.com.screenproc;

import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import pfg.com.screenproc.util.MyLog;

/**
 * Created by FPENG3 on 2018/7/26.
 */

public class VideoDecoderCore {

    private String TAG = "VideoDecoderCore";

    MediaExtractor extractor;
    MediaCodec mediaCodec;

    String filePath;
    Surface surface;

    private static final int MSG_START = 0;
    private static final int MSG_STOP = 1;
    private static final int TIME_OUT = 1000;

    private HandlerThread handlerThread;
    private Handler mHandler;
    private boolean isStoped = false;
    private int mWidth, mHeight;

    private Object lock = new Object();
    public EGLContext eglContext;

    public VideoDecoderCore(String filePath, Surface surface) {
        this.filePath = filePath;
        this.surface = surface;
    }

    private void init() {
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(filePath);
            int numTracks = extractor.getTrackCount();
            String mime = null;
            for (int i = 0; i < numTracks; ++i) {
                MediaFormat format = extractor.getTrackFormat(i);
                mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    mWidth = format.getInteger(MediaFormat.KEY_WIDTH);
                    mHeight = format.getInteger(MediaFormat.KEY_HEIGHT);
                    extractor.selectTrack(i);
                    mediaCodec = MediaCodec.createDecoderByType(mime);
                    mediaCodec.configure(format, surface, null, 0);
                    mediaCodec.start();
                    break;
                }
            }

        } catch (IOException e) {
            MyLog.logd(TAG, "IOException msg:"+e.getMessage());
            e.printStackTrace();
        }
        eglContext = EGL14.eglGetCurrentContext();
    }

    public void start() {
        isStoped = false;
        MyLog.logd(TAG, "Send:MSG_STARRT");

        handlerThread = new HandlerThread("VideoDecoder");
        handlerThread.start();

        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_START:
                        handleStart();
                        break;
                    case MSG_STOP:
                        MyLog.logd(TAG, "Receive:MSG_STOP");
                        handleStop();
                        break;
                    default:
                        break;
                }
            }
        };

        Message msg = mHandler.obtainMessage(MSG_START);
        mHandler.sendMessage(msg);

    }

    public void stop() {
        MyLog.logd(TAG, "Send:MSG_STOP");
        isStoped = true;
        Message msg = mHandler.obtainMessage(MSG_STOP);
        mHandler.sendMessage(msg);
    }

    public void waitForInit() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleStart() {
        MyLog.logd(TAG, "handleStart()");
        init();
        synchronized (lock) {
            lock.notifyAll();
        }
        while(!isStoped) {
            int inputBufferId = mediaCodec.dequeueInputBuffer(TIME_OUT);
            if (inputBufferId >= 0) {
                // fill inputBuffers[inputBufferId] with valid data
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferId);
                int sampleSize = extractor.readSampleData(inputBuffer, 0);
                if(sampleSize > 0) {
                    mediaCodec.queueInputBuffer(inputBufferId, 0, sampleSize, extractor.getSampleTime(), 0);
                    extractor.advance();
                }
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_OUT);

            if((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                break;
            }

            if (outputBufferId >= 0) {
                mediaCodec.releaseOutputBuffer(outputBufferId, true);
            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // Subsequent data will conform to new format.
                MediaFormat format = mediaCodec.getOutputFormat();
            }
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {

            }

        }

        extractor.release();
        mediaCodec.stop();
        mediaCodec.release();
        MyLog.logd(TAG, "MediaCodec release");
    }

    private void handleStop() {
        MyLog.logd(TAG, "handleStop()");
        handlerThread.quit();
    }

    public int getVideoWidth() {
        return mWidth;
    }

    public int getVideoHeight() {
        return mHeight;
    }



}
