package org.amoustakos.exifstripper.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.StatFs
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.crashlytics.android.Crashlytics
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType.WILDCARD
import timber.log.Timber
import java.io.*
import kotlin.experimental.and


object FileUtils {

	// =========================================================================================
	// Files
	// =========================================================================================

	fun readAsset(ctx: Context, name: String): InputStream = ctx.assets.open(name)

	fun readStream(stream: InputStream): String {
		val reader = BufferedReader(InputStreamReader(stream))
		val builder = StringBuilder()
		var line: String? = reader.readLine()

		while (line != null) {
			builder.append(line)
			line = reader.readLine()
		}

		return builder.toString()
	}

	fun createFile(source: InputStream, destination: String): File? {
		val destinationFile = File(destination)
		val buf = ByteArray(1024)
		var len: Int
		var out: OutputStream? = null

		try {
			try {
				val dir = File(destination.substring(0, destination.lastIndexOf("/")))
				if (!dir.exists())
					dir.mkdirs()

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
				Crashlytics.logException(e)
			} finally {
				source.close()
				out?.close()
			}
		} catch (e: IOException) {
			Timber.e(e)
			Crashlytics.logException(e)
		}
		return null
	}

	fun writeUriToFIle(uri: Uri, file: File, context: Context) {
		val pfd = context.contentResolver.openFileDescriptor(uri, "w")
				?: throw NullPointerException("Could not open file descriptor")
		val fileOutputStream = FileOutputStream(pfd.fileDescriptor)
		fileOutputStream.write(file.readBytes())
		fileOutputStream.close()
		pfd.close()
	}

	fun renameFile(imageFile: File, name: String): File? {
		try {
			val dir = imageFile.parentFile
			if (dir?.exists() == true) {
				val from = File(dir, imageFile.name)
				val to = File(dir, name + getExtension(imageFile.path))
				if (from.exists() && from.renameTo(to))
					return to
			}
		} catch (e: Exception) {
			Timber.e(e)
		}
		return null
	}

	fun getExtension(absolutePath: String): String? {
		val dot = ".".intern()
		if (!absolutePath.contains(dot)) return null
		val index = absolutePath.lastIndexOf(dot) + 1
		return if (index >= 0 && index <= absolutePath.length) absolutePath.substring(index)
		else null
	}

	fun getExtensionFromMimeType(mimeType: String) = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
	fun getMimeTypeFromExtension(extension: String) = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)


	fun recursivelyDelete(file: File) {
		if (!file.exists()) return

		if (file.isFile)
			file.delete()
		else
			file.listFiles()?.forEach { recursivelyDelete(it) }
	}

	fun makeDirectory(path: String): Boolean {
		val directory = File(path)
		return if (!directory.exists())
			directory.mkdirs()
		else
			true
	}

	fun exists(path: String): Boolean = File(path).exists()

	fun availableSpace(path: String, makeDirectory: Boolean = false): Long {
		if (!exists(path)) {
			if (!makeDirectory) return 0L
			if (!makeDirectory(path)) return 0L
		}
		val stat = StatFs(path)
		val blockSize = stat.blockSizeLong
		val totalBlocks = stat.blockCountLong
		return totalBlocks * blockSize
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

	fun saveFileIntent(uri: Uri, mime: String, filename: String? = null) = Intent().apply {
		action = Intent.ACTION_CREATE_DOCUMENT
		type = mime
		addCategory(Intent.CATEGORY_OPENABLE)
		putExtra(Intent.EXTRA_STREAM, uri)
		filename?.let { putExtra(Intent.EXTRA_TITLE, it) }
		addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
	}

	fun createGetContentIntent(
			context: Context,
			type: String = WILDCARD,
			title: String,
			allowMultiple: Boolean = false
	): Intent {
		//Document picker intent
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = type
		//Allow multiple files to be selected
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
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

//	fun getThumbnail(context: Context, uri: Uri, mimeType: String): Bitmap? {
//		if (!isMediaUri(uri)) {
//			Timber.w("You can only retrieve thumbnails for images and videos.")
//			return null
//		}
//
//		var bm: Bitmap? = null
//		val resolver = context.contentResolver
//		var cursor: Cursor? = null
//		try {
//			cursor = resolver.query(
//					uri,
//					null,
//					null,
//					null,
//					null
//			)
//			if (cursor != null && cursor.moveToFirst()) {
//				val id = cursor.getInt(0)
//
//				if (mimeType.contains("video")) {
//					bm = MediaStore.Video.Thumbnails.getThumbnail(
//							resolver,
//							id.toLong(),
//							MediaStore.Video.Thumbnails.MINI_KIND, null)
//				} else if (mimeType.contains(ContentType.Image.TYPE_GENERIC)) {
//					bm = MediaStore.Images.Thumbnails.getThumbnail(
//							resolver,
//							id.toLong(),
//							MediaStore.Images.Thumbnails.MINI_KIND, null)
//				}
//			}
//		} catch (e: Exception) {
//			Timber.e(e)
//		} finally {
//			cursor?.close()
//		}
//		return bm
//	}
//
//	fun isMediaUri(uri: Uri) = "media".equals(uri.authority, ignoreCase = true)

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