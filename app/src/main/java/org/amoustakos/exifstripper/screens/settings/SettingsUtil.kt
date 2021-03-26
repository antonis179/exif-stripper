package org.amoustakos.exifstripper.screens.settings

import android.content.Context
import org.amoustakos.exifstripper.ExifApplication
import org.amoustakos.exifstripper.io.prefs.BaseSharedPreferences


object SettingsUtil {

	private val prefs = SettingsPreferences()

	fun setInitialValues(ctx: Context) {}

	// =========================================================================================
	// Autosave
	// =========================================================================================

	fun getAutosave() = prefs.getAutosave()
	fun setAutosave(autosave: Boolean) = prefs.setAutosave(autosave)

	// =========================================================================================
	// Inner
	// =========================================================================================

	private class SettingsPreferences : BaseSharedPreferences(
			"settings",
			Context.MODE_PRIVATE,
			ExifApplication.appContext.get()!!
	) {
		companion object {
			private const val KEY_AUTOSAVE = "key_autosave"
		}

		fun setAutosave(autosave: Boolean) = set(KEY_AUTOSAVE, autosave)
		fun getAutosave() = get(KEY_AUTOSAVE, false)
	}

}