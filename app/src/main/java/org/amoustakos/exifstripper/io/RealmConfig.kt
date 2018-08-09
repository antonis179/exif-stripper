package org.amoustakos.exifstripper.io

import io.realm.CompactOnLaunchCallback
import io.realm.RealmConfiguration

object RealmConfig {

	private const val DEFAULT_VERSION = 0L


    @JvmStatic fun defaultConfig(): RealmConfiguration =
		    RealmConfiguration.Builder()
				    .schemaVersion(DEFAULT_VERSION)
				    .deleteRealmIfMigrationNeeded()
				    .compactOnLaunch(Compact())
				    .build()


	class Compact : CompactOnLaunchCallback {

		override fun equals(other: Any?) =
				other != null && other is Compact

		override fun hashCode() = 0

		override fun shouldCompact(totalBytes: Long, usedBytes: Long): Boolean {
			val thresholdSize = (10 * 1024 * 1024).toLong()
			return totalBytes > thresholdSize && usedBytes.toDouble() / totalBytes.toDouble() < 0.5
		}
	}

}