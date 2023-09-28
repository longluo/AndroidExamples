package com.longluo.gldemo.media.listener;

public interface MediaInfoListener {

    void musicInfo(int sampleRate, int channel);

    void callBackPcm(byte[] pcmData, int size);
}
