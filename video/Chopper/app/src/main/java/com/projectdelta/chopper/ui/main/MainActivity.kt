package com.projectdelta.chopper.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.projectdelta.chopper.databinding.ActivityMainBinding
import com.projectdelta.chopper.ui.base.BaseViewBindingActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File


@AndroidEntryPoint
class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

	private val viewModel: MainViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		viewModel

		initUI()
	}

	private fun initUI(){
		val outFile = File(externalCacheDir, "1.png")
		val encodeFile = File(externalCacheDir, "encode.png")

		viewModel.copyAssets("images/op_2.png", outFile)

		binding.sampleText.text = viewModel.versionSignature

		with(binding){
			btnEncode.setOnClickListener {
				val `in` = etInput.text?.toString() ?: ""
				if(`in`.isNotEmpty())
					viewModel.encode(outFile.absolutePath, `in`, encodeFile.absolutePath)
			}

			btnDecode.setOnClickListener {
				viewModel.decode(encodeFile.absolutePath){
					Handler(Looper.getMainLooper()).post {
						twOut.text = it
					}
				}
			}
		}
	}

	override fun onNetworkStateChange(isNetworkAvailable: Boolean) {
		super.onNetworkStateChange(isNetworkAvailable)
		Timber.d("Network change : online : $isNetworkAvailable")
	}

}
