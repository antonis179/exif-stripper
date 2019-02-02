package org.amoustakos.exifstripper.usecases.home

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity

class MainActivity : BaseActivity() {


	private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
		return@OnNavigationItemSelectedListener when (item.itemId) {
			R.id.navigation_home -> {
				message.setText(R.string.title_home)
				true
			}
			R.id.navigation_dashboard -> {
				message.setText(R.string.title_dashboard)
				true
			}
			R.id.navigation_notifications -> {
				message.setText(R.string.title_notifications)
				true
			}
			else -> false
		}
	}

	override fun layoutId() = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
	}
}
