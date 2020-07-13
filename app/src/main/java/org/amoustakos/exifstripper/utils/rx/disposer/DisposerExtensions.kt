package org.amoustakos.exifstripper.utils.rx.disposer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.*
import io.reactivex.disposables.Disposable


/**
 * Will dispose the upstream once this disposer receives a [dispose] signal
 * @see Disposer.bind
 */
fun <T> Observable<T>.disposeBy(disposer: Disposer): Observable<T> = disposer.bind(this)

/**
 * Will dispose the upstream once this disposer receives a [dispose] signal
 * @see Disposer.bind
 */
fun <T> Flowable<T>.disposeBy(disposer: Disposer): Flowable<T> = disposer.bind(this)

/**
 * Will dispose the upstream once this disposer receives a [dispose] signal
 * @see Disposer.bind
 */
fun <T> Single<T>.disposeBy(disposer: Disposer): Single<T> = disposer.bind(this)

/**
 * Will dispose the upstream once this disposer receives a [dispose] signal
 * @see Disposer.bind
 */
fun <T> Maybe<T>.disposeBy(disposer: Disposer): Maybe<T> = disposer.bind(this)

/**
 * Will dispose the upstream once this disposer receives a [dispose] signal
 * @see Disposer.bind
 */
fun Completable.disposeBy(disposer: Disposer): Completable = disposer.bind(this)

/**
 * Will dispose once this disposer receives a [dispose] signal
 * @see Disposer.bind
 */
fun Disposable.disposeBy(disposer: Disposer) = this.apply { disposer.add(this) }



/**
 * The associated disposers for the given lifecycle.
 * There is guaranteed only a single instance for each lifecycle that can be used.
 * All disposers will automatically dispose on the dedicated lifecycle event
 */
val Lifecycle.disposers: LifecycleDisposers
	get() = LifecycleDisposers.Store[this]

/**
 * Shortcut for ```.lifecycle.disposers```
 * @see Lifecycle.disposers
 */
val LifecycleOwner.disposers: LifecycleDisposers
	get() = lifecycle.disposers


val Lifecycle.onCreate get()        = disposers.onCreate
val Lifecycle.onStart get()         = disposers.onStart
val Lifecycle.onResume get()        = disposers.onResume
val Lifecycle.onPause get()         = disposers.onPause
val Lifecycle.onStop get()          = disposers.onStop
val Lifecycle.onDestroy get()       = disposers.onDestroy


val LifecycleOwner.onCreate get()   = lifecycle.onCreate
val LifecycleOwner.onStart get()    = lifecycle.onStart
val LifecycleOwner.onResume get()   = lifecycle.onResume
val LifecycleOwner.onPause get()    = lifecycle.onPause
val LifecycleOwner.onStop get()     = lifecycle.onStop
val LifecycleOwner.onDestroy get()  = lifecycle.onDestroy