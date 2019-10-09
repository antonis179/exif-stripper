package org.amoustakos.exifstripper

import android.app.Application
import android.content.Context
import timber.log.Timber




class ExifApplication : Application() {

	lateinit var environment: Environment

	override fun onCreate() {
		val start = System.currentTimeMillis()

		super.onCreate()

		environment = Environment(this)

		val end = System.currentTimeMillis() - start
		Timber.d("Application initialized in $end ms.")
	}

	companion object {
		@JvmStatic operator fun get(context: Context): ExifApplication {
			return context.applicationContext as ExifApplication
		}
	}

}
