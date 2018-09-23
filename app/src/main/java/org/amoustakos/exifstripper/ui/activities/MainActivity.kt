package org.amoustakos.exifstripper.ui.activities

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.base.BaseActivity
import org.amoustakos.exifstripper.view.navigation.BasicNavDrawer
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar
import org.amoustakos.models.messages.Event
import org.amoustakos.utils.android.ui.ViewUtil
import timber.log.Timber

class MainActivity : BaseActivity() {

	private var doubleBackToExitPressedOnce = false

	private val toolbar = BasicToolbar(R.id.toolbar)
	private var navDrawer: BasicNavDrawer? = null


	// =========================================================================================
	// Init
	// =========================================================================================

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityComponent().inject(this)

		setupViewComponent(toolbar)
		toolbar.setTitle(R.string.title_activity_main)
		toolbar.setAsActionbar(this)

		toolbar.get()?.let {
			navDrawer = BasicNavDrawer(R.id.navView, R.id.drawerLayout, it)
			navDrawer?.let { drawer -> setupViewComponent(drawer) }

			if(savedInstanceState == null)
				navDrawer?.selectItem(POSITION_PICKER)
		} ?: finish()

		initListeners()
	}

	override fun layoutId() = R.layout.activity_main

	private fun initListeners() {
		navDrawer?.getClicks()
				?.doOnSubscribe { addLifecycleDisposable(it) }
				?.doOnNext { event ->
					if (!event.hasError)
						event.item?.let{ onNavItemChanged(it) }
				}
				?.doOnError {
					Timber.e(it)
					Crashlytics.logException(it)
				}
				?.onErrorReturn { Event(true) }
				?.subscribe()
	}

	// =========================================================================================
	// Overrides
	// =========================================================================================

	override fun onBackPressed() {
		if (navDrawer?.isOpen(GravityCompat.START) == true) {
			navDrawer?.close(GravityCompat.START)
			return
		}

//        if (fragmentManager.popBackStackImmediate())
//            return

		if (doubleBackToExitPressedOnce) {
			super.onBackPressed()
			return
		}

		doubleBackToExitPressedOnce = true
		ViewUtil.showToast(this, getString(R.string.toast_back_to_exit), false)
		Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
	}


	// =========================================================================================
	// Menu
	// =========================================================================================

	private fun onNavItemChanged(item: MenuItem): Boolean {
		when(item.itemId) {
			R.id.nav_picker -> {
//				loadFragment(CoinListingFragment.newInstance())
			}

		}

		navDrawer?.close(GravityCompat.START)
		return true
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_settings -> true
			else -> super.onOptionsItemSelected(item)
		}
	}

	// =========================================================================================
	// Fragment handling
	// =========================================================================================

	private fun loadFragment(frag: Fragment) {
		val first = supportFragmentManager.backStackEntryCount == 0

		val fragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.flFragContainer, frag)

		if (!first)
			fragmentTransaction.addToBackStack(android.R.attr.fragment.toString())

		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		fragmentTransaction.commit()
	}



	companion object {
		const val POSITION_PICKER = 0
	}
}
