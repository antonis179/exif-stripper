package org.amoustakos.exifstripper.usecases.exifremoval.adapters

import android.view.ViewGroup
import android.widget.ImageView
import coil.load
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
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

	private val image: ImageView = itemView.findViewById(R.id.image)

	init {
		itemView.setOnClickListener { onClick() }
	}

	override fun loadItem(item: ExifImageViewData) {
		super.loadItem(item)
		loadImage()
	}

	private fun loadImage() {
		Do.safe({
			mItem?.path?.let { image.load(File(it)) }
		}, {
			AnalyticsUtil.logException(it)
		})
	}
}