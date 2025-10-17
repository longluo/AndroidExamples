package com.hsj.camera;

import java.nio.ByteBuffer;


public interface IFrameCallback {
    void onFrame(ByteBuffer data);
}
