package com.projectdelta.chopper.ui.exception

import android.annotation.SuppressLint
import android.os.Bundle
import com.projectdelta.chopper.databinding.ActivityExceptionBinding
import com.projectdelta.chopper.ui.base.BaseViewBindingActivity
import com.projectdelta.chopper.util.NotFound
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExceptionActivity : BaseViewBindingActivity<ActivityExceptionBinding>() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = ActivityExceptionBinding.inflate(layoutInflater)

		setContentView(binding.root)

		initUI()

	}

	@SuppressLint("SetTextI18n")
	private fun initUI(){

		binding.emptyView.playAnimation()

		binding.emptyText.text = "Oops! something went Wrong.\n ${NotFound.surpriseMe()}"

	}
}
