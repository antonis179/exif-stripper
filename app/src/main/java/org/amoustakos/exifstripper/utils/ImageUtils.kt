package org.amoustakos.exifstripper.utils

import android.content.Context
import android.net.Uri
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType.Image.*
import org.amoustakos.exifstripper.utils.FileUtils.getMagicNumbers
import timber.log.Timber
import java.io.*

object ImageUtils {

	fun typeByMagicNumbers(inputStream: InputStream): ContentType.Image? {
		val magicNumbers = getMagicNumbers(inputStream) ?: return null

		return when {
			magicNumbers.startsWith("FFD8")                     -> JPEG
			magicNumbers.startsWith("89504E470D0A1A0A")         -> PNG
			magicNumbers.startsWith("424D")                     -> BMP
			magicNumbers.startsWith("474946383961")
					|| magicNumbers.startsWith("474946383761")  -> GIF
			magicNumbers.startsWith("49492A00")
					|| magicNumbers.startsWith("4D4D002A")      -> TIFF
			else                                                       -> null
		}
	}

	fun typeByMagicNumbers(uri: Uri, context: Context): ContentType.Image? {
		return try {
			val inputStream = context.contentResolver.openInputStream(uri) ?: return null
			val imageType = typeByMagicNumbers(inputStream)
			inputStream.close()
			imageType
		} catch (e: FileNotFoundException) {
			Timber.e(e)
			null
		} catch (e: IOException) {
			Timber.e(e)
			null
		}
	}

	fun typeByMagicNumbers(file: File): ContentType.Image? {
		return try {
			val inputStream = FileInputStream(file)
			typeByMagicNumbers(inputStream)
		} catch (e: FileNotFoundException) {
			Timber.e(e)
			null
		}
	}

	fun isImage(uri: Uri, context: Context) = typeByMagicNumbers(uri, context) != null

}