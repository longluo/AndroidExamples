package com.example.floatingwindowdemo.util

import android.util.Log

class LogUtils {

    private var TAG = "Message"

    fun getLogPrint(message: String) {
        Log.d(TAG,message)
    }

    fun getLogPrint(Tag:String,message:String){
        Log.d(Tag,message)
    }

    companion object {
        var instance = LogUtils()
    }
}