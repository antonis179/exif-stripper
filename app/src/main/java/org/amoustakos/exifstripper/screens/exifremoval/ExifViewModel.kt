package org.amoustakos.exifstripper.screens.exifremoval

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.amoustakos.exifstripper.screens.exifremoval.adapters.ExifImageViewData
import org.amoustakos.exifstripper.screens.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.ui.dialogs.ErrorDialog


class ExifViewModel(private val state: SavedStateHandle) : ViewModel() {

	var attrAdapterData: MutableLiveData<MutableList<ExifAttributeViewData>>
		get() = state.getLiveData(KEY_ATTR_ADAPTER_DATA)
		set(value) = state.set(KEY_ATTR_ADAPTER_DATA, value)

	var adapterData: MutableLiveData<MutableList<ExifImageViewData>>
		get() = state.getLiveData(KEY_ADAPTER_DATA)
		set(value) = state.set(KEY_ADAPTER_DATA, value)

	var exifFiles: MutableLiveData<MutableList<ExifRemovalFile>?>
		get() = state.getLiveData(KEY_EXIF)
		set(value) = state.set(KEY_EXIF, value)

	val errorDialog: MutableLiveData<ErrorDialog?> by lazy { MutableLiveData<ErrorDialog?>() }

	companion object {
		private const val KEY_EXIF = "key_exif"
		private const val KEY_ATTR_ADAPTER_DATA = "key_attr_adapter_data"
		private const val KEY_ADAPTER_DATA = "key_adapter_data"
	}

}