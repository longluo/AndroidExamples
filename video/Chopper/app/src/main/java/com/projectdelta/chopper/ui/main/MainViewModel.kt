package com.projectdelta.chopper.ui.main

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectdelta.chopper.BuildConfig
import com.projectdelta.chopper.di.qualifiers.IODispatcher
import com.projectdelta.chopper.util.Constants
import com.projectdelta.chopper.util.cipher.ImageSteganography
import com.projectdelta.chopper.util.system.lang.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val assetManager: AssetManager
) : ViewModel() {

    val versionSignature : String
        get() = "App version : ${BuildConfig.VERSION_CODE} \nJNI version : ${Constants.coreJniVersion}, \nOpenCV version : ${Constants.openCVVersion}"

    fun copyAssets(assetName: String, where : File) {
        viewModelScope.launch(dispatcher) {
            FileUtils.copyAssets(assetManager, assetName, where)
        }
    }

    fun encode(source: String, blob: String, out: String){
        viewModelScope.launch(dispatcher) {
            val ret = ImageSteganography.encode(source, blob, out)
            Timber.d("Encode Completed, res : $ret")
        }
    }

    fun decode(source: String, onComplete: (String) -> Unit ){
        viewModelScope.launch(dispatcher) {
            val ret = ImageSteganography.decode(source)
            Timber.d("Decode Completed, res : $ret")
            onComplete(ret)
        }
    }

}
