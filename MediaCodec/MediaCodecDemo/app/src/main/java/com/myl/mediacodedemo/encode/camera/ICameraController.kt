package com.myl.mediacodedemo.encode.camera

import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera

interface ICameraController {
    /**
     * 打开相机
     */
    fun openCamera()

    /**
     * 关闭相机
     */
    fun closeCamera()

    /**
     * 设置准备成功监听器
     * @param listener
     */
    fun setOnSurfaceTextureListener(listener: OnSurfaceTextureListener?)

    /**
     * 设置预览回调
     */
    fun setPreviewCallback(callback: PreviewCallback?)

    /**
     * 设置纹理更新回调
     */
    fun setOnFrameAvailableListener(listener: SurfaceTexture.OnFrameAvailableListener?)

    /**
     * 设置是否为前置摄像头
     * @param front 是否前置摄像头
     */
    fun setFrontCamera(front: Boolean)

    /**
     * 是否前置摄像头
     */
    fun isFrontCamera(): Boolean

    /**
     * 获取预览Surface的旋转角度
     */
    fun getOrientation(): Int

    /**
     * 获取预览宽度
     */
    fun getPreviewWidth(): Int

    /**
     * 获取预览高度
     */
    fun getPreviewHeight(): Int

    /**
     * 是否支持自动对焦
     */
    fun canAutoFocus(): Boolean

    /**
     * 设置对焦区域
     * @param rect 对焦区域
     */
    fun setFocusArea(rect: Rect?)

    /**
     * 获取对焦区域
     */
    fun getFocusArea(x: Float, y: Float, width: Int, height: Int, focusSize: Int): Rect?

    /**
     * 判断是否支持闪光灯
     * @param front 是否前置摄像头
     */
    fun supportTorch(front: Boolean): Boolean

    /**
     * 设置闪光灯
     * @param on 是否打开闪光灯
     */
    fun setFlashLight(on: Boolean)

    /**
     * 切换相机
     */
    fun switchCamera()

    /**
     * zoom in
     */
    fun zoomIn()

    /**
     * zoom out
     */
    fun zoomOut()

    /**
     * 设置是否为前置摄像头
     * @param front 是否前置摄像头
     */
    fun setFront(front: Boolean)
}