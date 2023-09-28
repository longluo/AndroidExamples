package com.longluo.gldemo.media;

import android.text.TextUtils;
import android.util.Log;

import com.longluo.gldemo.media.listener.MediaErrorListener;
import com.longluo.gldemo.media.listener.MediaInfoListener;
import com.longluo.gldemo.media.listener.MediaPreparedListener;

/**
 * 音频播放器的逻辑处理类
 */
public class DarrenPlayer {

    private static final String TAG = "DarrenPlayer";

    static {
        System.loadLibrary("video-record");
    }

    /**
     * url 可以是本地文件路径，也可以是 http 链接
     */
    private String url;

    // called from jni
    public void onError(int code, String msg) {
        if (mErrorListener != null) {
            mErrorListener.onError(code, msg);
        }
    }

    // called from jni
    public void onPrepared() {
        if (mPreparedListener != null) {
            mPreparedListener.onPrepared();
        }
    }

    // called from jni
    public void onMediaInfo(int sampleRate, int channel) {
        if (mediaInfoListener != null) {
            mediaInfoListener.musicInfo(sampleRate,channel);
            Log.e(TAG,"rate= "+sampleRate+" channel= "+channel);
        }
    }

    // called from jni
    public void onCallBackPcmData(byte[] pcmData,int size) {
        if (mediaInfoListener != null) {
            mediaInfoListener.callBackPcm(pcmData,size);
//            Log.e(TAG,"data= "+pcmData.toString()+" channel= "+size);
        }
    }

    public void setDataSource(String url) {
        this.url = url;
    }

    public void play() {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null, please call method setDataSource");
        }
        nPlay();
    }

    public void prepare() {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null, please call method setDataSource");
        }
        nPrepare(url);
    }

    /**
     * 异步准备
     */
    public void prepareAsync() {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null, please call method setDataSource");
        }
        nPrepareAsync(url);
    }

    public void stop(){
        nStop();
    }

    private native void nPlay();

    private native void nStop();

    private native void nPrepareAsync(String url);

    private native void nPrepare(String url);


    private MediaErrorListener mErrorListener;

    private MediaPreparedListener mPreparedListener;

    public void setOnErrorListener(MediaErrorListener errorListener) {
        this.mErrorListener = errorListener;
    }

    public void setOnPreparedListener(MediaPreparedListener preparedListener) {
        this.mPreparedListener = preparedListener;
    }

    MediaInfoListener mediaInfoListener;

    public void setMediaInfoListener(MediaInfoListener mediaInfoListener) {
        this.mediaInfoListener = mediaInfoListener;
    }
}
