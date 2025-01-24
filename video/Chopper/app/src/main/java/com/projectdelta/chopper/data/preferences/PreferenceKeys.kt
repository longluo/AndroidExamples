package com.projectdelta.chopper.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {

	const val DATASTORE_NAME = "chopper_user_preferences"

	val USER_ID = stringPreferencesKey("user_id")
	val FIRST_LOGIN = booleanPreferencesKey("first_login")
	val USER_NAME = stringPreferencesKey("user_name")
	val FIRST_LOGIN_TIME = longPreferencesKey("first_login_time")
	val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
}
