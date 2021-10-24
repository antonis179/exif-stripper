package org.amoustakos.exifstripper.usecases.exifaddedit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.amoustakos.exifstripper.io.model.ExifAttribute

class ExifEditViewModel(private val state: SavedStateHandle) : ViewModel() {

	var exifAttribute: MutableLiveData<ExifAttribute>
		get() = state.getLiveData(KEY_EXIF)
		set(value) = state.set(KEY_EXIF, value)

	companion object {
		private const val KEY_EXIF = "key_exif"
	}

}