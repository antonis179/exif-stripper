package org.amoustakos.exifstripper.io.file.schemehandlers

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import org.amoustakos.exifstripper.utils.FileUtils.getExtensionFromMimeType
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.ref.WeakReference

internal class ContentSchemeHandler : SchemeHandler {

    private lateinit var uri: Uri
    private lateinit var context: WeakReference<Context>

    private val uriNameFallback: String
        get() {
            val components = uri.toString().split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return components[components.size - 1]
        }

    override fun init(path: String, context: Context) {
        uri = Uri.parse(path)
        this.context = WeakReference(context)
    }

    override fun getLength(): Long = getUriSize()

    @Throws(FileNotFoundException::class)
    override fun getInputStream(): InputStream? {
        return context.get()?.contentResolver?.openInputStream(uri)
    }

    override fun getContentType(): String {
        val type = context.get()?.contentResolver?.getType(uri)

        if (type.isNullOrEmpty())
            return ContentType.APPLICATION_OCTET_STREAM

        return type
    }

    override fun getFileExtension() = context.get()?.contentResolver?.getType(uri)?.let {
        getExtensionFromMimeType(it)
    }

    override fun getName() = getUriName()

    private fun getUriSize(): Long {
        val cursor = context.get()
                ?.contentResolver
                ?.query(uri, null, null, null, null)
        if (cursor == null) {
            Timber.e("null cursor for $uri, returning size 0")
            return 0
        }
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val size = cursor.getLong(sizeIndex)
        cursor.close()
        return size
    }

    @SuppressLint("Recycle")
    private fun getUriName(): String {
        val cursor = context.get()
                ?.contentResolver
                ?.query(uri, null, null, null, null)
            ?: return uriNameFallback

        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        cursor.close()
        return name
    }


	override fun getPath(): String? {
		val context = context.get() ?: return null

		when {
			DocumentsContract.isDocumentUri(context, uri) -> {
				// LocalStorageProvider
				if (isLocalStorageDocument(uri))
					return DocumentsContract.getDocumentId(uri)

				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					val docId = DocumentsContract.getDocumentId(uri)
					val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
					val type = split[0]

					if ("primary".equals(type, ignoreCase = true))
						return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
				} else if (isDownloadsDocument(uri)) {
					val id = DocumentsContract.getDocumentId(uri)
					val contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), java.lang.Long.parseLong(id))

					return getDataColumn(contentUri, null, null)
				} else if (isMediaDocument(uri)) {
					val docId = DocumentsContract.getDocumentId(uri)
					val split = docId.split(":".intern().toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
					val type = split[0]

					val contentUri = when (type) {
						"image".intern() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
						"video".intern() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
						"audio".intern() -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
						else             -> return null
					}

					val selection = "_id=?".intern()
					val selectionArgs = arrayOf(split[1])

					return getDataColumn(contentUri, selection, selectionArgs)
				}
			}

			"content".equals(uri.scheme!!, ignoreCase = true) -> // Return the remote address
				return if (isGooglePhotosUri(uri))
					uri.path
				else
					getDataColumn(uri, null, null)

			"file".equals(uri.scheme!!, ignoreCase = true) -> return uri.path
		}

		return null
	}

	@SuppressLint("Recycle")
	private fun getDataColumn(
			uri: Uri,
			selection: String?,
            selectionArgs: Array<String>?
	): String? {
		val column = "_data".intern()
		val projection = arrayOf(column)

		val cursor = context.get()
				?.contentResolver
				?.query(uri, projection, selection, selectionArgs, null)
				?: return null

		val index = cursor.getColumnIndexOrThrow(column)
		cursor.moveToFirst()
		val item = cursor.getString(index)
		cursor.close()
		return item
	}


	/**
	 * TODO
	 */
	fun isLocalStorageDocument(@Suppress("UNUSED_PARAMETER") uri: Uri) = false //LocalStorageProvider.AUTHORITY == uri.authority

	fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority
	fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority
	fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority
	fun isGooglePhotosUri(uri: Uri) = uri.authority?.startsWith("com.google.android.apps.photos")
			?: false




    override fun hasReadPermission() = try {
        getName()
        true
    } catch (se: SecurityException) {
        Timber.e(se)
        false
    }


    companion object {
        const val SCHEME = "content://"
    }
}
