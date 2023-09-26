package com.myl.mediacodedemo.encode.camera

import android.graphics.SurfaceTexture

abstract class BaseCameraController : ICameraController {

    companion object {
        // 16:9的默认宽高(理想值)，CameraX的预览方式与Camera1不一致，设置的预览宽高需要是实际的预览宽高
        private const val DEFAULT_16_9_WIDTH = 720
        private const val DEFAULT_16_9_HEIGHT = 1280
    }

    // 预览宽度
    protected var mPreviewWidth: Int = DEFAULT_16_9_WIDTH

    // 预览高度
    protected var mPreviewHeight: Int = DEFAULT_16_9_HEIGHT

    // SurfaceTexture准备监听器
    protected var mSurfaceTextureListener: OnSurfaceTextureListener? = null

    // 纹理更新监听器
    protected var mFrameAvailableListener: SurfaceTexture.OnFrameAvailableListener? = null

    // 预览回调
    protected var mPreviewCallback: PreviewCallback? = null

//    // 是否打开前置摄像头
//    private var isOpenFrontCamera = false


    override fun setOnSurfaceTextureListener(listener: OnSurfaceTextureListener?) {
        mSurfaceTextureListener = listener
    }

    override fun setPreviewCallback(callback: PreviewCallback?) {
        mPreviewCallback = callback
    }

    override fun setOnFrameAvailableListener(listener: SurfaceTexture.OnFrameAvailableListener?) {
        mFrameAvailableListener = listener
    }

}