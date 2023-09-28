package com.longluo.gldemo.livepush.picture;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class PictureView extends GLSurfaceView {

    PictureRender pictureRender;

    public PictureView(Context context) {
        super(context);
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        pictureRender = new PictureRender(context);
        setRenderer(pictureRender);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        pictureRender.setViewHeight(getMeasuredHeight());
        pictureRender.setViewWidth(getMeasuredWidth());
    }
}
