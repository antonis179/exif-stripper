package org.amoustakos.exifstripper.view.recycler

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView.Adapter


abstract class BaseRecyclerAdapter<Holder : BaseViewHolder<Model>, Model : BaseViewModel<Holder, Model>>(
		@NonNull private var mItems: MutableList<Model>,
		val publishers: List<PublisherItem<Model>>
) : Adapter<Holder>() {

	private val mLock = Any()


	fun getItem(position: Int): Model? = mItems[position]

	override fun getItemCount(): Int = mItems.size


	fun clean() = synchronized(mLock) {
		mItems.clear()
	}

	fun add(items: Collection<Model>) = synchronized(mLock) {
		mItems.addAll(items)
	}

	fun add(vararg items: Model) = synchronized(mLock) {
		items.forEach { mItems.add(it) }
	}

	fun add(items: List<Model>) = synchronized(mLock) {
		mItems.addAll(items)
	}

	fun add(item: Model) = synchronized(mLock) {
		mItems.add(item)
	}


	fun replace(items: Collection<Model>) = replace(ArrayList(items))
	fun replace(items: List<Model>) {
		clean()
		add(items)
	}


}