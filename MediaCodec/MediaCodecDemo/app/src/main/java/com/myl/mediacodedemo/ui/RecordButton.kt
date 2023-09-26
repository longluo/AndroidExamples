package com.myl.mediacodedemo.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Xfermode
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.myl.mediacodedemo.R
import com.myl.mediacodedemo.utils.DensityUtils.dp2px

/**
 * 录制按钮
 */
class RecordButton @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    mContext, attrs, defStyleAttr
) {
    // 中间的矩形
    private var mRectPaint: Paint? = null

    // 中间圆形(矩形)的默认颜色
    private var mCircleColor = Color.WHITE

    // 绘制圆环
    private var mCirclePaint: Paint? = null

    // 圆环默认颜色
    private var mStrokeColor = Color.parseColor("#33ffffff")
    private var mCorner = 0f
    private var mCircleRadius = 0f
    private var mCircleStrokeWidth = 0f
    private var mRectWidth = 0f
    private var mMinCircleRadius = 0f
    private var mMaxCircleRadius = 0f
    private var mMinRectWidth = 0f
    private var mMaxRectWidth = 0f
    private var mMinCorner = 0f
    private var mMaxCorner = 0f
    private var mMinCircleStrokeWidth = 0f
    private var mMaxCircleStrokeWidth = 0f
    private val mRectF = RectF()
    private var mRecordMode = RecordMode.IDLE
    private val mStartAnimatorSet = AnimatorSet()
    private val mStopAnimatorSet = AnimatorSet()
    private val mXfermode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private val mHandler = Handler()
    private val mTouchRunnable = TouchRunnable()
    private var mRecordStateListener: RecordStateListener? = null
    private var mInitX = 0f
    private var mInitY = 0f
    private var mDownRawX = 0f
    private var mDownRawY = 0f
    private var mInfectionPoint = 0f
    private var mSwipeDirection: SwipeDirection? = null
    private var mHasCancel = false

    // 是否允许录制
    private var mRecordEnable = true
    private fun init(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RecordButton)
        try {
            mMinCircleStrokeWidth = dp2px(context, StrokeWidthMin.toFloat()).toFloat()
            mMinCircleStrokeWidth = ta.getDimension(
                R.styleable.RecordButton_circleStrokeWidthMin,
                mMinCircleStrokeWidth
            )
            mMaxCircleStrokeWidth = dp2px(context, StrokeWidthMax.toFloat()).toFloat()
            mMaxCircleStrokeWidth = ta.getDimension(
                R.styleable.RecordButton_circleStrokeWidthMax,
                mMaxCircleStrokeWidth
            )
            mCircleStrokeWidth = mMinCircleStrokeWidth
            mCircleColor = ta.getColor(R.styleable.RecordButton_circleColor, mCircleColor)
            mStrokeColor = ta.getColor(R.styleable.RecordButton_strokeColor, mStrokeColor)
            mMaxRectWidth = ta.getDimension(R.styleable.RecordButton_rectWidthMax, mMaxRectWidth)
            mMinRectWidth = ta.getDimension(R.styleable.RecordButton_rectWidthMin, mMinRectWidth)
            mMinCorner = dp2px(context, 5f).toFloat()
            mMinCorner = ta.getDimension(R.styleable.RecordButton_rectCorner, mMinCorner)
        } finally {
            ta.recycle()
        }
        setLayerType(LAYER_TYPE_HARDWARE, null)
        mRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectPaint!!.style = Paint.Style.FILL
        mRectPaint!!.color = mCircleColor
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint!!.color = mStrokeColor
        mCirclePaint!!.strokeWidth = mCircleStrokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        val width = measuredWidth
        val height = measuredHeight
        val centerX = width / 2
        val centerY = height / 2
        if (mMaxRectWidth == 0f) {
            mMaxRectWidth = (width / 3).toFloat()
        }
        if (mMinRectWidth == 0f) {
            mMinRectWidth = mMaxRectWidth * 0.6f
        }
        mMinCircleRadius = mMaxRectWidth / 2 + mMinCircleStrokeWidth + dp2px(mContext, 5f)
        mMaxCircleRadius = width / 2f - mMaxCircleStrokeWidth
        mMaxCorner = mMaxRectWidth / 2
        if (mRectWidth == 0f) {
            mRectWidth = mMaxRectWidth
        }
        if (mCircleRadius == 0f) {
            mCircleRadius = mMinCircleRadius
        }
        if (mCorner == 0f) {
            mCorner = mRectWidth / 2
        }

        // 绘制圆环部分
        mCirclePaint!!.color = mStrokeColor
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mCircleRadius, mCirclePaint!!)
        mCirclePaint!!.xfermode = mXfermode

        // 透明区域
        mCirclePaint!!.color = mClipColor
        canvas.drawCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            mCircleRadius - mCircleStrokeWidth,
            mCirclePaint!!
        )
        mCirclePaint!!.xfermode = null
        mRectF.left = centerX - mRectWidth / 2
        mRectF.right = centerX + mRectWidth / 2
        mRectF.top = centerY - mRectWidth / 2
        mRectF.bottom = centerY + mRectWidth / 2
        canvas.drawRoundRect(mRectF, mCorner, mCorner, mRectPaint!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 如果不允许录制，则进度拍照状态
        if (!mRecordEnable) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (mRecordMode == RecordMode.IDLE && inBeginRange(event)) {
                mDownRawX = event.rawX
                mDownRawY = event.rawY
                startAnimate()
                mHandler.postDelayed(mTouchRunnable, TOUCH_DURATION.toLong())
                if (mRecordStateListener != null) {
                    mRecordStateListener!!.onRecordStart()
                }
            }

            MotionEvent.ACTION_MOVE -> if (!mHasCancel) {
                if (mRecordMode == RecordMode.MODE_PRESS) {
                    val mOldDirection = mSwipeDirection
                    val oldY = y
                    x = mInitX + event.rawX - mDownRawX
                    y = mInitY + event.rawY - mDownRawY
                    val newY = y
                    mSwipeDirection = if (newY <= oldY) {
                        SwipeDirection.SWIPE_UP
                    } else {
                        SwipeDirection.SWIPE_DOWN
                    }
                    if (mOldDirection != mSwipeDirection) {
                        mInfectionPoint = oldY
                    }
                    val zoomPercentage = (mInfectionPoint - y) / mInitY
                    if (mRecordStateListener != null) {
                        mRecordStateListener!!.onZoom(zoomPercentage)
                    }
                }
            }

            MotionEvent.ACTION_UP -> if (!mHasCancel) {
                if (mRecordMode == RecordMode.MODE_PRESS) {
                    if (mRecordStateListener != null) {
                        mRecordStateListener!!.onRecordStop()
                    }
                    resetPress(event.x, event.y)
                } else if (mRecordMode == RecordMode.IDLE && inBeginRange(event)) {
                    mHandler.removeCallbacks(mTouchRunnable)
                    mRecordMode = RecordMode.MODE_CLICK
                } else if (mRecordMode == RecordMode.MODE_CLICK && inEndRange(event)) {
                    if (mRecordStateListener != null) {
                        mRecordStateListener!!.onRecordStop()
                    }
                    resetClick()
                }
            } else {
                mHasCancel = false
            }

            else -> {
            }
        }
        return true
    }

    private fun inBeginRange(event: MotionEvent): Boolean {
        val centerX = measuredWidth / 2
        val centerY = measuredHeight / 2
        val minX = (centerX - mMinCircleRadius).toInt()
        val maxX = (centerX + mMinCircleRadius).toInt()
        val minY = (centerY - mMinCircleRadius).toInt()
        val maxY = (centerY + mMinCircleRadius).toInt()
        val isXInRange = event.x >= minX && event.x <= maxX
        val isYInRange = event.y >= minY && event.y <= maxY
        return isXInRange && isYInRange
    }

    private fun inEndRange(event: MotionEvent): Boolean {
        val minX = 0
        val maxX = measuredWidth
        val minY = 0
        val maxY = measuredHeight
        val isXInRange = event.x >= minX && event.x <= maxX
        val isYInRange = event.y >= minY && event.y <= maxY
        return isXInRange && isYInRange
    }

    private fun resetPress(x: Float, y: Float) {
        mRecordMode = RecordMode.IDLE
        mStartAnimatorSet.cancel()
        stopAnimate()
        setX(mInitX)
        setY(mInitY)
    }

    private fun resetClick() {
        mRecordMode = RecordMode.IDLE
        mStartAnimatorSet.cancel()
        stopAnimate()
    }

    /**
     * 重置录制按钮
     */
    fun reset() {
        if (mRecordMode == RecordMode.MODE_PRESS) {
            resetPress(0f, 0f)
        } else if (mRecordMode == RecordMode.MODE_CLICK) {
            resetClick()
        } else if (mRecordMode == RecordMode.IDLE) {
            if (mStartAnimatorSet.isRunning) {
                mHasCancel = true
                mStartAnimatorSet.cancel()
                stopAnimate()
                mHandler.removeCallbacks(mTouchRunnable)
                mRecordMode = RecordMode.IDLE
            }
        }
    }

    /**
     * 打开动画
     */
    private fun startAnimate() {
        val startAnimatorSet = AnimatorSet()
        val cornerAnimator = ObjectAnimator.ofFloat(
            this, "corner",
            mMaxCorner, mMinCorner
        )
            .setDuration(ANIMATION_MIN.toLong())
        val rectSizeAnimator = ObjectAnimator.ofFloat(
            this, "rectWidth",
            mMaxRectWidth, mMinRectWidth
        )
            .setDuration(ANIMATION_MIN.toLong())
        val radiusAnimator = ObjectAnimator.ofFloat(
            this, "circleRadius",
            mMinCircleRadius, mMaxCircleRadius
        )
            .setDuration(ANIMATION_MIN.toLong())
        startAnimatorSet.playTogether(cornerAnimator, rectSizeAnimator, radiusAnimator)
        val circleWidthAnimator = ObjectAnimator.ofFloat(
            this, "circleStrokeWidth",
            mMinCircleStrokeWidth, mMaxCircleStrokeWidth, mMinCircleStrokeWidth
        )
            .setDuration(ANIMATION_MAX.toLong())
        circleWidthAnimator.repeatCount = ObjectAnimator.INFINITE
        mStartAnimatorSet.playSequentially(startAnimatorSet, circleWidthAnimator)
        mStartAnimatorSet.start()
    }

    /**
     * 结束动画
     */
    private fun stopAnimate() {
        val cornerAnimator = ObjectAnimator.ofFloat(
            this, "corner",
            mMinCorner, mMaxCorner
        )
            .setDuration(ANIMATION_MIN.toLong())
        val rectSizeAnimator = ObjectAnimator.ofFloat(
            this, "rectWidth",
            mMinRectWidth, mMaxRectWidth
        )
            .setDuration(ANIMATION_MIN.toLong())
        val radiusAnimator = ObjectAnimator.ofFloat(
            this, "circleRadius",
            mMaxCircleRadius, mMinCircleRadius
        )
            .setDuration(ANIMATION_MIN.toLong())
        val circleWidthAnimator = ObjectAnimator.ofFloat(
            this, "circleStrokeWidth",
            mMaxCircleStrokeWidth, mMinCircleStrokeWidth
        )
            .setDuration(ANIMATION_MIN.toLong())
        mStopAnimatorSet.playTogether(
            cornerAnimator,
            rectSizeAnimator,
            radiusAnimator,
            circleWidthAnimator
        )
        mStopAnimatorSet.start()
    }

    /**
     * 设置corner
     * @param corner
     */
    fun setCorner(corner: Float) {
        mCorner = corner
        invalidate()
    }

    /**
     * 设置圆的半径
     * @param circleRadius
     */
    fun setCircleRadius(circleRadius: Float) {
        mCircleRadius = circleRadius
    }

    /**
     * 设置圆环边宽
     * @param width 边宽
     */
    fun setCircleStrokeWidth(width: Float) {
        mCircleStrokeWidth = width
        invalidate()
    }

    /**
     * 设置中心矩阵的宽度
     * @param rectWidth
     */
    fun setRectWidth(rectWidth: Float) {
        mRectWidth = rectWidth
        invalidate()
    }

    internal inner class TouchRunnable : Runnable {
        override fun run() {
            if (!mHasCancel) {
                mRecordMode = RecordMode.MODE_PRESS
                mInitX = x
                mInitY = y
                mInfectionPoint = mInitY
                mSwipeDirection = SwipeDirection.SWIPE_UP
            }
        }
    }

    /**
     * 设置是否允许录制
     * @param enable false时，为点击拍照
     */
    fun setRecordEnable(enable: Boolean) {
        mRecordEnable = enable
    }

    /**
     * 添加录制状态监听器
     * @param listener
     */
    fun addRecordStateListener(listener: RecordStateListener?) {
        mRecordStateListener = listener
    }

    /**
     * 录制状态监听器
     */
    interface RecordStateListener {
        /**
         * 录制开始
         */
        fun onRecordStart()

        /**
         * 录制停止
         */
        fun onRecordStop()

        /**
         * 放大程度
         * @param percent 缩放百分比值 0 ~ 1.0
         */
        fun onZoom(percent: Float)
    }

    /**
     * 录制模式
     */
    private enum class RecordMode {
        MODE_CLICK,  // 单击状态
        MODE_PRESS,  // 长按状态
        IDLE // 默认空闲状态
    }

    /**
     * 滑动方向
     */
    private enum class SwipeDirection {
        SWIPE_UP,  // 向上滑动
        SWIPE_DOWN // 乡下滑动
    }

    companion object {
        // 触摸监听时长
        private const val TOUCH_DURATION = 200

        // 动画时长
        private const val ANIMATION_MIN = 500
        private const val ANIMATION_MAX = 1200

        // 默认边宽最大最小的dp值
        private const val StrokeWidthMin = 3
        private const val StrokeWidthMax = 12

        // 圆形与圆环之间的透明分割颜色
        private val mClipColor = Color.parseColor("#000000")
    }

    init {
        init(mContext, attrs)
    }
}