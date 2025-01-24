/*
 * Copyright (c) 2022. Anshul Saraf
 */

package com.projectdelta.chopper.ui.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.projectdelta.chopper.data.preferences.PreferencesManager
import com.projectdelta.chopper.di.NetworkModule
import com.projectdelta.chopper.di.PreferencesModule
import com.projectdelta.chopper.util.NotFound
import com.projectdelta.chopper.util.networking.connectivity.ConnectivityManager
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {

    /**
     * Injects dependencies in classes not supported by Hilt
     * [Refer](https://developer.android.com/training/dependency-injection/hilt-android#not-supported)
     */
    private val connectivityManagerHiltEntryPoint: NetworkModule.ConnectivityManagerProviderEntryPoint by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            NetworkModule.ConnectivityManagerProviderEntryPoint::class.java
        )
    }
    private val preferencesManagerHiltEntryPoint: PreferencesModule.PreferenceManagerProviderEntryPoint by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            PreferencesModule.PreferenceManagerProviderEntryPoint::class.java
        )
    }

    lateinit var connectivityManager: ConnectivityManager
    lateinit var preferenceManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = preferencesManagerHiltEntryPoint.providesPreferenceManager()

        connectivityManager = connectivityManagerHiltEntryPoint.connectivityManager()
        connectivityManager.registerConnectionObserver()

        // FIXME : move livedata to flow , live-data is observed on ui thread.
        connectivityManager.isNetworkAvailable.observe(this@BaseActivity) { isOnline ->
            onNetworkStateChange(isOnline)
        }
    }

    open fun onNetworkStateChange(isNetworkAvailable : Boolean){}

    override fun onDestroy() {
        connectivityManager.unregisterConnectionObserver()
        super.onDestroy()
    }

    /**
     * Sets secure flag (mostly prevents screenshots)
     */
    protected fun setSecureMode(){
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

}
