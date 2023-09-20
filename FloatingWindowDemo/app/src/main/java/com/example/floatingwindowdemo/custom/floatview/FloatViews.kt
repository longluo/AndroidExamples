package com.example.floatingwindowdemo.custom.floatview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.floatingwindowdemo.FloatWindowApplication
import com.example.floatingwindowdemo.R
import com.example.floatingwindowdemo.util.LogUtils


class FloatViews : LinearLayout, View.OnClickListener {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {}
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr) {
    }

    private var image: AppCompatImageView? = null
    private var text: TextView? = null

    //是否需要依附边缘
    var needAttach = false

    init {
        View.inflate(context, R.layout.float_views_layout, this)
        image = findViewById(R.id.img_float_window)
        text = findViewById(R.id.text_float_window)
        setOnClickListener(this)
        //减去虚拟按键的高度
//        screenHeight -= DeviceUtils.instance.getVirtualBarHeight(context)
    }

    override fun onClick(v: View?) {
        LogUtils.instance.getLogPrint("点击了可拖动控件" + v?.context?.packageName)
        FloatWindowApplication.startTargetActivity()
    }
}