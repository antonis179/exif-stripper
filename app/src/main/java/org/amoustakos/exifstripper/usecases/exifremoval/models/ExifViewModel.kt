package org.amoustakos.exifstripper.usecases.exifremoval.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.amoustakos.exifstripper.ui.dialogs.ErrorDialog
import org.amoustakos.exifstripper.utils.ExifFile


class ExifViewModel : ViewModel() {

	val adapterData: MutableLiveData<MutableList<ExifAttributeViewData>> by lazy {
		MutableLiveData<MutableList<ExifAttributeViewData>>()
	}

	val exifFile: MutableLiveData<ExifFile?> by lazy { MutableLiveData<ExifFile?>() }

	val errorDialog: MutableLiveData<ErrorDialog?> by lazy { MutableLiveData<ErrorDialog?>() }

}