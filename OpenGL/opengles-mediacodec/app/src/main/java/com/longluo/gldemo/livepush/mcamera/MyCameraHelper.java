package com.longluo.gldemo.livepush.mcamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyCameraHelper {
    Context mContext;
    SurfaceTexture surfaceTexture;
    int mWidth;
    int mHeight;
    
    Camera camera;
//    SurfaceHolder surfaceHolder;

    public MyCameraHelper(Context context) {
        this.mContext = context;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }
    int cameraId;
    public void openCamera(int cameraId) {
        close();

        try {
            this.cameraId = cameraId;
            camera = Camera.open(cameraId);
            camera.setPreviewTexture(surfaceTexture);
//            camera.setPreviewDisplay(surfaceHolder);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            //默认格式，yuv420
            parameters.setPreviewFormat(ImageFormat.NV21);

            Camera.Size pictureSize = getOptimalSize(parameters.getSupportedPictureSizes(),mWidth,mHeight);
            parameters.setPictureSize(pictureSize.width,pictureSize.height);

            Camera.Size previewSize = getOptimalSize(parameters.getSupportedPreviewSizes(),mWidth,mHeight);
            parameters.setPreviewSize(previewSize.width,previewSize.height);

//            int degree = calculateCameraPreviewOrientation((Activity) mContext);
            //旋转角介绍
//            https://www.jianshu.com/p/f8d0d1467584
//            camera.setDisplayOrientation(degree);

            camera.setParameters(parameters);

            camera.startPreview();
            camera.autoFocus(null);

            Log.e("TAG", "开始预览相机：" + cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取最合适的尺寸
     *
     * @param supportList
     * @param width
     * @param height
     * @return
     */
    private static Camera.Size getOptimalSize(List<Camera.Size> supportList, int width, int height) {
        // camera的宽度是大于高度的，这里要保证expectWidth > expectHeight
        int expectWidth = Math.max(width, height);
        int expectHeight = Math.min(width, height);
        // 根据宽度进行排序,升序
        Collections.sort(supportList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size pre, Camera.Size after) {
                if (pre.width > after.width) {
                    return 1;
                } else if (pre.width < after.width) {
                    return -1;
                }
                return 0;
            }
        });

        Camera.Size result = supportList.get(0);
        boolean widthOrHeight = false; // 判断存在宽或高相等的Size
        // 辗转计算宽高最接近的值
        for (Camera.Size size : supportList) {
            // 如果宽高相等，则直接返回
            if (size.width == expectWidth && size.height == expectHeight) {
                result = size;
                break;
            }
            // 仅仅是宽度相等，计算高度最接近的size
            if (size.width == expectWidth) {
                widthOrHeight = true;
                if (Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            }
            // 高度相等，则计算宽度最接近的Size
            else if (size.height == expectHeight) {
                widthOrHeight = true;
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)) {
                    result = size;
                }
            }
            // 如果之前的查找不存在宽或高相等的情况，则计算宽度和高度都最接近的期望值的Size
            else if (!widthOrHeight) {
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)
                        && Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            }
        }
        return result;
    }

    public void close() {
        if(camera != null){
            camera.stopPreview();
            camera.release();
            Log.e("TAG", "停止预览相机");
            camera = null;
        }
    }

    public void setViewWidth(int width) {
        this.mWidth = width;
    }

    public void setViewHeight(int height) {
        this.mHeight = height;
    }

//    public void setHolder(SurfaceHolder mHolder) {
//        this.surfaceHolder = mHolder;
//    }

    public int calculateCameraPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public Boolean newCameraFocus(Point point, Camera.AutoFocusCallback autoFocusCallback) {
        try {
            if(camera ==null){
                throw new RuntimeException("camera is null");
            }
            Point cameraFocusPoint = convertToCameraPoint(point);
            Rect cameraRect = convertToCameraRect(cameraFocusPoint,100);
            Camera.Parameters parameters =camera.getParameters();
            //不支持设置自定义聚焦，则使用自动聚焦，返回
            if(parameters.getMaxNumFocusAreas()<=0){
                return focus(autoFocusCallback);
            }

            clearCameraFocus();
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(cameraRect,100));

            parameters.setFocusAreas(focusAreas);
            //设置测光区域列
            parameters.setMeteringAreas(focusAreas);
            camera.setParameters(parameters);

            return focus(autoFocusCallback);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }finally {
            return focus(autoFocusCallback);
        }
    }

    private void clearCameraFocus() {
        try {
            if (camera == null) {
                throw new RuntimeException("mCamera is null");
            }
            camera.cancelAutoFocus();
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusAreas(null);
            //设置测光区域列
            parameters.setMeteringAreas(null);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Boolean focus(Camera.AutoFocusCallback autoFocusCallback) {
        camera.cancelAutoFocus();
        camera.autoFocus(autoFocusCallback);
        return true;
    }

    private Rect convertToCameraRect(Point cameraFocusPoint, int weight) {
        int left = limit(cameraFocusPoint.x-weight,1000,-1000);
        int right = limit(cameraFocusPoint.x+weight,1000,-1000);
        int bottom = limit(cameraFocusPoint.y+weight,1000,-1000);
        int top = limit(cameraFocusPoint.y-weight,1000,-1000);
        return new Rect(left,top,right,bottom);
    }

    private int limit(int x, int max, int min) {
        if(x>max)
            return max;
        if (x<min)
            return min;
        return x;
    }

    private Point convertToCameraPoint(Point point) {

        int newY = point.x*2000 / mHeight - 1000;
        //减去1000的半个范围后，需要取负
        //camera坐标是正常竖屏往左旋转90度的
        //坐标系说明，https://blog.csdn.net/afei__/article/details/52033466
        int newx = -(point.x*2000 / mWidth - 1000);
        return new Point(newx,newY);
    }
}
