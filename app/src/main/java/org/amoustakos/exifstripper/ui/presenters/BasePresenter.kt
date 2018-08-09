package org.amoustakos.exifstripper.ui.presenters

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import io.reactivex.disposables.Disposable
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractActions
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractView
import org.amoustakos.utils.android.LifecycleDisposableList
import org.amoustakos.utils.android.LifecycleDisposableOpts

open class BasePresenter<out T : BaseContractView>
protected constructor(protected val mView: T) : DefaultLifecycleObserver, BaseContractActions {

	@set:Synchronized
	@get:Synchronized
	var disposables: LifecycleDisposableList = LifecycleDisposableList(disposeOpts())


	init {
		disposables.initSubscriptions()
	}


	override fun subscribeToLifecycle(lifecycle: Lifecycle) {
		lifecycle.addObserver(this)
		disposables.subscribeToLifecycle(lifecycle)
	}

	override fun unsubscribeToLifecycle(lifecycle: Lifecycle) {
		lifecycle.removeObserver(this)
		disposables.unsubscribeToLifecycle(lifecycle)
	}


	// =========================================================================================
	// Subscriptions
	// =========================================================================================

	protected open fun disposeOpts(): LifecycleDisposableOpts =
			LifecycleDisposableOpts(false, false, true)

	private fun initSubscriptions() {
		disposables.initSubscriptions()
	}

	fun addLifecycleDisposable(disposable: Disposable) = disposables.add(disposable)

	fun removeLifecycleDisposable(disposable: Disposable) = disposables.remove(disposable)

	fun clearLifecycleDisposables() {
		disposables.clear()
	}
}
