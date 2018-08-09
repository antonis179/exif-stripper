package org.amoustakos.exifstripper.view.adapters.base

import android.view.ViewGroup
import org.amoustakos.exifstripper.view.holders.base.BaseViewHolder
import org.amoustakos.exifstripper.view.models.base.BaseViewModel
import org.amoustakos.exifstripper.view.models.base.PublisherItem

abstract class RecyclerAdapter<Holder: BaseViewHolder<Model>, Model: BaseViewModel<Holder, Model>>(
    mItems: MutableList<Model>,
    publishers: List<PublisherItem<Model>>
): BaseRecyclerAdapter<Holder, Model>(mItems, publishers) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            makeHolder(parent, publishers)



    @Throws(NullPointerException::class)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position) ?: throw NullPointerException("View model was null")
        holder.loadItem(item)
    }




    abstract fun makeHolder(parent: ViewGroup, publishers: List<PublisherItem<Model>>): Holder

}