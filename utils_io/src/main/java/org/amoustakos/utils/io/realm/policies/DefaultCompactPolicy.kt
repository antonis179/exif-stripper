package org.amoustakos.utils.io.realm.policies


/**
 * This class will compact a DB when its total size is higher than [THRESHOLD] and the
 * usage is lower than [USAGE_THRESHOLD]
 */
class DefaultCompactPolicy : CompactPolicy() {

	override fun mbThreshold(): Long    = 10 * 1024 * 1024
	override fun usageThreshold()       = 0.75

}