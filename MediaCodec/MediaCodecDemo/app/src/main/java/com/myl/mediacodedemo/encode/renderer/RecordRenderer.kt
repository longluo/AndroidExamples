package com.myl.mediacodedemo.encode.renderer

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.myl.mediacodedemo.encode.gl.GLImageFilter
import com.myl.mediacodedemo.encode.gl.filter.GLImageOESInputFilter
import com.myl.mediacodedemo.encode.viewmodel.RecordViewModel
import com.myl.mediacodedemo.utils.OpenGLUtils
import com.myl.mediacodedemo.utils.TextureRotationUtils
import java.lang.ref.WeakReference
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RecordRenderer(private val recordViewModel: RecordViewModel) : GLSurfaceView.Renderer {
    // 顶点坐标缓冲
    private lateinit var mVertexBuffer: FloatBuffer

    // 纹理坐标缓冲
    private lateinit var mTextureBuffer: FloatBuffer

    // 预览顶点坐标缓冲
    private lateinit var mDisplayVertexBuffer: FloatBuffer

    // 预览纹理坐标缓冲
    private lateinit var mDisplayTextureBuffer: FloatBuffer

    private lateinit var mInputFilter: GLImageOESInputFilter // 相机输入滤镜

    private lateinit var mImageFilter: GLImageFilter // 输出滤镜

    // 输入纹理
    private var mInputTexture = OpenGLUtils.GL_NOT_TEXTURE

    private val mMatrix = FloatArray(16)

    @Volatile
    private var mNeedToAttach = false
    private var mWeakSurfaceTexture: WeakReference<SurfaceTexture>? = null

    // 控件视图大小
    private var mViewWidth = 0
    private var mViewHeight = 0

    // 输入纹理大小
    private var mTextureWidth = 0
    private var mTextureHeight = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)
        mDisplayVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mDisplayTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)
        //关闭抖动算法
        GLES30.glDisable(GL10.GL_DITHER)
        GLES30.glClearColor(0f, 0f, 0f, 0f)
        //开启背面剪裁
        GLES30.glEnable(GL10.GL_CULL_FACE)
        GLES30.glEnable(GL10.GL_DEPTH_TEST)
        initFilters()
        //todo 这里为啥还要大费周章，直接设置不就行了
        recordViewModel.onBindSharedContext(EGL14.eglGetCurrentContext())
    }

    private fun initFilters() {
        recordViewModel.fragmentActivity?.apply {
            mInputFilter = GLImageOESInputFilter(this)
        }
        mImageFilter = GLImageFilter()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mViewWidth = width
        mViewHeight = height
        onFilterSizeChanged()
//        adjustCoordinateSize()
    }

    private fun onFilterSizeChanged() {
        mInputFilter.apply {
            onInputSizeChanged(mTextureWidth, mTextureHeight)
            initFrameBuffer(mTextureWidth, mTextureHeight)
            onDisplaySizeChanged(mViewWidth, mViewHeight)
        }
        mImageFilter.apply {
            onInputSizeChanged(mTextureWidth, mTextureHeight)
            onDisplaySizeChanged(mViewWidth, mViewHeight)
        }
    }

    override fun onDrawFrame(p0: GL10?) {
        if (mWeakSurfaceTexture == null || mWeakSurfaceTexture?.get() == null) {
            return
        }

        // 更新纹理
        var timeStamp = 0L
        synchronized(this) {
            val surfaceTexture = mWeakSurfaceTexture?.get()
            surfaceTexture?.let {
                updateSurfaceTexture(it)
            }
            timeStamp = surfaceTexture?.timestamp ?: 0L
        }
        GLES30.glClearColor(0f, 0f, 0f, 0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        if (mInputFilter == null || mImageFilter == null) {
            return
        }
        mInputFilter.setTextureTransformMatrix(mMatrix)
        // 将OES纹理绘制到FBO中
        var currentTexture = mInputTexture
        currentTexture = mInputFilter.drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer)
        // 将最终的结果会是预览
        mImageFilter.drawFrame(currentTexture, mDisplayVertexBuffer, mDisplayTextureBuffer)
        // 录制视频
        recordViewModel.onRecordFrameAvailable(currentTexture, timeStamp)
    }

    /**
     * 更新输入纹理
     * @param surfaceTexture
     */
    private fun updateSurfaceTexture(surfaceTexture: SurfaceTexture) {
        // 绑定到当前的输入纹理
        synchronized(this) {
            if (mNeedToAttach) {
                if (mInputTexture != OpenGLUtils.GL_NOT_TEXTURE) {
                    OpenGLUtils.deleteTexture(mInputTexture)
                }
                mInputTexture = OpenGLUtils.createOESTexture()
                try {
                    surfaceTexture.attachToGLContext(mInputTexture)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mNeedToAttach = false
            }
        }
        try {
            surfaceTexture.updateTexImage()
            surfaceTexture.getTransformMatrix(mMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 绑定纹理
     * @param surfaceTexture
     */
    fun bindSurfaceTexture(surfaceTexture: SurfaceTexture) {
        synchronized(this) {
            if (mWeakSurfaceTexture == null || mWeakSurfaceTexture?.get() !== surfaceTexture) {
                mWeakSurfaceTexture = WeakReference(surfaceTexture)
                mNeedToAttach = true
            }
        }
    }

    /**
     * 设置纹理大小
     * @param width
     * @param height
     */
    fun setTextureSize(width: Int, height: Int) {
        mTextureWidth = width
        mTextureHeight = height
        if (mViewWidth != 0 && mViewHeight != 0) {
            onFilterSizeChanged()
//            adjustCoordinateSize()
        }
    }

    /**
     * 调整由于surface的大小与SurfaceView大小不一致带来的显示问题
     */
    private fun adjustCoordinateSize() {
        val textureCoord: FloatArray
        val vertexCoord = TextureRotationUtils.CubeVertices
        val textureVertices = TextureRotationUtils.TextureVertices
        val ratioMax = Math.max(
            mViewWidth.toFloat() / mTextureWidth,
            mViewHeight.toFloat() / mTextureHeight
        )
        // 新的宽高
        val imageWidth = mTextureWidth * ratioMax
        val imageHeight = mTextureHeight * ratioMax
        // 获取视图跟texture的宽高比
        val ratioWidth = imageWidth / mViewWidth.toFloat()
        val ratioHeight = imageHeight / mViewHeight.toFloat()
        val distHorizontal = (1 - 1 / ratioWidth) / 2
        val distVertical = (1 - 1 / ratioHeight) / 2
        textureCoord = floatArrayOf(
            addDistance(textureVertices[0], distHorizontal),
            addDistance(textureVertices[1], distVertical),
            addDistance(textureVertices[2], distHorizontal),
            addDistance(textureVertices[3], distVertical),
            addDistance(textureVertices[4], distHorizontal),
            addDistance(textureVertices[5], distVertical),
            addDistance(textureVertices[6], distHorizontal),
            addDistance(textureVertices[7], distVertical)
        )
        // 更新VertexBuffer 和 TextureBuffer
        mDisplayVertexBuffer.clear()
        mDisplayVertexBuffer.put(vertexCoord).position(0)
        mDisplayTextureBuffer.clear()
        mDisplayTextureBuffer.put(textureCoord).position(0)
    }

    private fun addDistance(coordinate: Float, distance: Float): Float {
        return if (coordinate == 0.0f) distance else 1 - distance
    }

    /**
     * 清理一些缓存数据
     */
    fun clear() {
        mWeakSurfaceTexture?.clear()
    }
}