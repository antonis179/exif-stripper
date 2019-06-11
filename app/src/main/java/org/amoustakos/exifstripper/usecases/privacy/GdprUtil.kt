package org.amoustakos.exifstripper.usecases.privacy

import android.content.Context
import org.amoustakos.exifstripper.io.prefs.BaseSharedPreferences

object GdprUtil {


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




