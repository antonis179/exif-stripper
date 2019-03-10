package org.amoustakos.exifstripper.usecases.exifremoval.models

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar


class ExifViewModel : ViewModel() {


	val imageUri: MutableLiveData<Uri?> by lazy { MutableLiveData<Uri?>() }

	val adapterData: MutableLiveData<MutableList<ExifAttributeViewData>> by lazy {
		MutableLiveData<MutableList<ExifAttributeViewData>>()
	}

	val snackbar: MutableLiveData<Snackbar?> by lazy { MutableLiveData<Snackbar?>() }


}