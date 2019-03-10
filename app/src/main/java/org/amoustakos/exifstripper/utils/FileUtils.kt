package org.amoustakos.exifstripper.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType.WILDCARD
import timber.log.Timber
import java.io.*
import java.util.*
import kotlin.experimental.and

object FileUtils {

	// =========================================================================================
	// Files
	// =========================================================================================

	fun getLastModifiedFileDate(filePath: String) = Date(File(filePath).lastModified()).toString()

	fun createFile(source: InputStream, destination: String): File? {
		val destinationFile = File(destination)
		val buf = ByteArray(1024)
		var len: Int
		var out: OutputStream? = null

		try {
			try {
				out = FileOutputStream(destinationFile)
				len = source.read(buf)
				while (len > 0) {
					out.write(buf, 0, len)
					len = source.read(buf)
				}
				out.flush()
				return destinationFile
			} catch (e: IOException) {
				Timber.e(e)
				return null
			} finally {
				source.close()
				out?.close()
			}
		} catch (e: IOException) {
			Timber.e(e)
			return null
		}

	}

	fun renameFile(imageFile: File, name: String): File? {
		try {
			val dir = imageFile.parentFile
			if (dir.exists()) {
				val from = File(dir, imageFile.name)
				val to = File(dir, name + getExtension(imageFile.path))
				if (from.exists()) {
					if (from.renameTo(to)) {
						return to
					}
				}
			}
		} catch (e: Exception) {
			Timber.e(e)
		}

		return null
	}

	fun getExtension(absolutePath: String): String? {
		val dot = ".".intern()

		if (!absolutePath.contains(dot))
			return null

		val index = absolutePath.lastIndexOf(dot) + 1

		return if (index >= 0 && index <= absolutePath.length)
			absolutePath.substring(index)
		else
			null
	}

	fun getExtensionFromMimeType(mimeType: String): String? {
		val mime = MimeTypeMap.getSingleton()
		return mime.getExtensionFromMimeType(mimeType)
	}

	fun getMimeTypeFromExtension(extension: String): String? {
		val mime = MimeTypeMap.getSingleton()
		return mime.getMimeTypeFromExtension(extension)
	}

	// =========================================================================================
	// URIs
	// =========================================================================================

	fun openFileIntent(uri: Uri, mime: String, title: String): Intent {
		val intent = Intent()
		intent.action = Intent.ACTION_VIEW
		intent.setDataAndType(uri, mime)
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

		return Intent.createChooser(intent, title)
	}

	fun shareFileIntent(uri: Uri, mime: String, title: String): Intent {
		val intent = Intent()
		intent.action = Intent.ACTION_SEND
		intent.type = mime
		intent.putExtra(Intent.EXTRA_STREAM, uri)
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

		return Intent.createChooser(intent, title)
	}

	fun createGetContentIntent(
			context: Context,
			type: String = WILDCARD,
			title: String
	): Intent {
		//Document picker intent
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = type
		//Allow multiple files to be selected
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
		// Only return URIs that can be opened with ContentResolver
		intent.addCategory(Intent.CATEGORY_OPENABLE)


		//Samsung file manager intent
		val sIntent = Intent("com.sec.android.app.myfiles.PICK_DATA")
		sIntent.type = type
		// Only return URIs that can be opened with ContentResolver
		sIntent.addCategory(Intent.CATEGORY_OPENABLE)
		//Allow multiple files to be selected
		sIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

		val chooserIntent: Intent
		if (context.packageManager.resolveActivity(sIntent, 0) != null) {
			//Device with Samsung file manager
			chooserIntent = Intent.createChooser(sIntent, title)
			//Combine the document picker intent so the user can choose
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intent))
		} else {
			chooserIntent = Intent.createChooser(intent, title)
		}

		return chooserIntent
	}

	fun getThumbnail(context: Context, uri: Uri, mimeType: String): Bitmap? {
		if (!isMediaUri(uri)) {
			Timber.w("You can only retrieve thumbnails for images and videos.")
			return null
		}

		var bm: Bitmap? = null
		val resolver = context.contentResolver
		var cursor: Cursor? = null
		try {
			cursor = resolver.query(
					uri,
					null,
					null,
					null,
					null
			)
			if (cursor != null && cursor.moveToFirst()) {
				val id = cursor.getInt(0)

				if (mimeType.contains("video")) {
					bm = MediaStore.Video.Thumbnails.getThumbnail(
							resolver,
							id.toLong(),
							MediaStore.Video.Thumbnails.MINI_KIND, null)
				} else if (mimeType.contains(ContentType.Image.TYPE_GENERIC)) {
					bm = MediaStore.Images.Thumbnails.getThumbnail(
							resolver,
							id.toLong(),
							MediaStore.Images.Thumbnails.MINI_KIND, null)
				}
			}
		} catch (e: Exception) {
			Timber.e(e)
		} finally {
			cursor?.close()
		}
		return bm
	}

	fun isMediaUri(uri: Uri) = "media".equals(uri.authority, ignoreCase = true)

	// =========================================================================================
	// Bitwise operations
	// =========================================================================================

	fun getMagicNumbers(inputStream: InputStream): String? {
		val bytesToRead = 8
		val magicBytes = ByteArray(bytesToRead)
		val bytesRead: Int

		try {
			bytesRead = inputStream.read(magicBytes, 0, bytesToRead)
			inputStream.close()
		} catch (e: IOException) {
			Timber.e(e)
			return null
		}

		if (bytesRead != bytesToRead) {
			Timber.e("Failed to read the first %s bytes", bytesToRead)
			return null
		}

		return bytesToHex(magicBytes)
	}

	fun bytesToHex(bytes: ByteArray): String {
		val hexArray = "0123456789ABCDEF".toCharArray()

		val hexChars = CharArray(bytes.size * 2)
		for (j in bytes.indices) {
			val v: Int = (bytes[j] and 0xFF.toByte()).toInt()
			hexChars[j * 2] = hexArray[v.ushr(4)]
			hexChars[j * 2 + 1] = hexArray[v and 0x0F]
		}
		return String(hexChars)
	}


}