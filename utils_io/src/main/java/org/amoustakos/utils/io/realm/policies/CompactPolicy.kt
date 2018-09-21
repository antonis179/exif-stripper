package org.amoustakos.utils.io.realm.policies

import io.realm.CompactOnLaunchCallback

abstract class CompactPolicy: CompactOnLaunchCallback {

	override fun equals(other: Any?) =
			other != null && other is CompactPolicy
	override fun hashCode() = 0


	abstract fun mbThreshold(): Long
	abstract fun usageThreshold(): Double


	override fun shouldCompact(totalBytes: Long, usedBytes: Long) =
			totalBytes > mbThreshold() && usedBytes.toDouble() / totalBytes.toDouble() < usageThreshold()
}