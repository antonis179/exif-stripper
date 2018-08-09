package org.amoustakos.exifstripper.view.adapters.base

import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import org.amoustakos.exifstripper.view.holders.base.BaseViewHolder
import org.amoustakos.exifstripper.view.models.base.BaseViewModel
import org.amoustakos.exifstripper.view.models.base.PublisherItem


abstract class BaseRecyclerAdapter<Holder : BaseViewHolder<Model>, Model: BaseViewModel<Holder, Model>>(
        @NonNull private var mItems: MutableList<Model>,
        val publishers: List<PublisherItem<Model>>
): RecyclerView.Adapter<Holder>() {

    private val mLock = Any()


    fun getItem(position: Int): Model? = mItems[position]

    override fun getItemCount(): Int = mItems.size


    fun clean() = synchronized(mLock) {
        mItems.clear()
    }

    fun addAll(items: Collection<Model>) = synchronized(mLock) {
        mItems = ArrayList(items)
    }

    fun addAll(vararg items: Model) = synchronized(mLock) {
        items.forEach { mItems.add(it) }
    }

    fun addAll(items: List<Model>) = synchronized(mLock) {
        mItems = ArrayList(items)
    }

    fun add(item: Model) = synchronized(mLock) {
        mItems.add(item)
    }


    fun replace(items: Collection<Model>) = replace(ArrayList(items))
    fun replace(items: List<Model>) {
        clean()
        addAll(items)
    }


}