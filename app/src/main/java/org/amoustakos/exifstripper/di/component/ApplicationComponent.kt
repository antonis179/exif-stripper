package org.amoustakos.exifstripper.di.component

import android.app.Application
import android.content.Context
import dagger.Component
import io.realm.RealmConfiguration
import org.amoustakos.exifstripper.Environment
import org.amoustakos.exifstripper.di.annotations.context.ApplicationContext
import org.amoustakos.exifstripper.di.annotations.realm.DefaultRealmConfig
import org.amoustakos.exifstripper.di.module.ApplicationModule
import org.amoustakos.exifstripper.di.module.DBModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
	ApplicationModule::class,
	DBModule::class
])
interface ApplicationComponent {


	// =========================================================================================
	// Base
	// =========================================================================================

	@ApplicationContext
	fun context(): Context

	fun application(): Application
	fun environment(): Environment


	// =========================================================================================
	// DB
	// =========================================================================================

	@DefaultRealmConfig
	fun defaultRealmConfig(): RealmConfiguration

}
