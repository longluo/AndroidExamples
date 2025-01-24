/*
 * Copyright (c) 2022. Anshul Saraf
 */

package com.projectdelta.chopper.ui.base

abstract class BaseViewBindingFragment<VB> : BaseFragment() {

    protected var _binding: VB? = null
    val binding: VB
        get() = _binding!!

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
