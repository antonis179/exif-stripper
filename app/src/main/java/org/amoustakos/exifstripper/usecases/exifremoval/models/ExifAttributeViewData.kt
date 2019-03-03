package org.amoustakos.exifstripper.usecases.exifremoval.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.ViewGroup
import org.amoustakos.exifstripper.usecases.exifremoval.views.ExifAttributeViewHolder
import org.amoustakos.exifstripper.view.recycler.BaseViewModel
import org.amoustakos.exifstripper.view.recycler.PublisherItem

data class ExifAttributeViewData(
		val title: String,
		val value: String
) : BaseViewModel<ExifAttributeViewHolder, ExifAttributeViewData>, Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readString()!!,
			parcel.readString()!!
	)

	override fun makeHolder(
			ctx: Context,
			view: ViewGroup,
			publishers: List<PublisherItem<ExifAttributeViewData>>
	) = ExifAttributeViewHolder(view, publishers)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(title)
		parcel.writeString(value)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ExifAttributeViewData> {
		override fun createFromParcel(parcel: Parcel): ExifAttributeViewData {
			return ExifAttributeViewData(parcel)
		}

		override fun newArray(size: Int): Array<ExifAttributeViewData?> {
			return arrayOfNulls(size)
		}
	}

}