package org.amoustakos.exifstripper.view.models.base

import android.content.Context
import android.view.ViewGroup
import io.reactivex.subjects.PublishSubject


interface BaseViewModel<out Holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, Model: BaseViewModel<Holder, Model>> {

    fun makeHolder(ctx: Context, view: ViewGroup, publishers: List<PublisherItem<Model>> ): Holder

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