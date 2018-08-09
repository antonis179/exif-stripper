package org.amoustakos.utils.android

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.ListCompositeDisposable

class LifecycleDisposableList(
        private val opts: LifecycleDisposableOpts
): DefaultLifecycleObserver {

    constructor(): this(LifecycleDisposableOpts())


    @set:Synchronized
    @get:Synchronized
    private lateinit var disposables: ListCompositeDisposable


    fun subscribeToLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    fun unsubscribeToLifecycle(lifecycle: Lifecycle) {
        lifecycle.removeObserver(this)
    }


    // =========================================================================================
    // Functionality
    // =========================================================================================

    fun initSubscriptions() {
        disposables = ListCompositeDisposable()
    }

    fun add(disposable: Disposable) =
            !disposables.isDisposed && disposables.add(disposable)

    fun remove(disposable: Disposable) =
            !disposables.isDisposed && disposables.remove(disposable)

    fun clear() {
        if (!disposables.isDisposed)
            disposables.clear()
    }


    // =========================================================================================
    // Lifecycle
    // =========================================================================================

    override fun onCreate(owner: LifecycleOwner) {
        if (opts.initOnCreate)
            initSubscriptions()
    }
    override fun onStart(owner: LifecycleOwner) {

    }
    override fun onResume(owner: LifecycleOwner) {

    }
    override fun onPause(owner: LifecycleOwner) {

    }
    override fun onStop(owner: LifecycleOwner) {

    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (opts.clearOnDestroy)
            disposables.clear()
        if (opts.disposeOnDestroy)
            disposables.dispose()
    }
}

data class LifecycleDisposableOpts(
        val initOnCreate: Boolean = false,
        val clearOnDestroy: Boolean = false,
        val disposeOnDestroy: Boolean = false
)