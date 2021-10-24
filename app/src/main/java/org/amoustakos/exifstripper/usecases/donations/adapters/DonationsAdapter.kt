package org.amoustakos.exifstripper.usecases.donations.adapters

import android.view.ViewGroup
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.RecyclerAdapter

class DonationsAdapter(
		mItems: MutableList<DonationViewData>,
		publishers: List<PublisherItem<DonationViewData>> = ArrayList()
) : RecyclerAdapter<DonationViewHolder, DonationViewData>(mItems, publishers) {


	override fun makeHolder(
			parent: ViewGroup,
			publishers: List<PublisherItem<DonationViewData>>): DonationViewHolder {
		return DonationViewHolder(parent, publishers)
	}


}