package org.amoustakos.exifstripper

import android.app.Application
import android.content.Context
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import timber.log.Timber


class ExifApplication : Application() {

	lateinit var environment: Environment

	override fun onCreate() {
		super.onCreate()
		environment = Environment(this)
		overrideTerms()
	}

	private fun overrideTerms() {
		if (!GdprUtil.hasAcceptedTerms(this))
			GdprUtil.acceptTerms(this)
	}

	companion object {
		@JvmStatic operator fun get(context: Context): ExifApplication {
			return context.applicationContext as ExifApplication
		}
	}

}
