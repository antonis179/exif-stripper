package org.amoustakos.exifstripper.screens.exifremoval.adapters

import android.view.ViewGroup
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.RecyclerAdapter

class ExifImagePagerAdapter(
		mItems: MutableList<ExifImageViewData>,
		publishers: List<PublisherItem<ExifImageViewData>> = ArrayList()
) : RecyclerAdapter<ExifImageViewHolder, ExifImageViewData>(mItems, publishers) {


	override fun makeHolder(
			parent: ViewGroup,
			publishers: List<PublisherItem<ExifImageViewData>>): ExifImageViewHolder {
		return ExifImageViewHolder(parent, publishers)
	}


}