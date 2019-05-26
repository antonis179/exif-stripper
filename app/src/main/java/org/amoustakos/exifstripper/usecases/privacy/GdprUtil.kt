package org.amoustakos.exifstripper.usecases.privacy

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.FirebasePerformance
import io.fabric.sdk.android.Fabric
import org.amoustakos.exifstripper.io.prefs.BaseSharedPreferences

object GdprUtil {


	fun enableAnalytics(ctx: Context) {
		FirebaseAnalytics.getInstance(ctx).setAnalyticsCollectionEnabled(true)
		FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
		Fabric.with(Crashlytics.getInstance().context)
	}

	fun hasAcceptedTerms(ctx: Context) = PrivacySharedPrefs(ctx).hasAcceptedTerms

	fun acceptTerms(ctx: Context) {
		PrivacySharedPrefs(ctx).hasAcceptedTerms = true
	}


}




class PrivacySharedPrefs(ctx: Context) : BaseSharedPreferences("privacy_prefs", Context.MODE_PRIVATE, ctx) {

	companion object {
		private const val HAS_ACCEPTED_TERMS = "key_has_accepted_terms"
	}


	var hasAcceptedTerms: Boolean
		set(value) { set(HAS_ACCEPTED_TERMS, value) }
		get() = get(HAS_ACCEPTED_TERMS, false)

}




