package org.amoustakos.exifstripper.screens.exifremoval

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import org.amoustakos.exifstripper.screens.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.exif.ExifFile
import java.io.File
import java.io.IOException

class ExifRemovalFile : ExifFile, Parcelable {

	constructor()
	constructor(parcel: Parcel) : super(parcel)

	@Throws
	fun saveToStripped(context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			val resolver = context.contentResolver
			val contentValues = ContentValues().apply {
				put(MediaStore.MediaColumns.DISPLAY_NAME, getName(context))
				put(MediaStore.MediaColumns.MIME_TYPE, getHandler(context).getContentType())
				put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$PATH_STRIPPED")
			}

			val uri = getUriForQOrLog(resolver, contentValues, context)

			writeToUri(uri, context)
		} else {
			val extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)!!
			val dir = File("${extDir.absolutePath}/$PATH_STRIPPED")
			val dest = "${dir.absolutePath}/${getName(context)}.${getHandler(context).getFileExtension()}"

			getHandler(context).getInputStream()?.let { FileUtils.createFile(it, dest) }

			MediaScannerConnection.scanFile(context, arrayOf(dest), null) { _, _ ->  }
		}
	}

	@Throws
	private fun getUriForQOrLog(resolver: ContentResolver, contentValues: ContentValues, context: Context): Uri {
		val uri = Do.safe(
				{ resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) },
				{
					val exc = IOException("""
						name: ${getName(context)}
						mime: ${getHandler(context).getContentType()}
						path: ${Environment.DIRECTORY_PICTURES}/$PATH_STRIPPED
						fileExists: ${file?.exists()}
					""".trimIndent())
					AnalyticsUtil.logException(exc)
					null
				}
		)

		return uri!!
	}


	override fun describeContents() = 0

	override fun writeToParcel(parcel: Parcel, flags: Int) = super.writeToParcel(parcel, flags)

	companion object {
		const val PATH_STRIPPED = "PhotoStripper/"

		@JvmField
		val CREATOR: Parcelable.Creator<ExifRemovalFile> = object : Parcelable.Creator<ExifRemovalFile> {
			override fun createFromParcel(source: Parcel): ExifRemovalFile = ExifRemovalFile(source)
			override fun newArray(size: Int): Array<ExifRemovalFile?> = arrayOfNulls(size)
		}
	}

}