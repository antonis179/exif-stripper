package org.amoustakos.exifstripper

import android.app.Application
import android.content.Context
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import timber.log.Timber
import java.lang.ref.WeakReference


class ExifApplication : Application() {

	lateinit var environment: Environment

	init {
		appContext = WeakReference(this)
	}

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
		var appContext: WeakReference<Context?> = WeakReference(null)
			private set

		@JvmStatic operator fun get(context: Context): ExifApplication {
			return context.applicationContext as ExifApplication
		}
	}

}
