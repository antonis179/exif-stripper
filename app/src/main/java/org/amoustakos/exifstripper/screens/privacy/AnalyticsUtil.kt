package org.amoustakos.exifstripper.screens.privacy

import android.content.Context
import android.os.Bundle
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

		FirebaseCrashlytics.getInstance().sendUnsentReports()
	}

	fun logException(e: Throwable, crashlyticsOnly: Boolean = false) {
		if (!crashlyticsOnly) Timber.e(e)
		FirebaseCrashlytics.getInstance().recordException(e)
	}

    fun logMessage(message: String, crashlyticsOnly: Boolean = false) {
        if (!crashlyticsOnly) Timber.e(message)
        FirebaseCrashlytics.getInstance().log(message)
    }

	fun logEvent(ctx: Context, name: String, message: String? = null) {
		FirebaseAnalytics.getInstance(ctx).logEvent(
				name,
				Bundle().apply { putString(name, message) }
		)
	}

}