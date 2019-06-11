package org.amoustakos.exifstripper

import android.annotation.SuppressLint
import android.content.Context
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import org.amoustakos.exifstripper.utils.ExifFile
import timber.log.Timber

class Environment (
    val context: Context
) {

    init {
        initPrefs()
        initLog()
        cleanup()
        initAnalytics()
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
