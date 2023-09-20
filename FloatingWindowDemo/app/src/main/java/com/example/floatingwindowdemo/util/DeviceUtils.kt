package com.example.floatingwindowdemo.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import java.lang.reflect.Method

class DeviceUtils {

    companion object {
        val instance = DeviceUtils()
    }

    /**
     * 获取虚拟按键的高度
     */
    fun getVirtualBarHeight(context: Context): Int {
        var vh = 0
        if(!hasDeviceNavigationBar(context))
            return vh
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        try {
            val c = Class.forName("android.view.Display")
            val method: Method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            vh = dm.heightPixels - display.height
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (isMIUI()) {
            if (isFullScreen(context)) {
                vh = 0
            }
        } else {
            if (!hasDeviceNavigationBar(context)) {
                vh = 0
            }
        }
        return vh
    }


    /**
     * 判断有没有底部虚拟键
     */
    private fun hasDeviceNavigationBar(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            var display = (context as Activity).windowManager.defaultDisplay
            var size = Point()
            var realSize = Point()
            display.getSize(size);
            display.getRealSize(realSize)
            realSize.y != size.y
        } else {
            var menu = ViewConfiguration.get(context as Activity).hasPermanentMenuKey()
            var back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            !(menu || back)
        }
    }

    //用于判断小米手机是否有虚拟按键
    private fun isFullScreen(context: Context): Boolean {
        // true 是手势，默认是 false
        // https://www.v2ex.com/t/470543
        return Settings.Global.getInt(context.contentResolver, "force_fsg_nav_bar", 0) !== 0
    }

    //用来排除一些特殊型号的手机，对这些特殊型号的手机可以进行自定义
    private fun isMIUI(): Boolean {
        val manufacturer = Build.MANUFACTURER
        // 这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        return "xiaomi".equals(manufacturer, ignoreCase = true)
    }
}