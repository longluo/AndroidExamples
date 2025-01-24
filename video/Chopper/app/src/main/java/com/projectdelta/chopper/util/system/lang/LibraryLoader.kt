package com.projectdelta.chopper.util.system.lang

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Non blocking implementation for loading shared libraries.
 */
object LibraryLoader {

    private val dispatcher = Dispatchers.IO + SupervisorJob() // inject this

    private val loadedLibs : ConcurrentMap<String, Boolean> = ConcurrentHashMap()

    fun load(vararg libs : String){
        for(lib in libs){
            if( ! loadedLibs.contains(lib) ){
                CoroutineScope(dispatcher).launch {
                    System.loadLibrary(lib)
                    loadedLibs[lib] = true
                }
            }
        }
    }

}
