/*
 * Copyright (c) 2022. Anshul Saraf
 */

package com.projectdelta.chopper.ui.base

import androidx.fragment.app.Fragment
import kotlinx.coroutines.Job

abstract class BaseFragment : Fragment() {

    protected val jobs = mutableListOf<Job>()

    override fun onDestroy() {
        jobs.forEach { it.cancel() }
        super.onDestroy()
    }

}
