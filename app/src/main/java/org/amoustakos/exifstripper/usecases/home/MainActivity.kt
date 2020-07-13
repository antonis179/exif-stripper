package org.amoustakos.exifstripper.usecases.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.appodeal.ads.Appodeal
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.donations.DonationsActivity
import org.amoustakos.exifstripper.usecases.exifremoval.ImageHandlingFragment
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import org.amoustakos.exifstripper.usecases.settings.SettingsActivity
import org.amoustakos.exifstripper.utils.Do


class MainActivity : BaseActivity() {

	private var isDoubleBackToExitPressedOnce = false
	private var drawerToggle: ActionBarDrawerToggle? = null

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setupAds()

		selectFragment(savedInstanceState)
	}

	private fun setupAds() {
		if (!GdprUtil.hasAcceptedTerms(this)) {
			showPrivacySplash()
			Do safe { FirebaseAnalytics.getInstance(this).logEvent("privacy_terms_not_accepted", null) }
			return
		} else {
			Do safe {
				Appodeal.initialize(
						this,
						getString(R.string.appodeal_app_key),
						Appodeal.BANNER_VIEW,
						true
				)
				Appodeal.setBannerViewId(R.id.adFooterBanner)
				Appodeal.show(this, Appodeal.BANNER_VIEW)
			}
		}
	}

	override fun onResume() {
		super.onResume()
		Appodeal.onResume(this, Appodeal.BANNER_VIEW)
	}

	override fun onResumeFragments() {
		super.onResumeFragments()
		setupNavView()
	}

	// =========================================================================================
	// Navigation
	// =========================================================================================

	private fun setupNavView() {
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		drawerToggle = ActionBarDrawerToggle(
				this,
				navDrawer,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close
		)

		navView.setNavigationItemSelectedListener { item ->
			when (item.itemId) {

				R.id.nav_donations -> {
					startActivity(Intent(this, DonationsActivity::class.java))
					if (isDrawerOpen()) closeDrawer()
					false
				}

				R.id.nav_settings -> {
					startActivity(Intent(this, SettingsActivity::class.java))
					if (isDrawerOpen()) closeDrawer()
					false
				}

				else -> true
			}
		}

		drawerToggle?.isDrawerIndicatorEnabled = true
		navDrawer.postDelayed({ drawerToggle?.syncState() }, 250 )
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		drawerToggle?.onConfigurationChanged(newConfig)
	}

	private fun loadFragment(fragment: Fragment, tag: String?) {
		val fragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.frag_container, fragment, tag)
		fragmentTransaction.commit()
	}

	override fun onBackPressed() {
		if (isDrawerOpen()) {
			closeDrawer()
			return
		}
		if (isDoubleBackToExitPressedOnce) {
			super.onBackPressed()
			return
		}
		isDoubleBackToExitPressedOnce = true
		Toast.makeText(this, getString(R.string.toast_back_to_exit), Toast.LENGTH_SHORT).show()
		Handler().postDelayed({ isDoubleBackToExitPressedOnce = false }, 2000)
	}

	private fun closeDrawer() {
		navDrawer.closeDrawer(GravityCompat.START)
	}

	private fun openDrawer() {
		navDrawer.openDrawer(GravityCompat.START)
	}

	private fun isDrawerOpen() = navDrawer.isDrawerOpen(GravityCompat.START)

	// =========================================================================================
	// Menu
	// =========================================================================================

	private fun selectFragment(savedInstanceState: Bundle?) {

		if (savedInstanceState == null) {
			val uris: ArrayList<Uri>? = when (intent?.action) {
				Intent.ACTION_SEND -> {
					if (intent.type?.startsWith(ContentType.Image.TYPE) == true) {
						val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
						uri?.let { arrayListOf(it) }
					} else null
				}
				Intent.ACTION_SEND_MULTIPLE -> {
					if (intent.type?.startsWith(ContentType.Image.TYPE) == true) {
						intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
					} else null
				}
				else -> null
			}

			loadFragment(ImageHandlingFragment.newInstance(uris), TAG_IMAGE_SELECTION)
		}

	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.toolbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.toolbar_policy -> showPrivacyPolicy()
			R.id.toolbar_toc    -> showTerms()
			R.id.toolbar_exit   -> finish()
			android.R.id.home -> openDrawer()
			else                -> return super.onOptionsItemSelected(item)
		}

		return true
	}


	companion object {
		const val TAG_IMAGE_SELECTION = "tag_image_selection"

		fun getIntent(ctx: Context): Intent {
			return Intent(ctx, MainActivity::class.java)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}

		fun getReturnIntent(ctx: Context): Intent {
			return Intent(ctx, MainActivity::class.java)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		}
	}
}
