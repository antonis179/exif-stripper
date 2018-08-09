package org.amoustakos.exifstripper.injection.module


import dagger.Module
import dagger.Provides
import org.amoustakos.exifstripper.injection.annotations.realm.DefaultRealmConfig
import org.amoustakos.exifstripper.io.RealmConfig

@Module
object DBModule {

	@Provides
	@DefaultRealmConfig
	fun provideDefaultRealmConfig() = RealmConfig.defaultConfig()

}
