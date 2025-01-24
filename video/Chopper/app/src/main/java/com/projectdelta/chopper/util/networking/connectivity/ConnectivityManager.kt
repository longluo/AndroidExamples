package com.projectdelta.chopper.util.networking.connectivity

import androidx.lifecycle.MutableLiveData

interface ConnectivityManager {

	val isNetworkAvailable: MutableLiveData<Boolean>

	fun registerConnectionObserver()

	fun unregisterConnectionObserver()

}
