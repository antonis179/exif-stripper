package org.amoustakos.exifstripper.usecases.exifremoval.models

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ExifViewModel : ViewModel() {


	val imageUri: MutableLiveData<Uri?> by lazy { MutableLiveData<Uri?>() }


}