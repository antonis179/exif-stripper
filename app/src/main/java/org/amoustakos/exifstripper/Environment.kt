package org.amoustakos.exifstripper

import android.annotation.SuppressLint
import android.content.Context
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import org.amoustakos.exifstripper.utils.ExifFile
import timber.log.Timber

class Environment (
    val context: Context
) {

    init {
        initPrefs()
        initLog()
//        initRealm()
        cleanup()
        initAnalytics()
        gdpr()
    }

    private fun initAnalytics() {
        Fabric.with(context, Crashlytics())
    }


    private fun initPrefs() {}

    private fun initLog() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

//    private fun initRealm() {
//        Realm.init(context)
//
//        //Get config here or realm config will be requested before realm init and crash
//        Realm.setDefaultConfiguration(
//                RealmConfig.defaultConfig()
//        )
//    }

    @SuppressLint("CheckResult")
    private fun cleanup() {
        Observable
                .fromCallable { ExifFile.clearCache(context) }
                .observeOn(Schedulers.computation())
                .subscribe(
                        {Timber.v("Cleared image cache")},
                        (Timber::e)
                )
    }


    private fun gdpr() {
        if (GdprUtil.hasAcceptedTerms(context))
            GdprUtil.enableAnalytics(context)
    }

}
