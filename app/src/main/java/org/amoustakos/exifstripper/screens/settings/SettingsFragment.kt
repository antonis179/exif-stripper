package org.amoustakos.exifstripper.screens.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar

class SettingsFragment : BaseFragment() {

	private val toolbar = BasicToolbar(R.id.toolbar)

	override fun layoutId() = R.layout.fragment_settings

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupToolbar()
		setupAutoSave()
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.setTitle(R.string.app_name)
		toolbar.setAsActionbar(activity as AppCompatActivity)
	}


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


	companion object {
		fun newInstance(): SettingsFragment {
			return SettingsFragment()
		}
	}

}