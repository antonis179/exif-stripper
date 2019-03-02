package org.amoustakos.exifstripper.usecases.exifremoval.views

import android.view.ViewGroup
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.view.recycler.BaseViewHolder
import org.amoustakos.exifstripper.view.recycler.PublisherItem

class ExifAttributeViewHolder(
		parent: ViewGroup,
		publishers: List<PublisherItem<ExifAttributeViewData>>
) : BaseViewHolder<ExifAttributeViewData>(
		makeView(parent, R.layout.row_exif_attribute),
		publishers
) {

	init {

	}

	override fun loadItem(item: ExifAttributeViewData) {
		super.loadItem(item)
	}

}