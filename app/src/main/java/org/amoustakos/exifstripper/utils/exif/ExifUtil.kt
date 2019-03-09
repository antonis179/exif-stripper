package org.amoustakos.exifstripper.utils.exif

import androidx.exifinterface.media.ExifInterface

object ExifUtil {

	fun removeAttribute(filename: String, tag: String) {
		ExifInterface(filename).removeAttribute(tag)
	}

	fun removeAttributes(filename: String, tags: Collection<String>) {
		ExifInterface(filename).removeAttributes(tags)
	}

	fun ExifInterface.removeAttribute(tag: String) {
		setAttribute(tag, null)
		saveAttributes()
	}

	fun ExifInterface.removeAttributes(tags: Collection<String>) {
		tags.forEach { setAttribute(it, null) }
		saveAttributes()
	}

	fun getAttribute(filename: String, tag: String) = ExifInterface(filename).getAttribute(tag)
	fun ExifInterface.get(tag: String) = getAttribute(tag)

	fun getAttributes(filename: String, tags: Collection<String>): Map<String, String> {
		val attrMap = mutableMapOf<String, String>()
		val exif = ExifInterface(filename)

		tags.forEach { tag ->
			attrMap[tag] = exif.getAttribute(tag) ?: return@forEach
		}

		return attrMap
	}

}