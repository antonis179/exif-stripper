package org.amoustakos.exifstripper.examples.ui.contracts

import org.amoustakos.exifstripper.examples.view.models.ActivityListingModel
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractActions
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractView


interface ActivityListingView : BaseContractView {
	fun onItemsCollected(items: List<ActivityListingModel>)
}


interface ActivityListingActions : BaseContractActions {
	fun load()
}
