package org.amoustakos.exifstripper.io.file.schemehandlers

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
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
