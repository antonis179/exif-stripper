package org.amoustakos.exifstripper.io.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExifAttribute(
		val key: String,
		var value: String?
) : Parcelable