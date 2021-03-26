package org.amoustakos.exifstripper.screens.privacy

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_privacy_terms.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.rx.disposer.disposeBy
import org.amoustakos.exifstripper.utils.rx.disposer.onDestroy
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar
import timber.log.Timber


class PrivacyTermsActivity : BaseActivity() {

	private lateinit var title: String
	private var content: String? = null

	private val toolbar = BasicToolbar(R.id.toolbar)


	override fun layoutId() = R.layout.activity_privacy_terms

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		title = intent.getStringExtra(KEY_TITLE) ?: run { finish(); return }
		setupToolbar()
		loadContent()
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.toggleBackButton(true)
		toolbar.setTitle(title)
	}

	override fun onSupportNavigateUp(): Boolean {
		onBackPressed()
		return true
	}


	private fun loadContent() {
		if (content != null) {
			setLabelContent()
			return
		}

		val contentLocation = intent.getStringExtra(KEY_CONTENT)

		Single.fromCallable { }
				.observeOn(Schedulers.io())
				.doOnSuccess {
					content = FileUtils.readStream(FileUtils.readAsset(this, contentLocation!!))
				}
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSuccess {
					setLabelContent()
				}
				.disposeBy(onDestroy)
				.doOnError(Timber::e)
				.onErrorReturn { }
				.subscribe()
	}

	private fun setLabelContent() {
		tv_content.text = fromHtml(content!!)
		tv_content.movementMethod = LinkMovementMethod.getInstance()
	}


	companion object {

		private const val KEY_TITLE = "key_title"
		private const val KEY_CONTENT = "key_content"

		private const val PRIVACY_POLICY = "privacy.html"
		private const val TERMS = "toc.html"


		fun getTermsIntent(ctx: Context): Intent {
			return Intent(ctx, PrivacyTermsActivity::class.java)
					.putExtra(KEY_TITLE, ctx.getString(R.string.title_terms))
					.putExtra(KEY_CONTENT, TERMS)
		}

		fun getPrivacyIntent(ctx: Context): Intent {
			return Intent(ctx, PrivacyTermsActivity::class.java)
					.putExtra(KEY_TITLE, ctx.getString(R.string.title_privacy_policy))
					.putExtra(KEY_CONTENT, PRIVACY_POLICY)
		}

		@Suppress("DEPRECATION")
		private fun fromHtml(source: String): Spanned {
			return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
			else
				Html.fromHtml(source)
		}
	}
}
