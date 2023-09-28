package com.longluo.gldemo.livepush.opengl;

import android.opengl.GLSurfaceView;

public abstract class BaseRender implements GLSurfaceView.Renderer {
    protected int mViewWidth;
    protected int mViewHeight;

    public void setViewHeight(int viewHeight) {
        this.mViewHeight = viewHeight;
    }

    public void setViewWidth(int viewWidth) {
        this.mViewWidth = viewWidth;
    }

}
