package org.amoustakos.exifstripper

import android.annotation.SuppressLint
import android.content.Context
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.screens.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.screens.settings.SettingsUtil
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.ads.AdUtility
import org.amoustakos.exifstripper.utils.exif.ExifFile
import org.amoustakos.exifstripper.utils.privacy.GdprUtil
import timber.log.Timber

class Environment (
    val context: Context
) {

    init {
        initRemoteConfig()
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

    private fun initPrefs() {
	    SettingsUtil.setInitialValues(context)
    }

    private fun initLog() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    private fun initAds() {
	    AdUtility()
    }

    private fun initRemoteConfig() {
//        FBRemoteConfigUtility.initialize()
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
