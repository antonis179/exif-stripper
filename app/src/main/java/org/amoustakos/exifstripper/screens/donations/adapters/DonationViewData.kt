package org.amoustakos.exifstripper.screens.donations.adapters

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.ViewGroup
import org.amoustakos.exifstripper.view.recycler.BaseViewModel
import org.amoustakos.exifstripper.view.recycler.PublisherItem

class DonationViewData(
		val position: Int,
		val title: String,
		val description: String,
		val price: String
) : BaseViewModel<DonationViewHolder, DonationViewData>, Parcelable {
	override fun makeHolder(
			ctx: Context,
			view: ViewGroup,
			publishers: List<PublisherItem<DonationViewData>>
	) = DonationViewHolder(view, publishers)

	constructor(source: Parcel) : this(
			source.readInt(),
			source.readString()!!,
			source.readString()!!,
			source.readString()!!
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(position)
		writeString(title)
		writeString(description)
		writeString(price)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<DonationViewData> = object : Parcelable.Creator<DonationViewData> {
			override fun createFromParcel(source: Parcel): DonationViewData = DonationViewData(source)
			override fun newArray(size: Int): Array<DonationViewData?> = arrayOfNulls(size)
		}
	}
}