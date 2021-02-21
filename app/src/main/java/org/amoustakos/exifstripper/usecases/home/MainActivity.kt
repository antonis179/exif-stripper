package org.amoustakos.exifstripper.usecases.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import org.amoustakos.exifstripper.utils.ads.AdLoadedListener
import org.amoustakos.exifstripper.utils.ads.AdUtility


class MainActivity : BaseActivity() {

	private var isDoubleBackToExitPressedOnce = false

	private val adLoadedListener = object : AdLoadedListener {
		override fun onAdLoaded() {
			Do safe {
				val frag = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT)
				if (frag != null && frag is AdLoadedListener) frag.onAdLoaded()
			}
		}
	}

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setupBottomNav()
		setupAds()

		selectFragment(savedInstanceState)
	}

	@SuppressLint("MissingPermission")
	private fun setupAds() {
		if (!GdprUtil.hasAcceptedTerms(this)) {
			showPrivacySplash()
			Do safe { FirebaseAnalytics.getInstance(this).logEvent("privacy_terms_not_accepted", null) }
			return
		} else {
			Do safeLogged {
				AdUtility.registerCallback(adLoadedListener)
				AdUtility.inflateFooterAdView(flAdFooter)
			}
		}
	}

	override fun onResume() {
		super.onResume()
		Do safeLogged  { AdUtility.onFooterResume(this) }
	}

	override fun onDestroy() {
		Do safeLogged  { AdUtility.unRegisterCallback(adLoadedListener) }
		super.onDestroy()
	}

	// =========================================================================================
	// Navigation
	// =========================================================================================

	private fun setupBottomNav() {
		navBottom.selectedItemId = -1
		navBottom.setOnNavigationItemSelectedListener { item ->
			when(item.itemId) {
				R.id.nav_donations -> {
					startActivity(Intent(this, DonationsActivity::class.java))
					true
				}

				R.id.nav_settings -> {
					startActivity(Intent(this, SettingsActivity::class.java))
					true
				}

				else -> false
			}
		}
	}

	private fun loadFragment(fragment: Fragment, tag: String?) {
		val fragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.frag_container, fragment, tag)
		fragmentTransaction.commit()
	}

	override fun onBackPressed() {
		if (isDoubleBackToExitPressedOnce) {
			super.onBackPressed()
			return
		}
		isDoubleBackToExitPressedOnce = true
		Toast.makeText(this, getString(R.string.toast_back_to_exit), Toast.LENGTH_SHORT).show()
		Handler(Looper.getMainLooper()).postDelayed({ isDoubleBackToExitPressedOnce = false }, 2000)
	}

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

			loadFragment(ImageHandlingFragment.newInstance(uris), TAG_FRAGMENT)
		}

	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.toolbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.toolbar_policy -> showPrivacyPolicy()
			R.id.toolbar_toc -> showTerms()
			R.id.toolbar_exit -> finish()
			else -> return super.onOptionsItemSelected(item)
		}

		return true
	}


	companion object {
		const val TAG_FRAGMENT = "FRAGMENT_TAG"

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
