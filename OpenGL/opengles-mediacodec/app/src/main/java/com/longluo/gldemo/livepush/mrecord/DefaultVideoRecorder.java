package com.longluo.gldemo.livepush.mrecord;

import android.content.Context;

import javax.microedition.khronos.egl.EGLContext;

public class DefaultVideoRecorder extends BaseVideoRecorder {

    public DefaultVideoRecorder(Context context, EGLContext eglContext) {
        super(context, eglContext);
    }

    public void setRenderId(int textureId) {
        setRender(new RecorderRenderer(super.mContext, textureId));
    }
}
