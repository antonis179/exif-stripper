package org.amoustakos.exifstripper.screens.exifremoval.adapters

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.ViewGroup
import org.amoustakos.exifstripper.view.recycler.BaseViewModel
import org.amoustakos.exifstripper.view.recycler.PublisherItem

data class ExifImageViewData(
		val path: String
) : BaseViewModel<ExifImageViewHolder, ExifImageViewData>, Parcelable {
	override fun makeHolder(
			ctx: Context,
			view: ViewGroup,
			publishers: List<PublisherItem<ExifImageViewData>>
	) = ExifImageViewHolder(view, publishers)

	constructor(source: Parcel) : this(
			source.readString()!!
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeString(path)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<ExifImageViewData> = object : Parcelable.Creator<ExifImageViewData> {
			override fun createFromParcel(source: Parcel): ExifImageViewData = ExifImageViewData(source)
			override fun newArray(size: Int): Array<ExifImageViewData?> = arrayOfNulls(size)
		}
	}
}