package org.amoustakos.exifstripper.utils.ads

import android.app.Activity
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.ExifApplication
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.FBRemoteConfigInitListener
import org.amoustakos.exifstripper.utils.FBRemoteConfigUtility

//TODO: ad load event logging + add bundle for event logging
object AdUtility {

	private const val TIMEOUT = 10 * 1000
	private val waitingThread = Schedulers.newThread()
	private var subscription: Disposable? = null

	private var remoteConfigLoaded = false
	private var remoteConfigDefaultsSet = false
	private var initialized = false

	private val callbacks = mutableListOf<AdLoadedListener>()

	private val fbRemoteListener = object : FBRemoteConfigInitListener {
		override fun onRemoteInit() {
			remoteConfigLoaded = true
		}

		override fun onDefaultsLoaded() {
			remoteConfigDefaultsSet = true
		}

		override fun onFailDefaults() {
			remoteConfigDefaultsSet = false
		}

		override fun onFailRemote() {
			remoteConfigLoaded = false
		}
	}


	private val DEFAULT_NETWORK = AdNetwork.Appodeal


	init {
		Do safeLogged { initAds() }
		FBRemoteConfigUtility.registerCallback(fbRemoteListener)
	}

	operator fun invoke() {}

	private fun initAds() {
		if (initialized) return

		initialized = true

		//Admob
		MobileAds.initialize(ExifApplication.appContext.get()) {}

		//Appodeal
//		Appodeal.disableLocationPermissionCheck()
//		Appodeal.disableWriteExternalStoragePermissionCheck()
//		Appodeal.setBannerAnimation(true)
//		Appodeal.set728x90Banners(true)
//
//		if (BuildConfig.DEBUG) {
//			Appodeal.setLogLevel(Log.LogLevel.verbose)
//			Appodeal.setTesting(true)
//		}
//
//		Appodeal.setAutoCache(Appodeal.BANNER_VIEW, true)
	}

	fun registerCallback(callback: AdLoadedListener) {
		callbacks.add(callback)
	}

	fun unRegisterCallback(callback: AdLoadedListener) = callbacks.remove(callback)

	@Synchronized
	fun inflateFooterAdView(viewGroup: ViewGroup) {
		if (!remoteConfigLoaded) {
			if (subscription != null) {
				subscription?.dispose()
				subscription = null
			}
			subscription = Single.fromCallable {}
					.observeOn(waitingThread)
					.subscribeOn(AndroidSchedulers.mainThread())
					.map {
						var time = 0L
						val step = 200L
						//Wait some time for remote config to load
						while (!remoteConfigLoaded && time < TIMEOUT) {
							Thread.sleep(step)
							time += step
						}
					}
					.observeOn(AndroidSchedulers.mainThread())
					.onErrorReturn { }
					.doOnError { inflateFooterAdViewInner(viewGroup) }
					.doOnSuccess { inflateFooterAdViewInner(viewGroup) }
					.subscribe()
		} else {
			inflateFooterAdViewInner(viewGroup)
		}
	}

	private fun inflateFooterAdViewInner(viewGroup: ViewGroup) {

		//When everything has failed!
		if (!remoteConfigLoaded && !remoteConfigDefaultsSet) {
			AnalyticsUtil.logEvent(viewGroup.context, "ads_defaulted_no_config")
			inflateAndLoadFooterAdmob(viewGroup)
			return
		}

		//If ads should not be shown
		if (!FBRemoteConfigUtility.shouldShowAds()) {
			AnalyticsUtil.logEvent(viewGroup.context, "ads_disabled")
			return
		}

		val network = getAdNetwork()

		AnalyticsUtil.logEvent(viewGroup.context, "loading_net_${network.name}")

		Do exhaustive when (network) {
			AdNetwork.AdMob -> inflateAndLoadFooterAdmob(viewGroup)
			AdNetwork.Appodeal -> {} //inflateAndLoadFooterAppodeal(viewGroup)
		}
	}


	fun onFooterResume(ctx: Activity) {
		Do exhaustive when (getAdNetwork()) {
			AdNetwork.AdMob -> {
			}
			AdNetwork.Appodeal -> {
				//Appodeal.onResume(ctx, Appodeal.BANNER_VIEW)
			}
		}
	}

	private fun inflateAndLoadFooterAdmob(viewGroup: ViewGroup) {
		val ctx: Activity = viewGroup.context as Activity
		viewGroup.removeAllViews()

		val ad = AdView(ctx)
		//TODO: add id to remote config
		ad.adUnitId = ctx.getString(R.string.admob_footer_banner)
		ad.adSize = admobSize(ctx, viewGroup)
		ad.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

		viewGroup.addView(ad)

		Do safeLogged {
			//https://developers.google.com/admob/android/banner/adaptive
			MobileAds.initialize(ctx) {}
			val adRequest = AdRequest.Builder().build()
			ad.adListener = object : AdListener() {
				override fun onAdImpression() {
					super.onAdImpression()
					AnalyticsUtil.logEvent(viewGroup.context, "ad_impression_${AdNetwork.AdMob.name}")
				}

				override fun onAdClicked() {
					super.onAdClicked()
					AnalyticsUtil.logEvent(viewGroup.context, "ad_click_${AdNetwork.AdMob.name}")
				}

				override fun onAdFailedToLoad(p0: LoadAdError?) {
					super.onAdFailedToLoad(p0)
					AnalyticsUtil.logEvent(viewGroup.context, "ad_load_fail_${AdNetwork.AdMob.name}")
				}

				override fun onAdOpened() {
					super.onAdOpened()
					AnalyticsUtil.logEvent(viewGroup.context, "ad_opened_${AdNetwork.AdMob.name}")
				}

				override fun onAdLoaded() {
					super.onAdLoaded()
					AnalyticsUtil.logEvent(viewGroup.context, "ad_load_${AdNetwork.AdMob.name}")
					Do safe { callbacks.forEach { it.onAdLoaded() } }
				}
			}
			ad.loadAd(adRequest)
		}
	}

//	private fun inflateAndLoadFooterAppodeal(viewGroup: ViewGroup) {
//		val ctx: Activity = viewGroup.context as Activity
//		viewGroup.removeAllViews()
//
//		LayoutInflater
//				.from(viewGroup.context)
//				.inflate(R.layout.ad_footer_appodeal, viewGroup, true)
//
//		Do safeLogged {
//			Appodeal.initialize(
//					ctx,
//					ctx.getString(R.string.appodeal_app_key),
//					Appodeal.BANNER_VIEW,
//					true
//			)
//			Appodeal.setBannerViewId(R.id.adFooter)
//			Appodeal.show(ctx, Appodeal.BANNER_VIEW)
//		}
//	}

	private fun admobSize(ctx: Activity, viewGroup: ViewGroup): AdSize {
		val display = ctx.windowManager.defaultDisplay
		val outMetrics = DisplayMetrics()
		display.getMetrics(outMetrics)

		val density = outMetrics.density

		var adWidthPixels = viewGroup.width.toFloat()
		if (adWidthPixels == 0f) {
			adWidthPixels = outMetrics.widthPixels.toFloat()
		}

		val adWidth = (adWidthPixels / density).toInt()
		return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth)
	}


	private fun getAdNetwork() = AdNetwork.forString(FBRemoteConfigUtility.adNetwork(), DEFAULT_NETWORK)

}


sealed class AdNetwork(val name: String) {

	companion object {
		fun forString(s: String, default: AdNetwork): AdNetwork = (Do exhaustive when (s) {
			AdMob.name -> AdMob
			Appodeal.name -> Appodeal
			else -> default
		})!!
	}

	object AdMob : AdNetwork("ADMOB")
	object Appodeal : AdNetwork("APPODEAL")
}


interface AdLoadedListener {
	fun onAdLoaded()
}