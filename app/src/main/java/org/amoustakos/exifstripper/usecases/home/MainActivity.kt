package org.amoustakos.exifstripper.usecases.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.appodeal.ads.Appodeal
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.exifremoval.ImageHandlingFragment
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import org.amoustakos.utils.android.kotlin.Do


class MainActivity : BaseActivity() {

	private var isDoubleBackToExitPressedOnce = false

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if (!GdprUtil.hasAcceptedTerms(this)) {
			showPrivacySplash()
			return
		} else {
			Do safe {
				Appodeal.initialize(
						this,
						getString(R.string.appodeal_app_key),
						Appodeal.BANNER_VIEW,
						GdprUtil.hasAcceptedTerms(this)
				)
				Appodeal.setBannerViewId(R.id.adFooterBanner)
				Appodeal.show(this, Appodeal.BANNER_VIEW)
			}
		}

		selectFragment(savedInstanceState)
	}

	override fun onResume() {
		super.onResume()
		Appodeal.onResume(this, Appodeal.BANNER_VIEW)
	}

	// =========================================================================================
	// Navigation
	// =========================================================================================

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
			val uris: List<Uri>? = when {

				intent?.action == Intent.ACTION_SEND -> {
					if (intent.type?.startsWith(ContentType.Image.TYPE) == true) {
						val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
						uri?.let { listOf(it) }
					} else null
				}

				intent?.action == Intent.ACTION_SEND_MULTIPLE -> {
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
	}
}
