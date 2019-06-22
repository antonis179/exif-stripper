package org.amoustakos.exifstripper.view.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.amoustakos.exifstripper.view.recycler.Type.LONG_CLICK

abstract class BaseViewHolder<Model>(
		itemView: View,
		protected val publishers: List<PublisherItem<Model>>
) : ViewHolder(itemView) {

	protected var mItem: Model? = null


	open fun loadItem(item: Model) {
		mItem = item
	}

	protected open fun onClick() =
			publishers
					.filter {
						it.type == Type.CLICK
					}
					.forEach {
						it.publisher.onNext(ClickEvent(mItem!!))
					}

	protected open fun onLongClick(): Boolean {
		publishers
				.filter {
					it.type == LONG_CLICK
				}
				.forEach {
					it.publisher.onNext(ClickEvent(mItem!!))
				}
		return true
	}


	companion object {
		@JvmStatic
		fun makeView(parent: ViewGroup, viewType: Int, attach: Boolean) =
				LayoutInflater.from(parent.context).inflate(viewType, parent, attach)

		@JvmStatic
		fun makeView(parent: ViewGroup, viewType: Int) = makeView(parent, viewType, false)
	}

}