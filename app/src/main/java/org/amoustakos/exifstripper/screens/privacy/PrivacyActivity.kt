package org.amoustakos.exifstripper.screens.privacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.databinding.ActivityPrivacyBinding
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.utils.privacy.GdprUtil
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar


class PrivacyActivity : BaseActivity() {

	private lateinit var binding: ActivityPrivacyBinding
	private lateinit var toolbar: BasicToolbar


	override fun layoutId() = R.layout.activity_privacy

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPrivacyBinding.inflate(LayoutInflater.from(this))
		setContentView(binding.root)
		toolbar = BasicToolbar(binding.toolbar.toolbar)

		setupToolbar()

		with(binding) {
			btnToc.setOnClickListener { showTerms() }
			btnPrivacy.setOnClickListener { showPrivacyPolicy() }
			btnAccept.setOnClickListener { acceptAndContinue() }
		}
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
