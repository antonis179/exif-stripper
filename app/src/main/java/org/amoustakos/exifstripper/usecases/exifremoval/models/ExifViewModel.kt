package org.amoustakos.exifstripper.usecases.exifremoval.models

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ExifViewModel : ViewModel() {


	val imageUri: MutableLiveData<Uri?> by lazy { MutableLiveData<Uri?>() }

	val adapterData: MutableLiveData<MutableList<ExifAttributeViewData>> by lazy {
		MutableLiveData<MutableList<ExifAttributeViewData>>()
	}

	val save: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }


}