package com.projectdelta.chopper.util.networking.connectivity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Author and idea from Mitch Tabian
 * https://github.com/mitchtabian/food2fork-compose/blob/master/app/src/main/java/com/codingwithmitch/food2forkcompose/presentation/util/ConnectivityManager.kt
 * and Alex Mason
 * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/connectivity/ConnectivityManagerImpl.kt
 */
class ConnectivityManagerImpl(
	application: Application,
) : ConnectivityManager {

	private val connectionLiveData = ConnectionLiveData(application)

	private val networkObserver =
		Observer<Boolean> { isConnected -> isNetworkAvailable.postValue(isConnected) }

	override val isNetworkAvailable = MutableLiveData<Boolean>()

	override fun registerConnectionObserver() {
		connectionLiveData.observeForever(networkObserver)
	}

	override fun unregisterConnectionObserver() {
		connectionLiveData.removeObserver(networkObserver)
	}

}
