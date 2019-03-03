package org.amoustakos.exifstripper.usecases.exifremoval.views

import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_exif_attribute.view.*
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
		itemView.setOnClickListener { onClick() }
		itemView.setOnLongClickListener { onLongClick() }
	}

	override fun loadItem(item: ExifAttributeViewData) {
		super.loadItem(item)
		loadExifAttribute()
	}

	private fun loadExifAttribute() {
		itemView.tv_title.text = mItem?.title
		itemView.tv_value.text = mItem?.value
	}

}