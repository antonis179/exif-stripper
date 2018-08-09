package org.amoustakos.exifstripper.net

import org.amoustakos.exifstripper.injection.annotations.network.DefaultRetrofitOptions
import org.amoustakos.utils.network.retrofit.RetrofitEngineOptions

object RetrofitConfig {

	@DefaultRetrofitOptions
	@JvmStatic
	fun defaultOptions() =
			RetrofitEngineOptions("http://dummy.io")

}