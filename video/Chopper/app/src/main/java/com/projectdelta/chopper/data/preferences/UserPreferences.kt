package com.projectdelta.chopper.data.preferences

import android.os.Parcel
import android.os.Parcelable


/**
 * Data Class as an abstract layer to act as typed datastore and for ease
 */
data class UserPreferences(
	val userId: String,
	val firstLogin: Boolean,
	val userName: String,
	val firstLoginTime: Long,
	val biometricEnabled: Boolean

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString()!!,
		parcel.readByte() != 0.toByte(),
		parcel.readString()!!,
		parcel.readLong(),
		parcel.readByte() != 0.toByte()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(userId)
		parcel.writeByte(if (firstLogin) 1 else 0)
		parcel.writeString(userName)
		parcel.writeLong(firstLoginTime)
		parcel.writeByte(if (biometricEnabled) 1 else 0)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<UserPreferences> {
		override fun createFromParcel(parcel: Parcel): UserPreferences {
			return UserPreferences(parcel)
		}

		override fun newArray(size: Int): Array<UserPreferences?> {
			return arrayOfNulls(size)
		}
	}
}
