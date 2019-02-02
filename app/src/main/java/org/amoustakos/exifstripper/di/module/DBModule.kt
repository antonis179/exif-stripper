package org.amoustakos.exifstripper.di.module


import dagger.Module
import dagger.Provides
import org.amoustakos.exifstripper.di.annotations.realm.DefaultRealmConfig
import org.amoustakos.exifstripper.io.realm.RealmConfig

@Module
object DBModule {

	@Provides
	@DefaultRealmConfig
	fun provideDefaultRealmConfig() = RealmConfig.defaultConfig()

}
