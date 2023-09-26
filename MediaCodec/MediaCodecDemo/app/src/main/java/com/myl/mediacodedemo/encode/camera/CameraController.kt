package com.myl.mediacodedemo.encode.camera

import android.app.Activity
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.annotation.RequiresApi
import java.io.IOException
import java.util.*
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class CameraController(private val activity: Activity) : BaseCameraController(),
    Camera.PreviewCallback {

    companion object {
        private const val THREAD_NAME = "FrameAvailableThread"
        private const val ONE_THOUSAND = 1000
    }

    // 摄像头id
    private var mCameraId = 0

    // 相机输出的SurfaceTexture
    private var mOutputTexture: SurfaceTexture? = null
    private var mOutputThread: HandlerThread? = null

    // 预览角度
    private var mOrientation = 0

    // 期望的fps
    private val mExpectFps = CameraParam.DESIRED_PREVIEW_FPS

    init {
        mCameraId =
            if (CameraApi.hasFrontCamera(activity)) Camera.CameraInfo.CAMERA_FACING_FRONT
            else Camera.CameraInfo.CAMERA_FACING_BACK
    }

    // 相机对象
    private var mCamera: Camera? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun openCamera() {
        closeCamera()
        mCamera = Camera.open(mCameraId)
        if (mCamera == null) {
            throw RuntimeException("Unable to open camera")
        }
        val cameraParam = CameraParam
        CameraParam.cameraId = mCameraId
        val parameters = mCamera?.parameters
        CameraParam.supportFlash = checkSupportFlashLight(parameters)
        CameraParam.previewFps =
            chooseFixedPreviewFps(parameters, mExpectFps * 1000)
        parameters?.setRecordingHint(true)
        // 后置摄像头自动对焦
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK
            && supportAutoFocusFeature(parameters)
        ) {
            mCamera!!.cancelAutoFocus()
            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        }
        mCamera!!.parameters = parameters
        setPreviewSize(mCamera, mPreviewWidth, mPreviewHeight)
        setPictureSize(mCamera, mPreviewWidth, mPreviewHeight)
        mOrientation = calculateCameraPreviewOrientation(activity)
        mCamera!!.setDisplayOrientation(mOrientation)
        releaseSurfaceTexture()
        mOutputTexture = createDetachedSurfaceTexture()
        try {
            mCamera!!.setPreviewTexture(mOutputTexture)
            mCamera!!.setPreviewCallback(this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mCamera!!.startPreview()
        mOutputTexture?.let { mSurfaceTextureListener?.onSurfaceTexturePrepared(it) }
    }

    /**
     * 创建一个SurfaceTexture并
     * @return
     */
    private fun createDetachedSurfaceTexture(): SurfaceTexture? {
        // 创建一个新的SurfaceTexture并从解绑GL上下文
        val surfaceTexture = SurfaceTexture(0)
        surfaceTexture.detachFromGLContext()
        if (Build.VERSION.SDK_INT >= 21) {
            if (mOutputThread != null) {
                mOutputThread!!.quit()
                mOutputThread = null
            }
            mOutputThread = HandlerThread(THREAD_NAME)
            mOutputThread!!.start()
            surfaceTexture.setOnFrameAvailableListener({ texture: SurfaceTexture? ->
                if (mFrameAvailableListener != null) {
                    mFrameAvailableListener!!.onFrameAvailable(texture)
                }
            }, Handler(mOutputThread!!.looper))
        } else {
            surfaceTexture.setOnFrameAvailableListener { texture: SurfaceTexture? ->
                if (mFrameAvailableListener != null) {
                    mFrameAvailableListener!!.onFrameAvailable(texture)
                }
            }
        }
        return surfaceTexture
    }

    /**
     * 判断是否支持自动对焦
     * @param parameters
     * @return
     */
    private fun supportAutoFocusFeature(parameters: Camera.Parameters?): Boolean {
        val focusModes = parameters?.supportedFocusModes
        return focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
    }


    /**
     * 设置预览角度，setDisplayOrientation本身只能改变预览的角度
     * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的
     * 拍摄的照片需要自行处理
     * 这里Nexus5X的相机简直没法吐槽，后置摄像头倒置了，切换摄像头之后就出现问题了。
     * @param activity
     */
    private fun calculateCameraPreviewOrientation(activity: Activity): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(CameraParam.cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun closeCamera() {
        mCamera?.apply {
            setPreviewCallback(null)
            setPreviewCallbackWithBuffer(null)
            addCallbackBuffer(null)
            stopPreview()
            release()
        }
        releaseSurfaceTexture()
    }

    /**
     * 检查摄像头(前置/后置)是否支持闪光灯
     * @param parameters 摄像头参数
     * @return
     */
    private fun checkSupportFlashLight(parameters: Camera.Parameters?): Boolean {
        if (parameters?.flashMode == null) {
            return false
        }
        val supportedFlashModes = parameters.supportedFlashModes
        return !(supportedFlashModes == null || supportedFlashModes.isEmpty()
                || (supportedFlashModes.size == 1
                && supportedFlashModes[0] == Camera.Parameters.FLASH_MODE_OFF))
    }

    /**
     * 释放资源
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun releaseSurfaceTexture() {
        mOutputTexture?.release()
        mOutputTexture = null

        mOutputThread?.quitSafely()
        mOutputThread = null
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        mPreviewCallback?.onPreviewFrame(data)
    }


    /**
     * 设置预览大小
     * @param camera
     * @param expectWidth
     * @param expectHeight
     */
    private fun setPreviewSize(camera: Camera?, expectWidth: Int, expectHeight: Int) {
        camera?.let {
            val parameters = camera.parameters
            val size: Camera.Size = calculatePerfectSize(
                parameters.supportedPreviewSizes,
                expectWidth, expectHeight, CalculateType.Lower
            )
            parameters.setPreviewSize(size.width, size.height)
            mPreviewWidth = size.width
            mPreviewHeight = size.height
            camera.parameters = parameters
        }
    }

    /**
     * 设置拍摄的照片大小
     * @param camera
     * @param expectWidth
     * @param expectHeight
     */
    private fun setPictureSize(camera: Camera?, expectWidth: Int, expectHeight: Int) {
        camera?.let {
            val parameters = camera.parameters
            val size: Camera.Size? = calculatePerfectSize(
                parameters.supportedPictureSizes,
                expectWidth, expectHeight, CalculateType.Max
            )
            parameters.setPictureSize(size?.width ?: 0, size?.height ?: 0)
            camera.parameters = parameters
        }
    }

    /**
     * 计算最完美的Size
     * @param sizes
     * @param expectWidth
     * @param expectHeight
     * @return
     */
    private fun calculatePerfectSize(
        sizes: List<Camera.Size>, expectWidth: Int,
        expectHeight: Int, calculateType: CalculateType
    ): Camera.Size {
        sortList(sizes) // 根据宽度进行排序

        // 根据当前期望的宽高判定
        val bigEnough: MutableList<Camera.Size> = ArrayList()
        val noBigEnough: MutableList<Camera.Size> = ArrayList()
        for (size in sizes) {
            if (size.height * expectWidth / expectHeight == size.width) {
                if (size.width > expectWidth && size.height > expectHeight) {
                    bigEnough.add(size)
                } else {
                    noBigEnough.add(size)
                }
            }
        }
        // 根据计算类型判断怎么如何计算尺寸
        var perfectSize: Camera.Size? = null
        when (calculateType) {
            CalculateType.Min ->                 // 不大于期望值的分辨率列表有可能为空或者只有一个的情况，
                // Collections.min会因越界报NoSuchElementException
                if (noBigEnough.size > 1) {
                    perfectSize = Collections.min(
                        noBigEnough,
                        CompareAreaSize()
                    )
                } else if (noBigEnough.size == 1) {
                    perfectSize = noBigEnough[0]
                }
            CalculateType.Max ->                 // 如果bigEnough只有一个元素，使用Collections.max就会因越界报NoSuchElementException
                // 因此，当只有一个元素时，直接使用该元素
                if (bigEnough.size > 1) {
                    perfectSize = Collections.max(
                        bigEnough,
                        CompareAreaSize()
                    )
                } else if (bigEnough.size == 1) {
                    perfectSize = bigEnough[0]
                }
            CalculateType.Lower ->                 // 优先查找比期望尺寸小一点的，否则找大一点的，接受范围在0.8左右
                if (noBigEnough.size > 0) {
                    val size = Collections.max(
                        noBigEnough,
                        CompareAreaSize()
                    )
                    if (size.width.toFloat() / expectWidth >= 0.8
                        && size.height.toFloat() / expectHeight > 0.8
                    ) {
                        perfectSize = size
                    }
                } else if (bigEnough.size > 0) {
                    val size = Collections.min(
                        bigEnough,
                        CompareAreaSize()
                    )
                    if (expectWidth.toFloat() / size.width >= 0.8
                        && (expectHeight / size.height).toFloat() >= 0.8
                    ) {
                        perfectSize = size
                    }
                }
            CalculateType.Larger ->                 // 优先查找比期望尺寸大一点的，否则找小一点的，接受范围在0.8左右
                if (bigEnough.size > 0) {
                    val size = Collections.min(
                        bigEnough,
                        CompareAreaSize()
                    )
                    if (expectWidth.toFloat() / size.width >= 0.8
                        && (expectHeight / size.height).toFloat() >= 0.8
                    ) {
                        perfectSize = size
                    }
                } else if (noBigEnough.size > 0) {
                    val size = Collections.max(
                        noBigEnough,
                        CompareAreaSize()
                    )
                    if (size.width.toFloat() / expectWidth >= 0.8
                        && size.height.toFloat() / expectHeight > 0.8
                    ) {
                        perfectSize = size
                    }
                }
        }
        // 如果经过前面的步骤没找到合适的尺寸，则计算最接近expectWidth * expectHeight的值
        if (perfectSize == null) {
            var result = sizes[0]
            var widthOrHeight = false // 判断存在宽或高相等的Size
            // 辗转计算宽高最接近的值
            for (size in sizes) {
                // 如果宽高相等，则直接返回
                if (size.width == expectWidth && size.height == expectHeight && size.height.toFloat() / size.width.toFloat() == CameraParam.currentRatio) {
                    result = size
                    break
                }
                // 仅仅是宽度相等，计算高度最接近的size
                if (size.width == expectWidth) {
                    widthOrHeight = true
                    if (abs(result.height - expectHeight) > abs(size.height - expectHeight)
                        && size.height.toFloat() / size.width.toFloat() == CameraParam.currentRatio
                    ) {
                        result = size
                        break
                    }
                } else if (size.height == expectHeight) {
                    widthOrHeight = true
                    if (abs(result.width - expectWidth) > abs(size.width - expectWidth)
                        && size.height.toFloat() / size.width.toFloat() == CameraParam.currentRatio
                    ) {
                        result = size
                        break
                    }
                } else if (!widthOrHeight) {
                    if (abs(result.width - expectWidth) > abs(size.width - expectWidth) && abs(
                            result.height - expectHeight
                        ) > abs(size.height - expectHeight) && size.height.toFloat() / size.width.toFloat() == CameraParam.currentRatio
                    ) {
                        result = size
                    }
                }
            }
            perfectSize = result
        }
        return perfectSize
    }

    /**
     * 分辨率由大到小排序
     * @param list
     */
    private fun sortList(list: List<Camera.Size>) {
        Collections.sort(list, CompareAreaSize())
    }

    /**
     * 比较器
     */
    private class CompareAreaSize : Comparator<Camera.Size> {
        override fun compare(pre: Camera.Size, after: Camera.Size): Int {
            return java.lang.Long.signum(
                pre.width.toLong() * pre.height -
                        after.width.toLong() * after.height
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun switchCamera() {
        var front: Boolean = !isFrontCamera()
        front = front && CameraApi.hasFrontCamera(activity)
        // 期望值不一致
        if (front != isFrontCamera()) {
            setFrontCamera(front)
            openCamera()
        }
    }

    /**
     * 选择合适的FPS
     * @param parameters
     * @param expectedThoudandFps 期望的FPS
     * @return
     */
    private fun chooseFixedPreviewFps(
        parameters: Camera.Parameters?,
        expectedThoudandFps: Int
    ): Int {
        val supportedFps = parameters?.supportedPreviewFpsRange
        if (supportedFps != null) {
            for (entry in supportedFps) {
                if (entry[0] == entry[1] && entry[0] == expectedThoudandFps) {
                    parameters.setPreviewFpsRange(entry[0], entry[1])
                    return entry[0]
                }
            }
        }
        val temp = IntArray(2)
        parameters?.getPreviewFpsRange(temp)
        return if (temp[0] == temp[1]) {
            temp[0]
        } else {
            temp[1] / 2
        }
    }

    override fun zoomIn() {
        if (canZoom()) {
            val parameters = mCamera!!.parameters
            val current = parameters.zoom
            val maxZoom = parameters.maxZoom
            parameters.zoom = (current + 1).coerceAtMost(maxZoom)
            mCamera!!.parameters = parameters
        }
    }

    override fun zoomOut() {
        if (canZoom()) {
            val parameters = mCamera!!.parameters
            val current = parameters.zoom
            parameters.zoom = (current - 1).coerceAtLeast(0)
            mCamera!!.parameters = parameters
        }
    }

    override fun setFront(front: Boolean) {
        mCameraId = if (front) {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        } else {
            Camera.CameraInfo.CAMERA_FACING_BACK
        }
    }

    private fun canZoom(): Boolean {
        return mCamera != null && mCamera!!.parameters.isZoomSupported
    }

    override fun isFrontCamera(): Boolean {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT
    }
    override fun setFrontCamera(front: Boolean) {
        mCameraId = if (front) {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        } else {
            Camera.CameraInfo.CAMERA_FACING_BACK
        }
    }

    override fun getOrientation(): Int {
        return mOrientation
    }

    override fun getPreviewWidth(): Int {
        return mPreviewWidth
    }

    override fun getPreviewHeight(): Int {
        return mPreviewHeight
    }

    override fun canAutoFocus(): Boolean {
        val focusModes = mCamera?.parameters?.supportedFocusModes
        return focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)
    }

    override fun setFocusArea(rect: Rect?) {
        mCamera?.let {
            val parameters = it.parameters // 先获取当前相机的参数配置对象
            if (supportAutoFocusFeature(parameters)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO // 设置聚焦模式
            }
            if (parameters.maxNumFocusAreas > 0) {
                val focusAreas: MutableList<Camera.Area> = ArrayList()
                focusAreas.add(Camera.Area(rect, CameraParam.Weight))
                // 设置聚焦区域
                if (parameters.maxNumFocusAreas > 0) {
                    parameters.focusAreas = focusAreas
                }
                // 设置计量区域
                if (parameters.maxNumMeteringAreas > 0) {
                    parameters.meteringAreas = focusAreas
                }
                // 取消掉进程中所有的聚焦功能
                it.parameters = parameters
                it.autoFocus { success: Boolean, camera: Camera ->
                    val params = camera.parameters
                    // 设置自动对焦
                    if (supportAutoFocusFeature(params)) {
                        camera.cancelAutoFocus()
                        params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                    }
                    camera.parameters = params
                    camera.autoFocus(null)
                }
            }
        }
    }

    override fun getFocusArea(x: Float, y: Float, width: Int, height: Int, focusSize: Int): Rect? {
        return calculateTapArea(x, y, width, height, focusSize, 1.0f)
    }

    override fun supportTorch(front: Boolean): Boolean {
        return if (front) {
            true
        } else !checkSupportFlashLight(mCamera?.parameters)
    }

    override fun setFlashLight(on: Boolean) {
        if (supportTorch(isFrontCamera())) {
            return
        }
        mCamera?.let {
            val parameters = it.parameters
            if (on) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            } else {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            }
            it.parameters = parameters
        }
    }

    /**
     * 计算点击区域
     * @param x
     * @param y
     * @param width
     * @param height
     * @param focusSize
     * @param coefficient
     * @return
     */
    private fun calculateTapArea(
        x: Float, y: Float, width: Int, height: Int,
        focusSize: Int, coefficient: Float
    ): Rect? {
        val areaSize = java.lang.Float.valueOf(focusSize * coefficient).toInt()
        val left: Int = clamp(java.lang.Float.valueOf(y / height * 2000 - ONE_THOUSAND).toInt(), areaSize)
        val top: Int =
            clamp(java.lang.Float.valueOf((height - x) / width * 2000 - ONE_THOUSAND).toInt(), areaSize)
        return Rect(left, top, left + areaSize, top + areaSize)
    }

    /**
     * 确保所选区域在在合理范围内
     * @param touchCoordinateInCameraReper
     * @param focusAreaSize
     * @return
     */
    private fun clamp(touchCoordinateInCameraReper: Int, focusAreaSize: Int): Int {
        return if (abs(touchCoordinateInCameraReper) + focusAreaSize > ONE_THOUSAND) {
                if (touchCoordinateInCameraReper > 0) {
                    ONE_THOUSAND - focusAreaSize
                } else {
                    -ONE_THOUSAND + focusAreaSize
                }
            } else {
                touchCoordinateInCameraReper - focusAreaSize / 2
            }
    }
}