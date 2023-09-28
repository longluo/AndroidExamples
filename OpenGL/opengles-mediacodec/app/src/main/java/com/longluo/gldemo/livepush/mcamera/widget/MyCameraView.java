package com.longluo.gldemo.livepush.mcamera.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.longluo.gldemo.livepush.camera.CameraRender;
import com.longluo.gldemo.livepush.mcamera.MyCameraHelper;
import com.longluo.gldemo.livepush.opengl.BaseGLSurfaceView;

import static android.view.MotionEvent.ACTION_UP;

public class MyCameraView extends BaseGLSurfaceView implements CameraRender.RenderListener {

    private static final String TAG = "MyCameraView";
    CameraRender myRender;
    MyCameraHelper myCameraHelper;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean mIsFocusing;

    //    SurfaceHolder mHolder;
    public MyCameraView(Context context) {
        this(context, null);
    }

    public MyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
//        mHolder = getHolder();
        myCameraHelper = new MyCameraHelper(context);
//        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        myRender = new CameraRender(context);

        setRenderer(myRender);
        myRender.setOnRenderListener(this);
    }

//    @Override
//    public void onSurfaceCreate(SurfaceTexture surfaceTexture) {
//        myCameraHelper.setSurfaceTexture(surfaceTexture);
//        rotateCameraAngle();
//
//        myCameraHelper.openCamera(cameraId);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        myRender.setViewHeight(height);
        myRender.setViewWidth(width);
//        mHolder.setFixedSize(width,height);

        myCameraHelper.setViewWidth(width);
        myCameraHelper.setViewHeight(height);
    }

    /**
     * 旋转相机的角度
     */
    private void rotateCameraAngle() {
        myRender.resetMatrix();
        // 前置摄像头
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            myRender.rotateMatrix(0, 90, 0, 0, 1);
            myRender.rotateMatrix(0, 180, 1, 0, 0);
        }
        // 后置摄像头
        else if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            myRender.rotateMatrix(0, 90, 0, 0, 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.e(TAG, "onTouchEvent focus: x=" + (int) event.getX() + ",y=" + (int) event.getY());
        if (event.getAction() == ACTION_UP) {
            focus((int) event.getX(), (int) event.getY());
        }
        return true;
//        else
//            return super.onTouchEvent(event);
    }

    private void focus(int x, int y) {
        if (mIsFocusing)
            return;
        mIsFocusing = true;
        Point point = new Point(x, y);
        if (mFocusListener != null)
            mFocusListener.beginFocus(x, y);
        Log.e(TAG, "focus: x=" + x + ",y=" + y);

        myCameraHelper.newCameraFocus(point, new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                mIsFocusing = false;
                if (mFocusListener != null)
                    mFocusListener.endFocus();
            }
        });
    }

    private FocusListener mFocusListener;

    public void setOnFocusListener(FocusListener focusListener) {
        this.mFocusListener = focusListener;
    }

    @Override
    public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
        myCameraHelper.setSurfaceTexture(surfaceTexture);
        rotateCameraAngle();

        myCameraHelper.openCamera(cameraId);
    }

    public interface FocusListener {
        void beginFocus(int x, int y);

        void endFocus();
    }

    public void onDestroy() {

        if (myCameraHelper != null) {
            myCameraHelper.close();
            myCameraHelper = null;
        }
        if (myRender != null)
            myRender = null;
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//        myCameraHelper.setHolder(mHolder);
//        myCameraHelper.openCamera(cameraId);
//    }

    public int getTextureId() {
        return myRender.getTextureId();
    }

}
