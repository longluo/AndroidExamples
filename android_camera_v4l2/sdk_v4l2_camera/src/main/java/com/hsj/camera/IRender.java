package com.hsj.camera;

import android.opengl.GLSurfaceView;


public interface IRender extends GLSurfaceView.Renderer {
    void onRender(boolean isResume);

    void setSurfaceCallback(ISurfaceCallback callback);
}
