package org.amoustakos.exifstripper.ui.contracts.base

import android.arch.lifecycle.Lifecycle

interface BaseContractActions {
	fun init() {}

	fun subscribeToLifecycle(lifecycle: Lifecycle)
	fun unsubscribeFromLifecycle(lifecycle: Lifecycle)
}

interface BaseContractView