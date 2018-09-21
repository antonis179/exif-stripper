package org.amoustakos.exifstripper.io

import io.realm.RealmConfiguration
import org.amoustakos.utils.io.realm.policies.DefaultCompactPolicy

object RealmConfig {

	private const val DEFAULT_VERSION = 0L


    @JvmStatic fun defaultConfig(): RealmConfiguration =
		    RealmConfiguration.Builder()
				    .name("general")
				    .schemaVersion(DEFAULT_VERSION)
				    .deleteRealmIfMigrationNeeded()
				    .compactOnLaunch(DefaultCompactPolicy())
				    .build()

}