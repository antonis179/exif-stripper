package org.amoustakos.exifstripper.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import io.reactivex.subjects.PublishSubject
import org.amoustakos.exifstripper.io.ResponseWrapper
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.file.schemehandlers.SchemeHandler
import org.amoustakos.exifstripper.io.file.schemehandlers.SchemeHandlerFactory
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.utils.exif.Attributes
import org.amoustakos.exifstripper.utils.exif.ExifUtil
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference

class ExifFile(
		context: Context
) {

	companion object {
		private const val CACHE_FOLDER = "/exif_cache/"
		private const val IMAGE_PROVIDER = "imageprovider"

		fun getCacheDirectory(context: Context) = "${context.externalCacheDir!!}$CACHE_FOLDER"

		fun getCacheFilePath(context: Context, name: String) = "${getCacheDirectory(context)}$name"

		fun clearCache(context: Context) {
			FileUtils.recursivelyDelete(File(getCacheDirectory(context)))
		}

		fun clearFile(context: Context, name: String) {
			val file = File(getCacheFilePath(context, name))
			if (file.exists() && file.isFile)
				file.delete()
		}
	}

	private val context: WeakReference<Context> = WeakReference(context)
	private var file: File? = null

	private var handler: SchemeHandler? = null

	private lateinit var exifAttributes: List<ExifAttributeViewData>
	val exifAttributesSubject: PublishSubject<List<ExifAttributeViewData>> = PublishSubject.create()

	private var name: String? = null
	var isLoaded: Boolean = false
		private set

	// =========================================================================================
	// Initialization
	// =========================================================================================

	sealed class LoadResult {
		object ContextError : LoadResult()
		object UriError : LoadResult()
		object FormatError : LoadResult()
		object CacheError : LoadResult()

		object Success : LoadResult()
	}

	fun load(uri: Uri): ResponseWrapper<LoadResult> {
		if (context.get() == null) {
			reset()
			return ResponseWrapper(LoadResult.ContextError)
		}

		if (!SchemeHandlerFactory(context.get()!!).isSupported(uri.toString())) {
			reset()
			return ResponseWrapper(LoadResult.UriError)
		}

		handler = SchemeHandlerFactory(context.get()!!)[uri.toString()]
		name = handler!!.getName()

		if (!ContentType.Image.JPEG.checkExtension(handler!!.getFileExtension())) {
			reset()
			return ResponseWrapper(LoadResult.FormatError)
		}

		val cachePath = getCacheFilePath(context.get()!!, name!!)

		clearFile(context.get()!!, cachePath)

		file = store(cachePath)	?: return ResponseWrapper(LoadResult.CacheError)
		handler = SchemeHandlerFactory(context.get()!!)[file!!.absolutePath]
		isLoaded = true

		loadExifAttributes()

		return ResponseWrapper(LoadResult.Success)
	}

	fun reset() {
		name = null
		isLoaded = false
		file = null
		handler = null
	}

	// =========================================================================================
	// Functionality
	// =========================================================================================

	fun getPath() = handler?.getPath()
	fun getMime() = handler?.getContentType()
	fun getName() = handler?.getName()


	fun loadExifAttributes() {
		getPath()?.let {
			try {
				val attrMap = ExifUtil.getAttributes(it, Attributes())
				exifAttributes = attrMap.map { entry ->
					ExifAttributeViewData(entry.key, entry.value)
				}
				updateSubscribers()
			} catch (e: Exception) {
				Timber.e(e)
			}
		}
	}

	fun removeExifData() {
		getPath()?.let {
			try {
				ExifUtil.removeAttributes(it, Attributes())
				exifAttributes = listOf()
				loadExifAttributes()
			} catch (e: Exception) {
				Timber.e(e)
			}
		}
	}

	fun removeAttribute(attribute: String) {
		getPath()?.let {
			try {
				ExifUtil.removeAttribute(it, attribute)
				exifAttributes = listOf()
				loadExifAttributes()
			} catch (e: Exception) {
				Timber.e(e)
			}
		}
	}

	private fun updateSubscribers() {
		exifAttributesSubject.onNext(exifAttributes)
	}

	fun shareImage(title: String) {
		val ctx = context.get() ?: throw NullPointerException("Cannot share image with null context")
		val mime = getMime() ?: throw NullPointerException("Issue retrieving mime type")
		val uri = FileProvider.getUriForFile(
				ctx,
				"${ctx.applicationContext.packageName}.$IMAGE_PROVIDER",
				file!!
		)
		val intent = FileUtils.shareFileIntent(uri, mime, title)
		ctx.startActivity(intent)
	}

	fun saveImageIntent(): Intent {
		val ctx = context.get() ?: throw NullPointerException("Cannot save image with null context")
		val mime = getMime() ?: throw NullPointerException("Issue retrieving mime type")
		val uri = FileProvider.getUriForFile(
				ctx,
				"${ctx.applicationContext.packageName}.$IMAGE_PROVIDER",
				file!!
		)
		return FileUtils.saveFileIntent(uri, mime, name)
	}

	fun store(destination: String): File? {
		return FileUtils.createFile(
				handler?.getInputStream() ?: return null,
				destination
		)
	}

	fun writeUriToFile(uri: Uri) {
		FileUtils.writeUriToFIle(uri, file!!, context.get()!!)
	}
}