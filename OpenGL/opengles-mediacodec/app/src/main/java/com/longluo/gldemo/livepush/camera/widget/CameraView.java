package com.longluo.gldemo.livepush.camera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;

import com.longluo.gldemo.livepush.camera.CameraHelper;
import com.longluo.gldemo.livepush.camera.CameraRender;
import com.longluo.gldemo.livepush.opengl.BaseGLSurfaceView;

public class CameraView extends BaseGLSurfaceView implements CameraRender.RenderListener {

    private CameraHelper mCameraHelper;

    private CameraRender mCameraRender;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean mIsFocusing;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        mCameraRender = new CameraRender(context);
        mCameraHelper = new CameraHelper(context);
        setRenderer(mCameraRender);
        mCameraRender.setOnRenderListener((CameraRender.RenderListener) this);
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            // 聚焦
//            focus((int) event.getX(), (int) event.getY());
//        }
//        return true;
//    }

//    @Override
//    public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
//        mCameraHelper.init(surfaceTexture);
//        open(mCameraId);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCameraRender.setViewHeight(getMeasuredHeight());
        mCameraRender.setViewWidth(getMeasuredWidth());
        mCameraHelper.setViewHeight(getMeasuredHeight());
        mCameraHelper.setViewWidth(getMeasuredWidth());
    }

    public void open(int cameraId) {
        rotateCameraAngle();
        mCameraHelper.open(cameraId);
    }

    /**
     * 旋转相机的角度
     */
    private void rotateCameraAngle() {
        mCameraRender.resetMatrix();
        // 前置摄像头
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCameraRender.rotateMatrix(0, 90, 0, 0, 1);
            mCameraRender.rotateMatrix(0, 180, 1, 0, 0);
        }
        // 后置摄像头
        else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraRender.rotateMatrix(0, 90, 0, 0, 1);
        }
    }

    public void onDestroy() {
        mCameraHelper.close();
    }

//    @Override
//    public void onSurfaceCreate(SurfaceTexture surfaceTexture) {
//        mCameraHelper.init(surfaceTexture);
//        open(mCameraId);
//    }

    @Override
    public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
        mCameraHelper.init(surfaceTexture);
        open(mCameraId);
    }

//    /**
//     *
//     * @param x
//     * @param y
//     */
//    private void focus(final int x, final int y) {
//        if (mIsFocusing) {
//            return;
//        }
//
//        mIsFocusing = true;
//        Point focusPoint = new Point(x, y);
//        if (mFocusListener != null) {
//            mFocusListener.beginFocus(x, y);
//        }
//
//        mCameraHelper.newCameraFocus(focusPoint, new Camera.AutoFocusCallback() {
//            @Override
//            public void onAutoFocus(boolean success, Camera camera) {
//                mIsFocusing = false;
//                if (mFocusListener != null) {
//                    mFocusListener.endFocus(success);
//                }
//            }
//        });
//    }

    private FocusListener mFocusListener;

    public void setOnFocusListener(FocusListener focusListener) {
        this.mFocusListener = focusListener;
    }

    private interface FocusListener {
        void beginFocus(int x, int y);

        void endFocus();
    }

//    public int getTextureId() {
//        return mCameraRender.getTextureId();
//    }


}
