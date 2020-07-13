package org.amoustakos.exifstripper.utils

import timber.log.Timber

/**
 * Kotlin object that provides inline utility methods
 * (e.g. Do exhaustive when(...) {...})
 */
object Do {

	/**
	 * Can be used to force exhaustive usage of 'when'.
	 * When one or more cases are not handled the code will not compile.
	 *
	 * e.g. Do exhaustive when(sealedClass) {...}
	 */
	inline infix fun <reified T> exhaustive(any: T?) = any

	/**
	 * Can be used to run a block of code "safely" inside a try catch method.
	 * Only errors extending [Exception] are caught.
	 */
	inline infix fun <reified T> safe(any: () -> T) {
		try {
			any()
		} catch (e: Exception) {
			Timber.e(e)
		}
	}

	/**
	 * Can be used to run a block of code "safely" inside a try catch method.
	 * Only errors extending [Exception] are caught.
	 */
	inline fun <reified T> safe(any: () -> T, onErrorExecute: (e: Exception) -> T): T {
		return try {
			any()
		} catch (e: Exception) {
			onErrorExecute(e)
		}
	}
}