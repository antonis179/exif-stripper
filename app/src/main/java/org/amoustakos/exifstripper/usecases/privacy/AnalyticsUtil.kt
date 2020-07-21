package org.amoustakos.exifstripper.usecases.privacy

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.amoustakos.exifstripper.BuildConfig
import timber.log.Timber

object AnalyticsUtil {

	fun enableAnalytics(ctx: Context) {
		if (BuildConfig.DEBUG)
			return

		FirebaseAnalytics.getInstance(ctx).setAnalyticsCollectionEnabled(true)
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
//        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
	}

	fun logException(e: Throwable, crashlyticsOnly: Boolean = false) {
		if (!crashlyticsOnly) Timber.e(e)
		FirebaseCrashlytics.getInstance().recordException(e)
	}

    fun logMessage(message: String, crashlyticsOnly: Boolean = false) {
        if (!crashlyticsOnly) Timber.e(message)
        FirebaseCrashlytics.getInstance().log(message)
    }


}