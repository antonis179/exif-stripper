package org.amoustakos.exifstripper.usecases.exifremoval.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.amoustakos.exifstripper.ui.dialogs.ErrorDialog
import org.amoustakos.exifstripper.utils.ExifFile


class ExifViewModel(private val state: SavedStateHandle) : ViewModel() {

	var adapterData: MutableLiveData<MutableList<ExifAttributeViewData>>
		get() = state.getLiveData(KEY_ADAPTER_DATA)
		set(value) = state.set(KEY_ADAPTER_DATA, value)

	var exifFile: MutableLiveData<ExifFile?>
		get() = state.getLiveData(KEY_EXIF)
		set(value) = state.set(KEY_EXIF, value)

	val errorDialog: MutableLiveData<ErrorDialog?> by lazy { MutableLiveData<ErrorDialog?>() }

	companion object {
		private const val KEY_EXIF = "key_exif"
		private const val KEY_ADAPTER_DATA = "key_adapter_data"
	}

}