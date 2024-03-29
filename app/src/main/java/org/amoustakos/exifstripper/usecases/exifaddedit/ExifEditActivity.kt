package org.amoustakos.exifstripper.usecases.exifaddedit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.databinding.ActivityExifEditBinding
import org.amoustakos.exifstripper.io.model.ExifAttribute
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.home.MainActivity
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.ads.AdUtility
import org.amoustakos.exifstripper.utils.privacy.GdprUtil
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar

class ExifEditActivity : BaseActivity() {


	private lateinit var binding: ActivityExifEditBinding
	private lateinit var toolbar: BasicToolbar
	private lateinit var viewModel: ExifEditViewModel

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.activity_exif_edit

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityExifEditBinding.inflate(LayoutInflater.from(this))
		setContentView(binding.root)
		toolbar = BasicToolbar(binding.toolbar.toolbar)

		binding.btnSaveAttribute.setOnClickListener {
			setResult(true)
			finish()
		}

		viewModel = ViewModelProvider(
				this,
				SavedStateViewModelFactory(application(), this)
		).get(ExifEditViewModel::class.java)

		setupToolbar()
		setupAds()

		loadData()

		binding.attrField.etValue.doOnTextChanged { text, _, _, _ ->
			viewModel.exifAttribute.value?.value = text?.toString()
		}
	}

	private fun loadData() {
		val attr = intent?.let{ getAttributeFromIntent(it) } ?: run {
			finish()
			return
		}

		viewModel.exifAttribute.value = attr

		binding.attrField.tvTitle.text = attr.key
		binding.attrField.etValue.setText(attr.value)
	}

	private fun setResult(save: Boolean) {
		if (save) {
			val data = Intent().putExtra(EXTRA_KEY_ATTRIBUTE, viewModel.exifAttribute.value)
			setResult(Activity.RESULT_OK, data)
		} else {
			setResult(Activity.RESULT_CANCELED)
		}
	}


	private fun setupAds() {
		if (!GdprUtil.hasAcceptedTerms(this)) {
			showPrivacySplash()
			Do safe { FirebaseAnalytics.getInstance(this).logEvent("privacy_terms_not_accepted", null) }
			return
		} else {
			Do safe {
				AdUtility.inflateFooterAdView(binding.flAdFooter)
			}
		}
	}

	override fun onResume() {
		super.onResume()
		AdUtility.onFooterResume(this)
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.toggleBackButton(true)
		toolbar.showHome(true)
		toolbar.setTitle(R.string.title_activity_exif_edit)
		toolbar.setAsActionbar(this)
	}

	override fun onBackPressed() {
		super.onBackPressed()
		val intent = MainActivity.getReturnIntent(this) //TODO
		startActivity(intent)
		finish()
	}


	companion object {
		const val EXTRA_KEY_ATTRIBUTE = "extra_key_attribute"

		fun getStartIntent(ctx: Context, attr: ExifAttribute) = Intent(ctx, ExifEditActivity::class.java).apply {
			putExtra(EXTRA_KEY_ATTRIBUTE, attr)
		}

		fun getAttributeFromIntent(intent: Intent): ExifAttribute? {
			return intent.extras?.getParcelable(EXTRA_KEY_ATTRIBUTE)
		}
	}


}