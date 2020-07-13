package org.amoustakos.exifstripper.usecases.exifremoval.adapters

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.row_vp_image.view.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.view.recycler.BaseViewHolder
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import timber.log.Timber

class ExifImageViewHolder(
		parent: ViewGroup,
		publishers: List<PublisherItem<ExifImageViewData>>
) : BaseViewHolder<ExifImageViewData>(
		makeView(parent, R.layout.row_vp_image),
		publishers
) {

	override fun loadItem(item: ExifImageViewData) {
		super.loadItem(item)
		loadExifAttribute()
	}

	private fun loadExifAttribute() {
		Do.safe({
			Glide
					.with(itemView.context)
					.load(mItem?.path)
					.downsample(DownsampleStrategy.AT_MOST)
					.override(1250)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.dontAnimate()
					.into(itemView.image)
		}, {
			Timber.e(it)
			Crashlytics.logException(it)
		})
	}
}