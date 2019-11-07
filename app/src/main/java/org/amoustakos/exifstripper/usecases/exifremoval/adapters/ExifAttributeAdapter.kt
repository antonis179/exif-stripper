package org.amoustakos.exifstripper.usecases.exifremoval.adapters

import android.view.ViewGroup
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.RecyclerAdapter


class ExifAttributeAdapter(
		mItems: MutableList<ExifAttributeViewData>,
		publishers: List<PublisherItem<ExifAttributeViewData>> = ArrayList()
) : RecyclerAdapter<ExifAttributeViewHolder, ExifAttributeViewData>(mItems, publishers) {


	override fun makeHolder(
			parent: ViewGroup,
			publishers: List<PublisherItem<ExifAttributeViewData>>): ExifAttributeViewHolder {
		return ExifAttributeViewHolder(parent, publishers)
	}


}