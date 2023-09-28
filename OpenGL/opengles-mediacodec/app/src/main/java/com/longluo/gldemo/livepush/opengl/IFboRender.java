package com.longluo.gldemo.livepush.opengl;

public interface IFboRender {
    void onBindFbo();

    void onUnbindFbo();

    void onSurfaceCreated(int viewWidth, int viewHeight);

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();
}
