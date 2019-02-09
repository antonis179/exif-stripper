package org.amoustakos.exifstripper.usecases.home

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jarsilio.android.privacypolicy.PrivacyPolicyBuilder
import kotlinx.android.synthetic.main.activity_main.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.exifremoval.ImageHandlingFragment
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar


class MainActivity : BaseActivity() {

	val homeFragment = ImageHandlingFragment()


	private val toolbar = BasicToolbar(R.id.toolbar)





	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setupViewComponent(toolbar)
		toolbar.setTitle(R.string.app_name)
		toolbar.setAsActionbar(this)

		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

		if (supportFragmentManager.fragments.size == 0) {
			val selId = navigation.selectedItemId
			mOnNavigationItemSelectedListener.onNavigationItemSelected(
					navigation.menu.findItem(selId)
			)
		}

		//TESTING
//		Attributes()
//		startActivityForResult(createGetContentIntent(this, ContentType.Image.TYPE_GENERIC, ""), 166)
	}


	// =========================================================================================
	// Navigation
	// =========================================================================================

	private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
		return@OnNavigationItemSelectedListener when (it.itemId) {
			R.id.navigation_home -> {
				loadFragment(homeFragment, null)
				true
			}
			R.id.navigation_dashboard -> {
				true
			}
			R.id.navigation_notifications -> {
				true
			}
			else -> false
		}
	}

	private fun loadFragment(fragment: Fragment, tag: String?) {
		val fragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.fl_container, fragment, tag)
		fragmentTransaction.commit()
	}


	// =========================================================================================
	// Menu
	// =========================================================================================

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
		PrivacyPolicyBuilder()
				.withIntro(getString(R.string.app_name), "Antonis Moustakos")
				.withUrl(Uri.parse("android.resource://$packageName/raw/privacy.md").toString())
				.withMeSection()
				.withGooglePlaySection()
				.withEmailSection("exif.stripper@gmail.com")
				.start(this)
	}

}
