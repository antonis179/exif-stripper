package org.amoustakos.exifstripper.utils.ads
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import org.amoustakos.exifstripper.BuildConfig
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.utils.Do

object AdMobAds {


	fun loadAd(view: AdView, listener: AdListener? = null) {
		if (BuildConfig.DEBUG) return
		Do.safe({
			view.loadAd(AdRequest.Builder().build())
			view.adListener = listener
		}, {
			AnalyticsUtil.logException(it)
		})
	}



}