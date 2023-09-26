package com.myl.mediacodedemo.encode.camera

import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.common.util.concurrent.ListenableFuture
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraXController(@NonNull val lifecycleOwner: FragmentActivity) : BaseCameraController() {

    companion object {
        private const val DEGREE_90 = 90
        private const val DEGREE_270 = 270
        private const val TAG = "CameraXController"
    }

    // Camera提供者
    private var mCameraProvider: ProcessCameraProvider? = null

    // 预览配置
    private var mPreview: Preview? = null

    // 相机数据输出的SurfaceTexture
    private var mOutputTexture: SurfaceTexture? = null

    // 预览帧
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private var mPreviewAnalyzer: ImageAnalysis? = null

    // Camera接口
    private var mCamera: Camera? = null

    // 是否打开前置摄像头
    private var mFacingFront = false

    // 预览角度
    private var mRotation = 0

    init {
        mFacingFront = true
        mRotation = DEGREE_90
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun openCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(lifecycleOwner)
        cameraProviderFuture.addListener({
            try {
                mCameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(lifecycleOwner))
    }

    /**
     * 初始化相机配置
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun bindCameraUseCases() {
        if (mCameraProvider == null) {
            return
        }

        // 解除绑定
        mCameraProvider?.unbindAll()

        // 预览画面
        mPreview = Preview.Builder()
            .setTargetResolution(Size(mPreviewWidth, mPreviewHeight))
            .build()

        // 预览绑定SurfaceTexture
        mPreview?.setSurfaceProvider(SurfaceProvider { surfaceRequest: SurfaceRequest ->
            // 创建SurfaceTexture
            val surfaceTexture: SurfaceTexture? =
                createDetachedSurfaceTexture(surfaceRequest.resolution)
            val surface = Surface(surfaceTexture)
            surfaceRequest.provideSurface(surface, mExecutor,
                { result: SurfaceRequest.Result? -> surface.release() })
            mOutputTexture?.let { mSurfaceTextureListener?.onSurfaceTexturePrepared(it) }
        })

        // 预览帧回调
        mPreviewAnalyzer = ImageAnalysis.Builder()
            .setTargetResolution(Size(mPreviewWidth, mPreviewHeight))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        mPreviewAnalyzer?.setAnalyzer(mExecutor, PreviewCallbackAnalyzer(mPreviewCallback))

        rebindCamera()

    }

    private fun rebindCamera() {
        // 前后置摄像头选择器
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(if (isFrontCamera()) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
            .build()

        // 绑定输出
        mCamera = mCameraProvider?.bindToLifecycle(
            lifecycleOwner, cameraSelector, mPreview,
            mPreviewAnalyzer
        )
    }

    override fun switchCamera() {
        val front: Boolean = isFrontCamera()
        setFrontCamera(!front)

        // 解除绑定
        mCameraProvider?.unbindAll()
        rebindCamera()
    }

    override fun zoomIn() {
        mCamera?.apply {
            val zoomState = Objects.requireNonNull(cameraInfo.zoomState.value)
            val currentZoomRatio = zoomState?.maxZoomRatio?.coerceAtMost(zoomState.zoomRatio + 0.1f) ?: 0f
            cameraControl.setZoomRatio(currentZoomRatio)
        }
    }

    override fun zoomOut() {
        mCamera?.apply {
            val zoomState = Objects.requireNonNull(cameraInfo.zoomState.value)
            val currentZoomRatio = zoomState?.minZoomRatio?.coerceAtLeast(zoomState.zoomRatio - 0.1f) ?: 0f
            cameraControl.setZoomRatio(currentZoomRatio)
        }
    }

    override fun setFront(front: Boolean) {
        mFacingFront = front
    }

    /**
     * 创建一个SurfaceTexture并
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createDetachedSurfaceTexture(size: Size): SurfaceTexture? {
        // 创建一个新的SurfaceTexture并从解绑GL上下文
        if (mOutputTexture == null) {
            mOutputTexture = SurfaceTexture(0)
            mOutputTexture?.apply {
                setDefaultBufferSize(size.width, size.height)
                detachFromGLContext()
                setOnFrameAvailableListener(OnFrameAvailableListener { texture: SurfaceTexture? ->
                    mFrameAvailableListener?.onFrameAvailable(texture)
                })
            }
        }
        return mOutputTexture
    }


    override fun closeCamera() {
        mCameraProvider?.unbindAll()
        releaseSurfaceTexture()
    }

    override fun isFrontCamera(): Boolean {
        return mFacingFront
    }
    override fun setFrontCamera(front: Boolean) {
        mFacingFront = front
    }

    override fun getOrientation(): Int {
        return mRotation
    }

    override fun getPreviewWidth(): Int {
        return if (mRotation == DEGREE_90 || mRotation == DEGREE_270) {
            mPreviewHeight
        } else mPreviewWidth
    }

    override fun getPreviewHeight(): Int {
        return if (mRotation == DEGREE_90 || mRotation == DEGREE_270) {
            mPreviewWidth
        } else mPreviewHeight
    }

    override fun canAutoFocus(): Boolean {
        return false
    }

    override fun setFocusArea(rect: Rect?) {
    }

    override fun getFocusArea(x: Float, y: Float, width: Int, height: Int, focusSize: Int): Rect? {
        return null
    }

    override fun supportTorch(front: Boolean): Boolean {
        return mCamera?.cameraInfo?.hasFlashUnit() == true
    }

    override fun setFlashLight(on: Boolean) {
        if (supportTorch(isFrontCamera())) {
            Log.e(TAG, "Failed to set flash light: $on")
            return
        }
        mCamera?.cameraControl?.enableTorch(on)
    }

    /**
     * 释放输出的SurfaceTexture，防止内存泄露
     */
    private fun releaseSurfaceTexture() {
        mOutputTexture?.release()
    }

}