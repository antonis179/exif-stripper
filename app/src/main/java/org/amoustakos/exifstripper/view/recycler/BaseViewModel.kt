package org.amoustakos.exifstripper.view.recycler

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.reactivex.subjects.PublishSubject


interface BaseViewModel<out Holder : ViewHolder, Model : BaseViewModel<Holder, Model>> {

	fun makeHolder(ctx: Context, view: ViewGroup, publishers: List<PublisherItem<Model>>): Holder

}


data class PublisherItem<Model>(
		val publisher: PublishSubject<ClickEvent<Model>>,
		val type: Type
)

data class ClickEvent<Model>(
		val item: Model
)

enum class Type {
	CLICK, LONG_CLICK, ITEM_SELECTION
}