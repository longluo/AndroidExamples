package com.xinging.opengltest

import android.content.Context

interface IDrawer {
    fun prepare(context: Context)
    fun draw()
    fun setScreenSize(width: Int, height: Int)
}

abstract class AbstractDrawer : IDrawer{
    protected var screenWidth: Int = 0
    protected var screenHeight: Int = 0
    override fun setScreenSize(width: Int, height: Int) {
        this.screenWidth = width
        this.screenHeight = height
    }
}