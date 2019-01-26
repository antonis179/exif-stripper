package org.amoustakos.exifstripper

import android.app.Application
import android.content.Context
import org.amoustakos.exifstripper.di.component.ApplicationComponent
import org.amoustakos.exifstripper.di.component.DaggerApplicationComponent
import org.amoustakos.exifstripper.di.module.ApplicationModule
import org.amoustakos.exifstripper.di.module.DBModule
import timber.log.Timber


class ExifApplication : Application() {

	lateinit var component: ApplicationComponent

	override fun onCreate() {
		val start = System.currentTimeMillis()
		super.onCreate()

		makeComponent()

		component.environment().init()

		val end = System.currentTimeMillis() - start
		Timber.d("Application initialized in $end ms.")
	}

	private fun makeComponent() {
		component = DaggerApplicationComponent.builder()
				.applicationModule(ApplicationModule(this))
				.dBModule(DBModule)
				.build()
	}



	companion object {
		@JvmStatic operator fun get(context: Context): ExifApplication {
			return context.applicationContext as ExifApplication
		}
	}

}
