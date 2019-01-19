package org.amoustakos.exifstripper.ui.presenters

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractActions
import org.amoustakos.exifstripper.ui.contracts.base.BaseContractView
import java.lang.ref.WeakReference

open class BasePresenter<out T : BaseContractView>
protected constructor (
		view: T,
		val lifecycle: Lifecycle
) : DefaultLifecycleObserver, BaseContractActions {

	protected val mView: WeakReference<out T> = WeakReference(view)


	protected fun view() = mView.get()!!



	override fun subscribeToLifecycle(lifecycle: Lifecycle) {
		lifecycle.addObserver(this)
	}

	override fun unsubscribeFromLifecycle(lifecycle: Lifecycle) {
		lifecycle.removeObserver(this)
	}

}
