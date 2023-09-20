package com.example.floatingwindowdemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.floatingwindowdemo.custom.floatview.FloatWindowHelper

class FloatWindowApplication : Application() {

    private var activityCount = 0
    private var flowGroup: FloatWindowHelper? = null

    override fun onCreate() {
        super.onCreate()
        mContext = this
        if (flowGroup == null)
            flowGroup = FloatWindowHelper()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStarted(activity: Activity) {
                if (activityCount == 0) {
                    hideWindow()
                }
                ++activityCount
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
                --activityCount
                if (activityCount == 0) {
                    showWindow()
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }

    fun showWindow() {
        flowGroup?.showView(this)
    }

    fun hideWindow() {
        flowGroup?.hideView(this)
    }

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { FloatWindowApplication() }
        var mContext: Context? = null
        fun startTargetActivity() {
            if (mContext != null) {
                var intent = Intent()
                intent.setClass(mContext!!, TestTargetActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mContext?.startActivity(intent)
            }
        }
    }
}