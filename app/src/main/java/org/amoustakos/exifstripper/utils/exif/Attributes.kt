package org.amoustakos.exifstripper.utils.exif

import androidx.exifinterface.media.ExifInterface
import timber.log.Timber
import java.lang.reflect.Modifier

object Attributes {

	operator fun invoke(): Set<String> = attributes

	private val attributes: MutableSet<String> = HashSet()

	init {
		// Get all fields that the concrete Android-Java implementation have and delete them
		val fields = ExifInterface::class.java.declaredFields
		for (field in fields) {
			if (Modifier.isPublic(field.modifiers) &&
					Modifier.isStatic(field.modifiers) &&
					Modifier.isFinal(field.modifiers)) {

				if (field.type == String::class.java) try {
					val name = field.get(String::class.java) as String
					attributes.add(name)
					Timber.v(name)
				} catch (e: IllegalAccessException) {
					Timber.e(e)
				}

			}
		}
	}

}