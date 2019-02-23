package org.amoustakos.exifstripper.utils.exif

import androidx.exifinterface.media.ExifInterface

object ExifUtil {


	fun removeExifProperty(filename: String, property: String) {
		val exifInterface = ExifInterface(filename)
		exifInterface.setAttribute(property, null)
	}


}