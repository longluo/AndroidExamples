package com.example.floatingwindowdemo.custom.floatview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import com.example.floatingwindowdemo.FloatWindowApplication
import com.example.floatingwindowdemo.util.LogUtils

class FloatWindowHelper {
    private var windowManager: WindowManager? = null
    private var view: FloatViews? = null
    private var lastX: Int = 0
    private var lastY: Int = 0
    private var downTime = 0L
    private var isDraged = false

    @SuppressLint("ClickableViewAccessibility")
    private fun addView(context: Context) {
        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        }
        var layoutParam = WindowManager.LayoutParams()
        //设置宽和高
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam.width = WindowManager.LayoutParams.WRAP_CONTENT
        //设置初始位置在左上角
        layoutParam.format = PixelFormat.TRANSPARENT
        layoutParam.gravity = Gravity.START or Gravity.TOP

        var displayMetrics: DisplayMetrics? = FloatWindowApplication.mContext?.resources?.displayMetrics
        var screenWidth = displayMetrics?.widthPixels ?: 0
        var screenHeight = displayMetrics?.heightPixels ?: 0
//        layoutParam.verticalMargin = 0.2f
        // FLAG_LAYOUT_IN_SCREEN：将window放置在整个屏幕之内,无视其他的装饰(比如状态栏)； FLAG_NOT_TOUCH_MODAL：不阻塞事件传递到后面的窗口
        layoutParam.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        //设置悬浮窗属性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            // 设置窗体显示类型(TYPE_TOAST:与toast一个级别)
            layoutParam.type = WindowManager.LayoutParams.TYPE_TOAST
        }
        if (view == null)
            view = FloatViews(context)
        windowManager?.addView(view, layoutParam)
        view?.needAttach = true
        view?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, ev: MotionEvent?): Boolean {
                if (v == null || ev == null)
                    return false
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = ev.rawX.toInt()
                        lastY = ev.rawY.toInt()
                        downTime = System.currentTimeMillis()
                        isDraged = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        var dx = ev.rawX.toInt() - lastX
                        var dy = ev.rawY.toInt() - lastY
                        var l = v.left + dx
                        var r = v.right + dx
                        var t = v.top + dy
                        var b = v.bottom + dy
                        //当滑动出边界时需要重新设置位置
                        if (l < 0) {
                            l = 0
                            r = v.width
                        }
                        if (t < 0) {
                            t = 0
                            b = v.height
                        }
                        v.layout(l, t, r, b)
                        lastX = ev.rawX.toInt()
                        lastY = ev.rawY.toInt()
                        layoutParam.x = lastX - v.width / 2
                        layoutParam.y = lastY - v.height / 2
                        windowManager?.updateViewLayout(v, layoutParam)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - downTime < ViewConfiguration.getTapTimeout()) {
                            v.performClick()
                            isDraged = false
                        } else {
                            isDraged = true
                        }
                        LogUtils.instance.getLogPrint(screenHeight.toString())
                        if ((v as FloatViews).needAttach && screenWidth != 0) {
                            if (lastX > screenWidth / 2) {
                                layoutParam.x = screenWidth - v.width
                            } else {
                                layoutParam.x = 0
                            }
                            windowManager?.updateViewLayout(v, layoutParam)
                        }
                    }
                }
                return isDraged
            }
        })
    }

    fun showView(context: Context) {
        if (view == null || view?.windowToken == null) {
            addView(context)
        }
        if (view != null)
            view?.visibility = View.VISIBLE
    }

    fun hideView(context: Context) {
        view?.visibility = View.GONE
    }


    companion object {
        val instance = FloatWindowHelper()
    }
}