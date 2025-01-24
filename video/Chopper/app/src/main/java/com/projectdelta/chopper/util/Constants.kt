package com.projectdelta.chopper.util

import androidx.annotation.Keep

@Keep
@Suppress("KotlinJniMissingFunction") // AS Sync issue :/
class Constants {

	companion object {

		const val CHOPPER_LIBS = "chopper"

		val coreJniVersion: Float
			get() = nativeGetJniVersion()

		val openCVVersion: String
			get() = nativeGetOpenCVVersion()

		@Keep
		@JvmStatic
		private external fun nativeGetJniVersion(): Float

		@Keep
		@JvmStatic
		private external fun nativeGetOpenCVVersion(): String

	}

}
