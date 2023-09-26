package com.zhengsr.opengldemo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * @author by zhengshaorui 2022/12/3
 * describe：
 */
class MainApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
    }
}