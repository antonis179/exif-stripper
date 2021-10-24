package org.amoustakos.exifstripper.usecases.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.databinding.ActivityMainBinding
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.usecases.donations.DonationsFragment
import org.amoustakos.exifstripper.usecases.exifremoval.ImageHandlingFragment
import org.amoustakos.exifstripper.usecases.settings.SettingsFragment
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.ads.AdLoadedListener
import org.amoustakos.exifstripper.utils.ads.AdUtility
import org.amoustakos.exifstripper.utils.privacy.GdprUtil
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName


class MainActivity : BaseActivity() {

	private lateinit var binding: ActivityMainBinding

	private var isDoubleBackToExitPressedOnce = false

	private val adLoadedListener = object : AdLoadedListener {
		override fun onAdLoaded() {
			Do safe {
				val frag = supportFragmentManager.findFragmentByTag(TAG_HOME_FRAGMENT)
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
		binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
		setContentView(binding.root)

		setupBottomNav()
		setupAds()

		loadHomeFragment(savedInstanceState)
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
				AdUtility.inflateFooterAdView(binding.flAdFooter)
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
		binding.navBottom.selectedItemId = R.id.nav_home
		binding.navBottom.setOnNavigationItemSelectedListener { item ->
			when(item.itemId) {
				R.id.nav_home -> {
					loadHomeFragment(null)
					true
				}
				R.id.nav_donations -> {
					loadDonationsFragment()
					true
				}

				R.id.nav_settings -> {
					loadSettingsFragment()
					true
				}

				else -> false
			}
		}
	}

	override fun onBackPressed() {
		if (supportFragmentManager.backStackEntryCount > 1) {
			super.onBackPressed()
			binding.navBottom.selectedItemId = getSelectionId()
			return
		}

		if (isDoubleBackToExitPressedOnce) {
			finishAffinity()
			return
		}

		isDoubleBackToExitPressedOnce = true
		Toast.makeText(this, getString(R.string.toast_back_to_exit), Toast.LENGTH_SHORT).show()
		Handler(Looper.getMainLooper()).postDelayed({ isDoubleBackToExitPressedOnce = false }, 2000)
	}

	// =========================================================================================
	// Menu
	// =========================================================================================

	private fun replaceFragment(fragment: Fragment, tag: String?) {
		val backStateName = getFragmentName(fragment::class)

		if (getTopFragmentName() == backStateName) return //Fragment already at top.
		if (supportFragmentManager.popBackStackImmediate(backStateName, 0)) return //Try popping

		supportFragmentManager.beginTransaction().apply {
			replace(R.id.frag_container, fragment, tag)
			addToBackStack(backStateName)
			commit()
		}
	}

	private fun getSelectionId() = when(getTopFragmentName()) {
		getFragmentName(ImageHandlingFragment::class) -> R.id.nav_home
		getFragmentName(DonationsFragment::class) -> R.id.nav_donations
		getFragmentName(SettingsFragment::class) -> R.id.nav_settings
		else -> -1
	}

	private fun getTopFragmentName(): String? {
		if (supportFragmentManager.backStackEntryCount == 0) return null
		return supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount-1).name
	}

	private fun getFragmentName(clss: KClass<*>) = clss.jvmName

	private fun loadHomeFragment(savedInstanceState: Bundle?) {
		val uris: ArrayList<Uri>? = if (savedInstanceState == null) {
			 when (intent?.action) {
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
		} else null

		val loadedFragment = supportFragmentManager.findFragmentByTag(TAG_HOME_FRAGMENT) ?: ImageHandlingFragment.newInstance(uris)
		replaceFragment(loadedFragment, TAG_HOME_FRAGMENT)
	}

	private fun loadDonationsFragment() {
		val loadedFragment = supportFragmentManager.findFragmentByTag(TAG_DONATIONS_FRAGMENT) ?: DonationsFragment.newInstance()
		replaceFragment(loadedFragment, TAG_DONATIONS_FRAGMENT)
	}

	private fun loadSettingsFragment() {
		val loadedFragment = supportFragmentManager.findFragmentByTag(TAG_SETTINGS_FRAGMENT) ?: SettingsFragment.newInstance()
		replaceFragment(loadedFragment, TAG_SETTINGS_FRAGMENT)
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
		const val TAG_HOME_FRAGMENT = "FRAGMENT_HOME_TAG"
		const val TAG_DONATIONS_FRAGMENT = "FRAGMENT_DONATIONS_TAG"
		const val TAG_SETTINGS_FRAGMENT = "FRAGMENT_SETTINGS_TAG"

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
