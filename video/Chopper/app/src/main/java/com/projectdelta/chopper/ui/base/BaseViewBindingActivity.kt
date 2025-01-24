/*
 * Copyright (c) 2022. Anshul Saraf
 */

package com.projectdelta.chopper.ui.base

import androidx.viewbinding.ViewBinding

@Suppress("PropertyName")
abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseActivity() {

    protected var _binding: VB? = null
    val binding: VB
        get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
