package org.amoustakos.exifstripper.usecases.donations.adapters

import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_donation_card.view.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.view.recycler.BaseViewHolder
import org.amoustakos.exifstripper.view.recycler.PublisherItem

class DonationViewHolder(
		parent: ViewGroup,
		publishers: List<PublisherItem<DonationViewData>>
) : BaseViewHolder<DonationViewData>(
		makeView(parent, R.layout.row_donation_card),
		publishers
) {

	init {
		itemView.btnBuy.setOnClickListener { onClick() }
	}

	override fun loadItem(item: DonationViewData) {
		super.loadItem(item)

		with(itemView) {
			tvTitle.text = item.title
			tvDescription.text = item.description
			btnBuy.text = item.price
		}
	}
}