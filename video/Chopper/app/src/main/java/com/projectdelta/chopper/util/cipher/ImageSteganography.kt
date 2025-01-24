package com.projectdelta.chopper.util.cipher

@Suppress("KotlinJniMissingFunction")
object ImageSteganography {

    fun encode(source: String, blob: String, out: String) =
        nativeEncode(source, blob, out)

    fun decode(`in` : String) =
        nativeDecode(`in`)

    @JvmStatic private external fun nativeEncode(source: String, blob: String, out: String) : Int

    @JvmStatic private external fun nativeDecode(`in` : String) : String
}
