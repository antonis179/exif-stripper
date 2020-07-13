@file:Suppress("unused")

package org.amoustakos.exifstripper.utils.rx.disposer

import androidx.lifecycle.Lifecycle
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


private val disposers = WeakHashMap<Lifecycle, LifecycleDisposers>()
private val disposersLock = ReentrantLock()


/**
 * Object holding all possible disposers for a given [Lifecycle] object.
 * Each [Lifecycle] owns one single instance of this disposers
 *
 * @see Lifecycle.disposers
 * @see Lifecycle.onCreate
 * @see Lifecycle.onStart
 * @see Lifecycle.onResume
 * @see Lifecycle.onPause
 * @see Lifecycle.onPause
 * @see Lifecycle.onStop
 * @see Lifecycle.onDestroy
 */
class LifecycleDisposers(
		val onCreate: Disposer,
		val onStart: Disposer,
		val onResume: Disposer,
		val onPause: Disposer,
		val onStop: Disposer,
		val onDestroy: Disposer
) {

    internal object Factory {
	    internal fun create(lifecycle: Lifecycle): LifecycleDisposers {
		    return LifecycleDisposers(
				    onCreate = LifecycleDisposer(lifecycle, Lifecycle.Event.ON_CREATE),
				    onStart = LifecycleDisposer(lifecycle, Lifecycle.Event.ON_START),
				    onResume = LifecycleDisposer(lifecycle, Lifecycle.Event.ON_RESUME),
				    onPause = LifecycleDisposer(lifecycle, Lifecycle.Event.ON_PAUSE),
				    onStop = LifecycleDisposer(lifecycle, Lifecycle.Event.ON_STOP),
				    onDestroy = LifecycleDisposer(lifecycle, Lifecycle.Event.ON_DESTROY))
	    }
    }

    /**
     * Each [Lifecycle] will have it's own dedicated [LifecycleDisposers] object which
     * can be retrieved from this store.
     */
    internal object Store {
	    internal operator fun get(lifecycle: Lifecycle):
			    LifecycleDisposers = disposersLock.withLock {
		    disposers.getOrPut(lifecycle) { Factory.create(lifecycle) }
	    }
    }

}




