package org.amoustakos.exifstripper.utils.ads

import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import org.amoustakos.exifstripper.utils.Do
import timber.log.Timber

object AdMobAds {


	fun loadAd(view: AdView, listener: AdListener? = null) {
		Do.safe({
			view.loadAd(AdRequest.Builder().build())
			view.adListener = listener
		}, {
			Timber.e(it)
			Crashlytics.logException(it)
		})
	}



}