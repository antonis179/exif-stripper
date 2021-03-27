package org.amoustakos.exifstripper.screens.donations.adapters

import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

	private val btnBuy: Button = itemView.findViewById(R.id.btnBuy)
	private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
	private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

	init {
		btnBuy.setOnClickListener { onClick() }
	}

	override fun loadItem(item: DonationViewData) {
		super.loadItem(item)

		tvTitle.text = item.title
		tvDescription.text = item.description
		btnBuy.text = item.price
	}
}