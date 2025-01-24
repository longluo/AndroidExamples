package me.longluo.audioinput;

public interface AudioCable {
    void send(float[] sample); // one multi-channel sample

    void endOfFrame();

    void endOfStream();
}
