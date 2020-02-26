package org.amoustakos.exifstripper

import android.annotation.SuppressLint
import android.content.Context
import com.appodeal.ads.Appodeal
import com.appodeal.ads.utils.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import org.amoustakos.exifstripper.utils.exif.ExifFile
import org.amoustakos.utils.android.kotlin.Do
import timber.log.Timber

class Environment (
    val context: Context
) {

    init {
        initPrefs()
        initLog()
        cleanup()
        initAnalytics()
        initAds()
    }

    fun onGdprUpdate() {
        Do safe {
            initAnalytics()
            initAds()
        }
    }

    private fun initAnalytics() {
        if (GdprUtil.hasAcceptedTerms(context))
            AnalyticsUtil.enableAnalytics(context)
    }

    private fun initPrefs() {}

    private fun initLog() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    private fun initAds() {
	    Appodeal.disableLocationPermissionCheck()
	    Appodeal.disableWriteExternalStoragePermissionCheck()
	    Appodeal.set728x90Banners(true)

	    if (BuildConfig.DEBUG) {
		    Appodeal.setLogLevel(Log.LogLevel.verbose)
		    Appodeal.setTesting(true)
	    }

	    Appodeal.setAutoCache(Appodeal.BANNER_VIEW, true)
    }

    @SuppressLint("CheckResult")
    private fun cleanup() {
        Single
                .fromCallable { }
                .observeOn(Schedulers.computation())
                .doOnSuccess { ExifFile.clearCache(context) }
                .subscribe(
                        {Timber.v("Cleared image cache")},
                        (Timber::e)
                )
    }
}
