package org.amoustakos.exifstripper.screens.privacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_privacy.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.utils.privacy.GdprUtil
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar


class PrivacyActivity : BaseActivity() {

	private val toolbar = BasicToolbar(R.id.toolbar)

	override fun layoutId() = R.layout.activity_privacy


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setupToolbar()

		btn_toc.setOnClickListener { showTerms() }
		btn_privacy.setOnClickListener { showPrivacyPolicy() }

		btn_accept.setOnClickListener { acceptAndContinue() }
	}


	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.setTitle(title)
	}

	private fun acceptAndContinue() {
		GdprUtil.acceptTerms(this)
		showMainActivity()
	}

	companion object {
		fun getIntent(ctx: Context) = Intent(ctx, PrivacyActivity::class.java)
	}

}