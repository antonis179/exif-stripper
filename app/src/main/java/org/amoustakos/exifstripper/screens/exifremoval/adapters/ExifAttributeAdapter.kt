package org.amoustakos.exifstripper.screens.exifremoval.adapters

import android.view.ViewGroup
import org.amoustakos.exifstripper.screens.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.RecyclerAdapterWithEmptySpace


class ExifAttributeAdapter(
		mItems: MutableList<ExifAttributeViewData>,
		publishers: List<PublisherItem<ExifAttributeViewData>> = ArrayList()
) : RecyclerAdapterWithEmptySpace<ExifAttributeViewHolder, ExifAttributeViewData>(mItems, publishers) {


	override fun makeHolder(
			parent: ViewGroup,
			publishers: List<PublisherItem<ExifAttributeViewData>>): ExifAttributeViewHolder {
		return ExifAttributeViewHolder(parent, publishers)
	}


}