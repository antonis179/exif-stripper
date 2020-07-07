package org.amoustakos.exifstripper.usecases.settings

import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.home.MainActivity
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar


class SettingsActivity : BaseActivity() {

	private val toolbar = BasicToolbar(R.id.toolbar)

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.activity_settings

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupToolbar()

		setupAutoSave()
	}

	override fun onBackPressed() {
		super.onBackPressed()
		val intent = MainActivity.getReturnIntent(this)
		startActivity(intent)
		finish()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> onBackPressed()
			else              -> return super.onOptionsItemSelected(item)
		}
		return true
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.toggleBackButton(true)
		toolbar.showHome(true)
		toolbar.setTitle(R.string.title_activity_settings)
	}


	// =========================================================================================
	// Autosave
	// =========================================================================================

	private fun setupAutoSave() {
		chkAutoSave.isChecked = SettingsUtil.getAutosave()
		chkAutoSave.setOnCheckedChangeListener { _, isChecked -> SettingsUtil.setAutosave(isChecked) }
	}

//	private fun setupFormat() {
//		val formats = arrayOf(
//				getString(R.string.format_jpeg),
//				getString(R.string.format_webp)
//		)
//		val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formats)
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//		spinnerFormat.adapter = adapter
//
//		//Select saved value
//		SettingsUtil.getFormat()?.let {
//			spinnerFormat.setSelection(adapter.getPosition(it))
//		}
//
//		spinnerFormat.onItemSelectedListener = object : OnItemSelectedListener {
//			override fun onNothingSelected(parent: AdapterView<*>?) {}
//			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//				adapter.getItem(position)?.let { SettingsUtil.setFormat(it) }
//			}
//		}
//	}


}