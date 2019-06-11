package org.amoustakos.exifstripper.usecases.privacy

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.FirebasePerformance
import io.fabric.sdk.android.Fabric
import org.amoustakos.exifstripper.BuildConfig

object AnalyticsUtil {

    fun enableAnalytics(ctx: Context) {
        if (BuildConfig.DEBUG)
            return

        FirebaseAnalytics.getInstance(ctx).setAnalyticsCollectionEnabled(true)
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
        Fabric.with(Crashlytics.getInstance().context)
    }

}