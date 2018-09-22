package org.amoustakos.exifstripper.ui.presenters

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import io.reactivex.disposables.Disposable
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractActions
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractView
import org.amoustakos.utils.android.LifecycleDisposableList
import org.amoustakos.utils.android.LifecycleDisposableOpts
import java.lang.ref.WeakReference

open class BasePresenter<out T : BaseContractView>
protected constructor (view: T) : DefaultLifecycleObserver, BaseContractActions {

	@set:Synchronized
	@get:Synchronized
	var disposables: LifecycleDisposableList? = null

	protected val mView: WeakReference<out T> = WeakReference(view)


	init {
		initSubscriptions()
	}

	protected fun view() = mView.get()!!

	override fun subscribeToLifecycle(lifecycle: Lifecycle) {
		lifecycle.addObserver(this)
		disposables?.subscribeToLifecycle(lifecycle)
	}

	override fun unsubscribeFromLifecycle(lifecycle: Lifecycle) {
		lifecycle.removeObserver(this)
		disposables?.unsubscribeToLifecycle(lifecycle)
	}


	// =========================================================================================
	// Subscriptions
	// =========================================================================================

	protected open fun disposeOpts(): LifecycleDisposableOpts =
			LifecycleDisposableOpts(false, false, true)

	private fun initSubscriptions() {
		if (disposables == null)
			disposables = LifecycleDisposableList(disposeOpts())
		disposables?.initSubscriptions()
	}

	fun addLifecycleDisposable(disposable: Disposable) = disposables?.add(disposable)

	fun removeLifecycleDisposable(disposable: Disposable) = disposables?.remove(disposable)

	fun clearLifecycleDisposables() {
		disposables?.clear()
	}
}
