package org.amoustakos.exifstripper.usecases.exifremoval.adapters

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.view.recycler.BaseViewHolder
import org.amoustakos.exifstripper.view.recycler.ClickEvent
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.Type

class ExifAttributeViewHolder(
		parent: ViewGroup,
		publishers: List<PublisherItem<ExifAttributeViewData>>
) : BaseViewHolder<ExifAttributeViewData>(
		makeView(parent, R.layout.row_exif_attribute),
		publishers
) {

	private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
	private val tvValue: TextView = itemView.findViewById(R.id.tv_value)
	private val ivDelete: ImageView = itemView.findViewById(R.id.iv_delete)

	init {
		itemView.setOnClickListener { onClick() }
		ivDelete.setOnClickListener { onDelete() }
		itemView.setOnLongClickListener { onLongClick() }
	}

	private fun onDelete() =
			publishers
					.filter { it.id == DELETION_PUBLISHER_ID }
					.forEach { it.publisher.onNext(ClickEvent(mItem!!)) }

	override fun onClick() =
			publishers
					.filter {
						it.type == Type.CLICK && it.id != DELETION_PUBLISHER_ID
					}
					.forEach {
						it.publisher.onNext(ClickEvent(mItem!!))
					}

	override fun loadItem(item: ExifAttributeViewData) {
		super.loadItem(item)
		tvTitle.text = mItem?.title
		tvValue.text = mItem?.value
	}

	companion object {
		const val DELETION_PUBLISHER_ID = "delete_publisher"
		const val EDIT_PUBLISHER_ID = "edit_publisher"
	}

}