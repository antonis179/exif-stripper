package org.amoustakos.exifstripper.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.databinding.FragmentSettingsBinding
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar

class SettingsFragment : BaseFragment() {

	private var _binding: FragmentSettingsBinding? = null
	private val binding get() = _binding!!

	private lateinit var toolbar: BasicToolbar


	override fun layoutId() = R.layout.fragment_settings

	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View {
		_binding = FragmentSettingsBinding.inflate(inflater, container, false)
		toolbar = BasicToolbar(binding.toolbar.toolbar)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
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
		binding.chkAutoSave.isChecked = SettingsUtil.getAutosave()
		binding.chkAutoSave.setOnCheckedChangeListener { _, isChecked -> SettingsUtil.setAutosave(isChecked) }
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