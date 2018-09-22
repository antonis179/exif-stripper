package org.amoustakos.exifstripper.examples.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.examples.ui.contracts.ActivityListingActions
import org.amoustakos.exifstripper.examples.ui.contracts.ActivityListingView
import org.amoustakos.exifstripper.examples.ui.presenters.ActivityListingPresenter
import org.amoustakos.exifstripper.examples.view.adapters.ActivityListingAdapter
import org.amoustakos.exifstripper.examples.view.models.ActivityListingModel
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar
import timber.log.Timber
import java.util.*

/**
 * Activity that lists all the activities in the same package and creates <br></br>
 * a [RecyclerView] that starts each one on click.
 */
class MainActivity : BaseActivity(), ActivityListingView {

	private var isDoubleBackToExitPressedOnce = false
	private var presenter: ActivityListingActions? = null

	private var adapter: ActivityListingAdapter? = null

	private val toolbar = BasicToolbar(R.id.toolbar)


	@LayoutRes override fun layoutId() = R.layout.activity_main


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityComponent().inject(this)

		setupViewComponent(toolbar)
		toolbar.setTitle(R.string.title_activity_main)
		toolbar.setAsActionbar(this)

		Timber.d("${intent.action} | ${intent.dataString}")

		if (presenter == null) {
			presenter = ActivityListingPresenter(
					this,
					packageName,
					packageManager)
			presenter!!.subscribeToLifecycle(lifecycle)
		}

		setupRecycler()
		presenter!!.load()
	}


	override fun onDestroy() {
		super.onDestroy()
		presenter?.unsubscribeFromLifecycle(lifecycle)
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
	}

	override fun onBackPressed() {
		if (isDoubleBackToExitPressedOnce) {
			super.onBackPressed()
			return
		}
		isDoubleBackToExitPressedOnce = true
		Toast.makeText(this, getString(R.string.toast_back_to_exit), Toast.LENGTH_SHORT).show()
		Handler().postDelayed({ isDoubleBackToExitPressedOnce = false }, 2000)
	}

	// =========================================================================================
	// View interactions
	// =========================================================================================

	override fun onItemsCollected(items: List<ActivityListingModel>) {
		refreshAdapter(items)
	}

	private fun refreshAdapter(items: List<ActivityListingModel>) {
		adapter!!.clean()
		adapter!!.addAll(items)
		adapter!!.notifyDataSetChanged()
	}

	// =========================================================================================
	// Setup
	// =========================================================================================

	private fun setupRecycler() {
		if (adapter == null)
			adapter = ActivityListingAdapter(ArrayList())
		rv_activity_pool.adapter = adapter
	}


	override fun onNavigationItemSelected(item: MenuItem) = false
}
