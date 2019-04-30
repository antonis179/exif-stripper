package org.amoustakos.exifstripper.usecases.home

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.exifremoval.ImageHandlingFragment


class MainActivity : BaseActivity() {

	companion object {
		const val TAG_IMAGE_SELECTION = "tag_image_selection"
	}

	private var isDoubleBackToExitPressedOnce = false

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		selectFragment(savedInstanceState)
	}

	// =========================================================================================
	// Navigation
	// =========================================================================================

//	private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
//		return@OnNavigationItemSelectedListener when (it.itemId) {
//			R.id.navigation_home -> {
//				loadFragment(ImageHandlingFragment(), null)
//				true
//			}
//			R.id.navigation_dashboard -> {
//				true
//			}
//			R.id.navigation_notifications -> {
//				true
//			}
//			else -> false
//		}
//	}

	private fun loadFragment(fragment: Fragment, tag: String?) {
		val fragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.fl_container, fragment, tag)
		fragmentTransaction.commit()
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
	// Menu
	// =========================================================================================

	private fun selectFragment(savedInstanceState: Bundle?) {

		if (savedInstanceState == null) {
			loadFragment(ImageHandlingFragment(), TAG_IMAGE_SELECTION)
		}

		//Bottom nav
//		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//
//		if (supportFragmentManager.fragments.size == 0) {
//			val selId = navigation.selectedItemId
//			mOnNavigationItemSelectedListener.onNavigationItemSelected(
//					navigation.menu.findItem(selId)
//			)
//		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.toolbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.toolbar_policy -> showPrivacyPolicy()
			R.id.toolbar_exit   -> finish()
		}

		return true
	}

	private fun showPrivacyPolicy() {
//		PrivacyPolicyBuilder()
//				.withIntro(getString(R.string.app_name), "Antonis Moustakos")
//				.withUrl(Uri.parse("android.resource://$packageName/raw/privacy.md").toString())
//				.withMeSection()
//				.withGooglePlaySection()
//				.withEmailSection("exif.stripper@gmail.com")
//				.start(this)
	}

}
