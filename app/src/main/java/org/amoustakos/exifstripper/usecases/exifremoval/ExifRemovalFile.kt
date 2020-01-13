package org.amoustakos.exifstripper.usecases.exifremoval

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.exif.ExifFile
import java.io.File

class ExifRemovalFile : ExifFile, Parcelable {

	constructor()
	constructor(parcel: Parcel) : super(parcel)

	@Throws
	fun saveToSigned(context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			val resolver = context.contentResolver
			val contentValues = ContentValues().apply {
				put(MediaStore.MediaColumns.DISPLAY_NAME, getName(context))
				put(MediaStore.MediaColumns.MIME_TYPE, getHandler(context).getContentType())
				put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$PATH_SIGNED")
			}

			val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!

			writeToUri(uri, context)
		} else {
			val extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)!!
			val dir = File("${extDir.absolutePath}/$PATH_SIGNED")
			val dest = "${dir.absolutePath}/${getName(context)}.${getHandler(context).getFileExtension()}"

			getHandler(context).getInputStream()?.let { FileUtils.createFile(it, dest) }

			MediaScannerConnection.scanFile(context, arrayOf(dest), null) { _, _ ->  }
		}
	}

	override fun describeContents() = 0

	override fun writeToParcel(parcel: Parcel, flags: Int) = super.writeToParcel(parcel, flags)

	companion object {
		const val PATH_SIGNED = "PhotoStripper/"

		@JvmField
		val CREATOR: Parcelable.Creator<ExifRemovalFile> = object : Parcelable.Creator<ExifRemovalFile> {
			override fun createFromParcel(source: Parcel): ExifRemovalFile = ExifRemovalFile(source)
			override fun newArray(size: Int): Array<ExifRemovalFile?> = arrayOfNulls(size)
		}
	}

}