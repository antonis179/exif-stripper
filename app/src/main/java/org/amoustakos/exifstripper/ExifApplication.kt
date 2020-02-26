package org.amoustakos.exifstripper

import android.app.Application
import android.content.Context
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import timber.log.Timber




class ExifApplication : Application() {

	lateinit var environment: Environment

	override fun onCreate() {
		val start = System.currentTimeMillis()

		super.onCreate()

		environment = Environment(this)

		overrideTerms()

		val end = System.currentTimeMillis() - start
		Timber.d("Application initialized in $end ms.")
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
