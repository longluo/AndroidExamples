package com.longluo.gldemo.livepush.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class CameraHelper {
    private SurfaceTexture mSurfaceTexture;
    // 和camera2区别：https://www.jianshu.com/p/23e8789fbc10
    private Camera mCamera;
    private int mViewWidth;
    private int mViewHeight;
    private Context mContext;

    public CameraHelper(Context context) {
        this.mContext = context;
    }

    public void setViewHeight(int viewHeight) {
        this.mViewHeight = viewHeight;
    }

    public void setViewWidth(int viewWidth) {
        this.mViewWidth = viewWidth;
    }

    public void init(SurfaceTexture surfaceTexture) {
        this.mSurfaceTexture = surfaceTexture;
    }

    public void open(int cameraId) {
        try {
            close();
            mCamera = Camera.open(cameraId);
            mCamera.setPreviewTexture(mSurfaceTexture);

            Camera.Parameters parameters = mCamera.getParameters();
            List<int[]> fpsList = parameters.getSupportedPreviewFpsRange();

            parameters.setFlashMode("off");
            //属于yuv420,https://blog.csdn.net/xjhhjx/article/details/80291465
            parameters.setPreviewFormat(ImageFormat.NV21);

            // 直接设置 View 的大小，会有一定的兼容性问题，怎么拿合适的需要根据具体场景来选择
            Camera.Size size = getOptimalSize(parameters.getSupportedPictureSizes(), mViewWidth, mViewHeight);
            parameters.setPictureSize(size.width, size.height);
            size = getOptimalSize(parameters.getSupportedPreviewSizes(), mViewWidth, mViewHeight);
            parameters.setPreviewSize(size.width, size.height);

            mCamera.setParameters(parameters);

            mCamera.startPreview();
            mCamera.autoFocus(null);

            Log.e("TAG", "开始预览相机：" + cameraId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            Log.e("TAG", "停止预览相机");
            mCamera = null;
        }
    }

    public void change(int cameraId) {
        close();
        open(cameraId);
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

    /**
     * 对焦
     *
     * @param focusPoint 焦点位置
     * @param callback   对焦成功或失败的callback
     * @return
     */
    public boolean newCameraFocus(Point focusPoint, Camera.AutoFocusCallback callback) {
        if (mCamera == null) {
            throw new RuntimeException("mCamera is null");
        }
        Point cameraFocusPoint = convertToCameraPoint(focusPoint);
        Rect cameraFocusRect = convertToCameraRect(cameraFocusPoint, 100);
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getMaxNumFocusAreas() <= 0) {
            return focus(callback);
        }
        clearCameraFocus();
        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        // 100是权重
        focusAreas.add(new Camera.Area(cameraFocusRect, 100));
        parameters.setFocusAreas(focusAreas);
        // 设置感光区域
        parameters.setMeteringAreas(focusAreas);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return focus(callback);
    }

    /**
     * 将屏幕坐标转换成camera坐标
     *
     * @param focusPoint
     * @return cameraPoint
     */
    private Point convertToCameraPoint(Point focusPoint) {
        int newX = focusPoint.y * 2000 / mViewHeight - 1000;
        int newY = -focusPoint.x * 2000 / mViewWidth + 1000;
        return new Point(newX, newY);
    }

    private Rect convertToCameraRect(Point centerPoint, int radius) {
        int left = limit(centerPoint.x - radius, 1000, -1000);
        int right = limit(centerPoint.x + radius, 1000, -1000);
        int top = limit(centerPoint.y - radius, 1000, -1000);
        int bottom = limit(centerPoint.y + radius, 1000, -1000);
        return new Rect(left, top, right, bottom);
    }

    private static int limit(int s, int max, int min) {
        if (s > max) {
            return max;
        }
        if (s < min) {
            return min;
        }
        return s;
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        mCamera.cancelAutoFocus();
        mCamera.autoFocus(callback);
        return true;
    }

    /**
     * 清除焦点
     */
    public void clearCameraFocus() {
        if (mCamera == null) {
            throw new RuntimeException("mCamera is null");
        }
        mCamera.cancelAutoFocus();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusAreas(null);
        parameters.setMeteringAreas(null);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
