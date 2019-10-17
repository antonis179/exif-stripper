package org.amoustakos.exifstripper

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.ads.MobileAds
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import org.amoustakos.exifstripper.utils.ExifFile
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
            if (!BuildConfig.DEBUG)  initAds()
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
        if (GdprUtil.hasAcceptedTerms(context))
            MobileAds.initialize(context) {}
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
