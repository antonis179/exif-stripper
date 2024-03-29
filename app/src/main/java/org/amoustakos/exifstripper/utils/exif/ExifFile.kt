package org.amoustakos.exifstripper.utils.exif

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.FileProvider
import io.reactivex.subjects.PublishSubject
import org.amoustakos.exifstripper.io.ResponseWrapper
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.file.schemehandlers.SchemeHandlerFactory
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.exif.errors.InvalidFormatException
import timber.log.Timber
import java.io.File
import java.io.IOException

open class ExifFile() : Parcelable {

	companion object {
		private const val CACHE_FOLDER = "/exif_cache/"
		private const val IMAGE_PROVIDER = "imageprovider"

		private var storedCacheFolder: String? = null

		@Throws
		fun getCacheDirectory(context: Context): String {
			if (storedCacheFolder == null)  storedCacheFolder = FileUtils.findExternalCacheDir(context)

			return "${storedCacheFolder!!}$CACHE_FOLDER"
		}

		fun getCacheFilePath(context: Context, name: String) = "${getCacheDirectory(context)}$name"

		fun clearCache(context: Context) {
			FileUtils.recursivelyDelete(File(getCacheDirectory(context)))
		}

		fun clearFile(context: Context, name: String) {
			val file = File(getCacheFilePath(context, name))
			if (file.exists() && file.isFile)
				file.delete()
		}

		@JvmField
		val CREATOR = object : Parcelable.Creator<ExifFile> {
			override fun createFromParcel(parcel: Parcel): ExifFile {
				return ExifFile(parcel)
			}

			override fun newArray(size: Int): Array<ExifFile?> {
				return arrayOfNulls(size)
			}
		}

	}


	protected var file: File? = null
	private var exifAttributes: List<ExifAttributeData> = ArrayList()
	val exifAttributesSubject: PublishSubject<List<ExifAttributeData>> = PublishSubject.create()

	val isLoaded: Boolean
		get() = file != null

	constructor(parcel: Parcel) : this() {
		file = parcel.readString()?.let { File(it) }
		exifAttributes = parcel.createTypedArrayList(ExifAttributeData) ?: ArrayList()
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(file?.absolutePath)
		parcel.writeTypedList(exifAttributes)
	}

	override fun describeContents() = 0

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

	fun load(uri: Uri, context: Context): ResponseWrapper<LoadResult> {
		if (!SchemeHandlerFactory(context).isSupported(uri.toString())) {
			reset()
			return ResponseWrapper(LoadResult.UriError)
		}

		val handler = SchemeHandlerFactory(context)[uri.toString()]

		if (!ContentType.Image.isTypeOf(handler.getFileExtension(), ExifUtil.supportedReadFormats())) {
			reset()
			return ResponseWrapper(LoadResult.FormatError)
		}

		val cachePath = getCacheFilePath(context, handler.getName())

		Do.safe({
			clearFile(context, cachePath)
		}, {
			AnalyticsUtil.logException(it)
		})

		file =
				Do.safe({
					handler.getInputStream()?.let { FileUtils.createFile(it, cachePath) }
				}, {
					AnalyticsUtil.logException(it)
					null
				}) ?: return ResponseWrapper(LoadResult.CacheError)

		loadExifAttributes(context)

		return ResponseWrapper(LoadResult.Success)
	}

	protected fun getHandler(context: Context) = getHandler(context, file!!.absolutePath)
	protected fun getHandler(context: Context, path: String) = SchemeHandlerFactory(context)[path]

	fun reset() {
		file = null
	}

	// =========================================================================================
	// Functionality
	// =========================================================================================

	fun getPath(context: Context) = getHandler(context).getPath()
	fun getName(context: Context) = getHandler(context).getName()

	fun loadExifAttributes(context: Context) {
		getPath(context)?.let {
			try {
				val attrMap = ExifUtil.getAttributes(it, Attributes())
				exifAttributes = attrMap.map { entry ->
					ExifAttributeData(entry.key, entry.value)
				}
				updateSubscribers()
			} catch (e: Exception) {
				Timber.e(e)
			}
		}
	}

	@Throws(IOException::class, InvalidFormatException::class)
	fun removeExifData(context: Context) {
		validForWriteOrThrow(context)
		getPath(context)?.let {
			ExifUtil.removeAttributes(it, Attributes())
			exifAttributes = listOf()
			loadExifAttributes(context)
		}
	}

	@Throws(IOException::class, InvalidFormatException::class)
	fun removeAttribute(context: Context, attribute: String) {
		validForWriteOrThrow(context)
		getPath(context)?.let {
			ExifUtil.removeAttribute(it, attribute)
			exifAttributes = listOf()
			loadExifAttributes(context)
		}
	}

	fun getAttribute(context: Context, attribute: String): String? {
		return getPath(context)?.let {
			try {
				ExifUtil.getAttribute(it, attribute)
			} catch (e: Exception) {
				Timber.e(e)
				null
			}
		}
	}

	fun setAttribute(context: Context, attribute: String, value: String?) {
		validForWriteOrThrow(context)
		getPath(context)?.let {
			ExifUtil.setAttribute(it, attribute, value)
			exifAttributes = listOf()
			loadExifAttributes(context)
		}
	}

	@Throws(InvalidFormatException::class)
	private fun validForWriteOrThrow(context: Context) {
		if (!ContentType.Image.isTypeOf(getHandler(context).getFileExtension(), ExifUtil.supportedWriteFormats())) {
			throw InvalidFormatException()
		}
	}

	private fun updateSubscribers() {
		exifAttributesSubject.onNext(exifAttributes)
	}

	fun shareImage(title: String, context: Context) {
		val handler = getHandler(context)
		val mime = handler.getContentType()
		val uri = getUri(context)
		val intent = FileUtils.shareFileIntent(uri, mime, title)
		context.startActivity(intent)
	}

	fun getUri(context: Context): Uri {
		return FileProvider.getUriForFile(
				context,
				"${context.applicationContext.packageName}.$IMAGE_PROVIDER",
				file!!
		)
	}

	fun saveImageIntent(context: Context): Intent {
		val handler = getHandler(context)
		val mime = handler.getContentType()
		val uri = FileProvider.getUriForFile(
				context,
				"${context.applicationContext.packageName}.$IMAGE_PROVIDER",
				file!!
		)
		return FileUtils.saveFileIntent(uri, mime, handler.getName())
	}

	fun writeToUri(uri: Uri, context: Context) {
		FileUtils.writeUriToFIle(uri, file!!, context)
	}
}


data class ExifAttributeData(
		val title: String,
		val value: String
) : Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readString()!!,
			parcel.readString()!!
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(title)
		parcel.writeString(value)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ExifAttributeData> {
		override fun createFromParcel(parcel: Parcel): ExifAttributeData {
			return ExifAttributeData(parcel)
		}

		override fun newArray(size: Int): Array<ExifAttributeData?> {
			return arrayOfNulls(size)
		}
	}

}