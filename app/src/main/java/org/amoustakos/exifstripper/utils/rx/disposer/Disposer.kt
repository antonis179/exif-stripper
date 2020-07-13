package org.amoustakos.exifstripper.utils.rx.disposer

import io.reactivex.*
import io.reactivex.disposables.Disposable

/**
 * A Disposer is parent of multiple [Disposable] objects that will be disposed
 * once the disposer receives a [dispose] event.
 *
 * Disposers instances can be reused and allow for multiple [dispose] calls.
 * Be aware, that this fact means, that a [Disposer] will therefore always
 * return true in [isDisposed]
 */
interface Disposer : Disposable {

    /**
     * Disposer is never disposed, because the [dispose] function can be called
     * multiple times, which will just reset the state of the disposer
     */
    override fun isDisposed(): Boolean = false

    /**
     * @param disposable will be disposed once this disposer receives a [dispose] signal
     */
    fun add(disposable: Disposable)

    /**
     * Syntactic sugar for [add]
     */
    operator fun plusAssign(disposable: Disposable)

    /**
     * Will dispose the upstream once this disposer receives a [dispose] signal
     */
    fun <T> bind(observable: Observable<T>): Observable<T>

    /**
     * Will dispose the upstream once this disposer receives a [dispose] signal
     */
    fun <T> bind(flowable: Flowable<T>): Flowable<T>

    /**
     * Will dispose the upstream once this disposer receives a [dispose] signal
     */
    fun <T> bind(single: Single<T>): Single<T>

    /**
     * Will dispose the upstream once this disposer receives a [dispose] signal
     */
    fun <T> bind(maybe: Maybe<T>): Maybe<T>

    /**
     * Will dispose the upstream once this disposer receives a [dispose] signal
     */
    fun bind(completable: Completable): Completable

}