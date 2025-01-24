package com.projectdelta.chopper

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import com.projectdelta.chopper.ui.exception.ExceptionActivity
import com.projectdelta.chopper.util.Constants.Companion.CHOPPER_LIBS
import com.projectdelta.chopper.util.system.CustomDebugTree
import com.projectdelta.chopper.util.system.lang.ExceptionListener
import com.projectdelta.chopper.util.system.lang.LibraryLoader
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class ChopperApplication : Application(), ExceptionListener {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(CustomDebugTree())
            setupStrictMode()
        }

        setupExceptionHandler()
    }

    private fun setupStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskWrites()
//				.detectAll()
                .penaltyLog()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // TODO Make sure you are logging this issue some where like Crashlytics.
        Timber.e(throwable, "Caught an uncaught exception at Application level")

        Intent(this, ExceptionActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }.also {
            startActivity(it)
        }

    }

    private fun setupExceptionHandler() {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            uncaughtException(t, e)
        }
    }

    companion object {
        init {
            LibraryLoader.load(CHOPPER_LIBS)
        }
    }
}
