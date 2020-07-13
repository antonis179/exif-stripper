package org.amoustakos.exifstripper.utils.rx.disposer

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver


internal class LifecycleDisposer(
    lifecycle: Lifecycle,
    vararg events: Lifecycle.Event
) : Disposer by SyncedDisposer() {

    private val observer: LifecycleObserver = GenericLifecycleObserver { _, event ->
        if (events.contains(event))     dispose()
    }

    init {
        lifecycle.addObserver(observer)
    }
}