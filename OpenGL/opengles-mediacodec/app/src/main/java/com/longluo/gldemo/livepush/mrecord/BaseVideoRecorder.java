package com.longluo.gldemo.livepush.mrecord;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.longluo.gldemo.media.DarrenPlayer;
import com.longluo.gldemo.media.listener.MediaErrorListener;
import com.longluo.gldemo.media.listener.MediaInfoListener;
import com.longluo.gldemo.media.listener.MediaPreparedListener;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.concurrent.CyclicBarrier;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public abstract class BaseVideoRecorder {
    public Context mContext;
    private EGLContext mEglContext;

    private WeakReference<BaseVideoRecorder> recorderReference = new WeakReference<>(this);

    private Surface mSurface;
    private GLSurfaceView.Renderer mRender;
    // 视频渲染
    VideoRenderThread videoRenderThread;
    // 视频编码
    VideoEncoderThread videoEncoderThread;
    // 音频编码
    AudioEncoderThread audioEncoderThread;
    //视频编码器
    MediaCodec mVideoCodec;
    //音频编码器
    MediaCodec mAudioCodec;

    MediaMuxer mMediaMuxer;

    CyclicBarrier startCb = new CyclicBarrier(2);
    CyclicBarrier stopCb = new CyclicBarrier(2);

    DarrenPlayer mediaPlayer;
    private String TAG = "BaseVideoRecorder";
    private int videoWidth;
    private int videoHeight;

    public BaseVideoRecorder(Context context, EGLContext eglContext) {
        this.mContext = context;
        this.mEglContext = eglContext;

        mediaPlayer = new DarrenPlayer();
    }

    public void setRender(GLSurfaceView.Renderer mRender) {
        this.mRender = mRender;
        videoRenderThread = new VideoRenderThread(recorderReference);
        videoRenderThread.setSize(videoWidth, videoHeight);
    }

    public void initMediaParams(String audioPath, String outPath, int videoWidth, int videoHeight) {

        try {
            mMediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            initVideoCodec(videoWidth, videoHeight);

            initAudioParams(audioPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAudioParams(String audioPath) throws Exception {
        // 设置mediaPlayer音频路径，并做初始化
        mediaPlayer.setDataSource(audioPath);
        mediaPlayer.setOnPreparedListener(new MediaPreparedListener() {
            @Override
            public void onPrepared() {
//                mediaPlayer.prepareAsync();
                start();

                mediaPlayer.play();
            }
        });
        mediaPlayer.setMediaInfoListener(new MediaInfoListener() {

            int sampleRate;
            int channel;

            long audioPts;

            @Override
            public void musicInfo(int sampleRate, int channel) {
                initAudioCodec(sampleRate, channel);
                this.sampleRate = sampleRate;
                this.channel = channel;
            }

            @Override
            public void callBackPcm(byte[] pcmData, int size) {

                int inputBufferTrack = mAudioCodec.dequeueInputBuffer(0);
                if (inputBufferTrack >= 0) {
                    ByteBuffer inputBuffer = mAudioCodec.getInputBuffers()[inputBufferTrack];
//                    inputBuffer.position(0);
                    inputBuffer.clear();

                    inputBuffer.put(pcmData);

                    //0.41795918 *1000 000
                    audioPts += 1000000 * size * 1.0f / sampleRate * channel * 2;
                    Log.e(TAG, "callBackPcm: " + audioPts);
                    //数据放入mAudioCodec的队列中
                    mAudioCodec.queueInputBuffer(inputBufferTrack, 0, size, audioPts, 0);
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaErrorListener() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "mediaPlayer jni onError: code=" + code + ", msg=" + msg);
            }
        });

        //初始化本地，准备播放
//        mediaPlayer.prepareAsync();
    }

    private void initVideoCodec(int width, int height) {
        try {
            // https://developer.android.google.cn/reference/android/media/MediaCodec mediacodec官方介绍
            // 比方MediaCodec的几种状态
            // avc即h264编码
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            // 设置颜色格式
            // 本地原始视频格式（native raw video format）：这种格式通过COLOR_FormatSurface标记，并可以与输入或输出Surface一起使用
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            // 设置码率，通常码率越高，视频越清晰，但是对应的视频也越大
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);

            // 设置帧率 三星s21手机camera预览时，支持的帧率为10-30
            // 通常这个值越高，视频会显得越流畅，一般默认设置成30，你最低可以设置成24，不要低于这个值，低于24会明显卡顿
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            // 设置 I 帧间隔的时间
            // 通常的方案是设置为 1s，对于图片电影等等特殊情况，这里可以设置为 0，表示希望每一帧都是 KeyFrame
            // IFRAME_INTERVAL是指的帧间隔，这是个很有意思的值，它指的是，关键帧的间隔时间。通常情况下，你设置成多少问题都不大。
            // 比如你设置成10，那就是10秒一个关键帧。但是，如果你有需求要做视频的预览，那你最好设置成1
            // 因为如果你设置成10，那你会发现，10秒内的预览都是一个截图
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            // 创建编码器
            // https://www.codercto.com/a/41316.html MediaCodec 退坑指南
            mVideoCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mVideoCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            // 相机的像素数据绘制到该 surface 上面
            mSurface = mVideoCodec.createInputSurface();

            videoEncoderThread = new VideoEncoderThread(recorderReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAudioCodec(int sampleRate, int channel) {
        try {
            // 采样率，44.1khz，双声道，每个声道16位，2字节
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channel);
            // 设置比特率96k hz
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            // 设置输入数据缓冲区的最大大小
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, sampleRate * channel * 2);

            mAudioCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            mAudioCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            audioEncoderThread = new AudioEncoderThread(recorderReference);

            mAudioCodec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startRecord() {
        // mediaPlayer的prepare有0.几秒的延迟，最好这里启动准备，准备好后启动音、视频录制线程
        // 如果这里用play（）,并启动start()方法的线程，会导致录制的视频黑屏
        mediaPlayer.prepareAsync();
    }

    private void start() {
        // 视频渲染开始
        videoRenderThread.start();
        // 视频编码开始
        videoEncoderThread.start();
        // 音频编码开始
        audioEncoderThread.start();
    }

    public void stopRecord() {
        mediaPlayer.stop();

        videoRenderThread.requestExit();
        videoEncoderThread.requestExit();
        audioEncoderThread.requestExit();
    }

    RecordInfoListener recordInfoListener;

    public void setRecordInfoListener(RecordInfoListener recordInfoListener) {
        this.recordInfoListener = recordInfoListener;
    }

    public interface RecordInfoListener {
        void onTime(long times);
    }

    private class AudioEncoderThread extends Thread {

        WeakReference<BaseVideoRecorder> videoRecorderWf;
        private boolean shouldExit = false;

        private MediaCodec mAudioCodec;
        private MediaMuxer mMediaMuxer;
        MediaCodec.BufferInfo bufferInfo;

        long audioPts = 0;
        final CyclicBarrier startCb;
        final CyclicBarrier stopCb;
        /**
         * 音频轨道
         */
        private int mAudioTrackIndex = -1;

        public AudioEncoderThread(WeakReference<BaseVideoRecorder> videoRecorderWf) {
            this.videoRecorderWf = videoRecorderWf;
            this.mAudioCodec = videoRecorderWf.get().mAudioCodec;
            this.mMediaMuxer = videoRecorderWf.get().mMediaMuxer;
            this.startCb = videoRecorderWf.get().startCb;
            this.stopCb = videoRecorderWf.get().stopCb;
            bufferInfo = new MediaCodec.BufferInfo();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (shouldExit) {
                        onDestroy();
                        return;
                    }

                    // 返回有效数据填充的输出缓冲区的索引
                    int outputBufferIndex = mAudioCodec.dequeueOutputBuffer(bufferInfo, 0);
                    if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // 将mMediaCodec的指定的格式的数据轨道，设置到mMediaMuxer上
                        mAudioTrackIndex = mMediaMuxer.addTrack(mAudioCodec.getOutputFormat());
                        Log.e(TAG, "run:  audio mMediaMuxer.await before");
                        startCb.await();
                        Log.e(TAG, "run: audio mMediaMuxer.await after");
                    } else {

                        int index = 0;
                        while (outputBufferIndex >= 0) {

                            Log.e(TAG, "outputBufferIndex:" + outputBufferIndex + " count:" + index);
                            // 获取数据
                            ByteBuffer outBuffer = mAudioCodec.getOutputBuffers()[outputBufferIndex];

                            outBuffer.position(bufferInfo.offset);
                            outBuffer.limit(bufferInfo.offset + bufferInfo.size);

                            // 修改视频的 pts,基准时间戳
                            if (audioPts == 0)
                                audioPts = bufferInfo.presentationTimeUs;
                            bufferInfo.presentationTimeUs -= audioPts;

//                                System.out.println(bufferInfo.presentationTimeUs);
                            System.out.println("writeSampleData mAudioTrackIndex:" + mAudioTrackIndex);
                            // 写入音频数据
                            mMediaMuxer.writeSampleData(mAudioTrackIndex, outBuffer, bufferInfo);

                            // 释放 outBuffer
                            mAudioCodec.releaseOutputBuffer(outputBufferIndex, false);
                            outputBufferIndex = mAudioCodec.dequeueOutputBuffer(bufferInfo, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void onDestroy() {
//            videoRecorderWf.get().release();
            try {
                if (mAudioCodec != null) {
                    mAudioCodec.stop();
                    mAudioCodec.release();
                    mAudioCodec = null;
                }
                //和videoEncoderThread的退出，避免一方退出了，一方还在运行，需保持一致
                Log.e(TAG, "run: audio stopCb.await() before");
                stopCb.await();
                Log.e(TAG, "run: audio stopCb.await() after");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void requestExit() {
            shouldExit = true;
        }
    }

    private class VideoEncoderThread extends Thread {

        WeakReference<BaseVideoRecorder> videoRecorderWf;
        private boolean shouldExit = false;

        private MediaCodec mVideoCodec;
        private MediaMuxer mMediaMuxer;
        MediaCodec.BufferInfo bufferInfo;
        CyclicBarrier startCb;
        CyclicBarrier stopCb;
        long videoPts = 0;

        /**
         * 视频轨道
         */
        private int mVideoTrackIndex = -1;

        public VideoEncoderThread(WeakReference<BaseVideoRecorder> videoRecorderWf) {
            this.videoRecorderWf = videoRecorderWf;
            this.mVideoCodec = videoRecorderWf.get().mVideoCodec;
            this.mMediaMuxer = videoRecorderWf.get().mMediaMuxer;
            this.startCb = videoRecorderWf.get().startCb;
            this.stopCb = videoRecorderWf.get().stopCb;
            bufferInfo = new MediaCodec.BufferInfo();
        }

        @Override
        public void run() {
            mVideoCodec.start();

            while (true) {
                try {
                    if (shouldExit) {
                        onDestroy();
                        return;
                    }

                    // 返回有效数据填充的输出缓冲区的索引
                    int outputBufferIndex = mVideoCodec.dequeueOutputBuffer(bufferInfo, 0);
                    if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // 将mMediaCodec的指定的格式的数据轨道，设置到mMediaMuxer上
                        mVideoTrackIndex = mMediaMuxer.addTrack(mVideoCodec.getOutputFormat());
                        mMediaMuxer.start();
                        //等待audioEncoder，一起往 startCb.wait（）后面走
                        Log.e(TAG, "run: video mMediaMuxer.start()");
                        startCb.await();
                        Log.e(TAG, "run: video mMediaMuxer.await after");
                    } else {

                        while (outputBufferIndex >= 0) {

                            // 获取数据
                            ByteBuffer outBuffer = mVideoCodec.getOutputBuffers()[outputBufferIndex];

                            outBuffer.position(bufferInfo.offset);
                            outBuffer.limit(bufferInfo.offset + bufferInfo.size);

                            // 修改视频的 pts,基准时间戳
                            if (videoPts == 0)
                                videoPts = bufferInfo.presentationTimeUs;
                            bufferInfo.presentationTimeUs -= videoPts;

//                                System.out.println(bufferInfo.presentationTimeUs);
                            // 写入数据
                            System.out.println("writeSampleData mVideoTrackIndex:" + mVideoTrackIndex);
                            mMediaMuxer.writeSampleData(mVideoTrackIndex, outBuffer, bufferInfo);

                            if (videoRecorderWf.get().recordInfoListener != null) {
                                // us，需要除以1000转为 ms
                                videoRecorderWf.get().recordInfoListener.onTime(bufferInfo.presentationTimeUs / 1000);
                            }

                            // 释放 outBuffer
                            mVideoCodec.releaseOutputBuffer(outputBufferIndex, false);
                            outputBufferIndex = mVideoCodec.dequeueOutputBuffer(bufferInfo, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void onDestroy() {
            try {
                if (mVideoCodec != null) {
                    mVideoCodec.stop();
                    mVideoCodec.release();
                    mVideoCodec = null;
                }
                Log.e(TAG, "run: video stopCb.await() before");
                stopCb.await();
                Log.e(TAG, "run: video stopCb.await() after");
                if (mMediaMuxer != null) {
                    mMediaMuxer.stop();
                    mMediaMuxer.release();
                    mMediaMuxer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void requestExit() {
            shouldExit = true;
        }
    }

    private long drawTime;

    private class VideoRenderThread extends Thread {

        private WeakReference<BaseVideoRecorder> mVideoRecorderWf;

        boolean mShouldExit;
        EglHelper mEglHelper;
        boolean hasCreateEglContext = false;
        boolean hasSurfaceCreated = false;
        boolean hasSurfaceChanged = false;
        boolean hasDrawFrame = false;
        private int mWidth;
        private int mHeight;
        GL10 egl;


        public VideoRenderThread(WeakReference<BaseVideoRecorder> mVideoRecorderWf) {
            this.mVideoRecorderWf = mVideoRecorderWf;
            mEglHelper = new EglHelper();
        }

        private void requestExit() {
            mShouldExit = true;
        }

        public void run() {

            while (true) {
                // 按下结束时能退出
                if (mShouldExit) {
                    onDestroy();
                    return;
                }

                BaseVideoRecorder baseVideoRecorder = mVideoRecorderWf.get();

                // 根据GLSurfaceView源码中的循环绘制流程
                // GLSurfaceView绘制源码解析：https://www.jianshu.com/p/369d5694c8ca
                if (!hasCreateEglContext) {
//                    mEglHelper.initCreateEgl(baseVideoRecorder.mSurface, baseVideoRecorder.mEglContext);
                    hasCreateEglContext = true;
                }

//                egl = (GL10) mEglHelper.getEglContext().getGL();

                Log.e(TAG, "VideoRenderThread run: " + baseVideoRecorder.mRender.toString());
                if (!hasSurfaceCreated) {
                    // 调用mRender的onSurfaceCreated，做参数和纹理等的初始化
                    baseVideoRecorder.mRender.onSurfaceCreated(egl, mEglHelper.getEglConfig());
                    hasSurfaceCreated = true;
                }

                if (!hasSurfaceChanged) {
                    // 调用mRender的onSurfaceChanged，做窗口的初始化，和变换
                    baseVideoRecorder.mRender.onSurfaceChanged(egl, mWidth, mHeight);
                    hasSurfaceChanged = true;
                }

                drawTime = System.currentTimeMillis();
//                    System.out.println("onDrawFrame:"+drawTime);
                baseVideoRecorder.mRender.onDrawFrame(egl);

                // 绘制到 MediaCodec 的 Surface 上面去
//                mEglHelper.swapBuffers();

                try {
                    //休眠33毫秒，30fps，一秒需要30帧
                    Thread.sleep(16);
                } catch (Exception r) {
                    r.printStackTrace();
                }
            }
        }

        private void onDestroy() {
//            mEglHelper.destroy();
//            mVideoRecorderWf.get().release();
        }

        public void setSize(int mWidth, int mHeight) {
            this.mWidth = mWidth;
            this.mHeight = mHeight;
        }
    }

}
