package org.amoustakos.exifstripper.screens.exifremoval.adapters

import android.view.ViewGroup
import coil.load
import kotlinx.android.synthetic.main.row_vp_image.view.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.screens.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.view.recycler.BaseViewHolder
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import java.io.File

class ExifImageViewHolder(
		parent: ViewGroup,
		publishers: List<PublisherItem<ExifImageViewData>>
) : BaseViewHolder<ExifImageViewData>(
		makeView(parent, R.layout.row_vp_image),
		publishers
) {

	init {
		itemView.setOnClickListener { onClick() }
	}

	override fun loadItem(item: ExifImageViewData) {
		super.loadItem(item)
		loadImage()
	}

	private fun loadImage() {
		Do.safe({
			mItem?.path?.let { itemView.image.load(File(it)) }
		}, {
			AnalyticsUtil.logException(it)
		})
	}
}