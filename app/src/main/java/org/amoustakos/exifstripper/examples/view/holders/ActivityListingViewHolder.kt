package org.amoustakos.exifstripper.examples.view.holders

import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.examples.view.models.ActivityListingModel
import org.amoustakos.exifstripper.view.holders.base.BaseViewHolder
import org.amoustakos.exifstripper.view.models.base.PublisherItem


class ActivityListingViewHolder(
        parent: ViewGroup,
        publishers: List<PublisherItem<ActivityListingModel>>
): BaseViewHolder<ActivityListingModel>(
        makeView(parent, R.layout.list_item_activity_card),
        publishers
) {
    @field:BindView(R.id.tv_activity_name)
    lateinit var name: TextView
    @field:BindView(R.id.tv_activity_descr)
    lateinit var descr: TextView

    var item: ActivityListingModel? = null

    init {
        initViews()
    }

    private fun initViews() {
        ButterKnife.bind(this, itemView)
    }



    override fun loadItem(item: ActivityListingModel) {
        super.loadItem(item)

        name.text = item.name
        descr.text = item.description
    }
}