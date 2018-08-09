package org.amoustakos.exifstripper

import android.app.Application
import android.content.Context

import org.amoustakos.exifstripper.injection.component.ApplicationComponent
import org.amoustakos.exifstripper.injection.component.DaggerApplicationComponent
import org.amoustakos.exifstripper.injection.module.ApplicationModule
import org.amoustakos.exifstripper.injection.module.DBModule
import org.amoustakos.exifstripper.injection.module.NetworkModule
import timber.log.Timber


class ExifStripperApplication : Application() {

	lateinit var component: ApplicationComponent

	override fun onCreate() {
		val start = System.currentTimeMillis()
		super.onCreate()

		makeComponent()

		component.environment().init()

		val end = System.currentTimeMillis() - start
		Timber.d("Application initialized in $end ms.")
	}

	override fun onLowMemory() {
		super.onLowMemory()
		//TODO: EVENT
	}

	private fun makeComponent() {
		component = DaggerApplicationComponent.builder()
				.applicationModule(ApplicationModule(this))
				.dBModule(DBModule)
				.networkModule(NetworkModule)
				.build()
	}



	companion object {
		@JvmStatic operator fun get(context: Context): ExifStripperApplication {
			return context.applicationContext as ExifStripperApplication
		}
	}

}
