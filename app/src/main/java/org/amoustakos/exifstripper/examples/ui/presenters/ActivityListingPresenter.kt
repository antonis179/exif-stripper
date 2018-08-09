package org.amoustakos.exifstripper.examples.ui.presenters

import android.content.pm.PackageManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.examples.ui.contracts.ActivityListingActions
import org.amoustakos.exifstripper.examples.ui.contracts.ActivityListingView
import org.amoustakos.exifstripper.examples.view.models.ActivityListingModel
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.ui.presenters.BasePresenter
import org.amoustakos.utils.android.PackageManagerUtils.definedActivities
import timber.log.Timber
import java.util.*


class ActivityListingPresenter(
	view: ActivityListingView,
	private val basePackage: String,
	private val pm: PackageManager
): BasePresenter<ActivityListingView>(view), ActivityListingActions {


	private val activities: List<Class<out BaseActivity>>
		get() = definedActivities(pm, basePackage)


	override fun init() {}

	override fun load() {
		addLifecycleDisposable(Observable.just {}
				.observeOn(Schedulers.computation())
				.map<List<ActivityListingModel>> { _ ->
					val models = ArrayList<ActivityListingModel>()
					val activities = excludeCurrent(activities)

					for (act in activities) {
						val model = ActivityListingModel(
                                act,
                                act.name,
                                act.superclass.name
                        )
						models.add(model)
					}

					Timber.i("Found " + activities.size + " classes")

					models
				}
				.doOnError { Timber.i(it) }
				.onErrorReturn { ArrayList() }
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext { mView.onItemsCollected(it) }
				.subscribe())
	}


	private fun excludeCurrent(classes: List<Class<out BaseActivity>>) =
			classes.filter{ item -> mView.javaClass != item }

}
