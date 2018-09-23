package org.amoustakos.exifstripper.ui.activities.base

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.util.LongSparseArray
import io.reactivex.disposables.Disposable
import org.amoustakos.exifstripper.ExifStripperApplication
import org.amoustakos.exifstripper.injection.component.ActivityComponent
import org.amoustakos.exifstripper.injection.component.ConfigPersistentComponent
import org.amoustakos.exifstripper.injection.component.DaggerConfigPersistentComponent
import org.amoustakos.exifstripper.injection.module.injectors.ActivityModule
import org.amoustakos.exifstripper.view.base.IActivityViewComponent
import org.amoustakos.utils.android.LifecycleDisposableList
import org.amoustakos.utils.android.LifecycleDisposableOpts
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract activity that every other Activity in this application must implement. It handles
 * creation of Dagger components and makes sure that instances of ConfigPersistentComponent survive
 * across configuration changes.
 */
abstract class BaseActivity : AppCompatActivity() {

    private var mActivityComponent: ActivityComponent? = null
    private var mActivityId: Long = 0

    @set:Synchronized
    @get:Synchronized
    var disposables: LifecycleDisposableList? = null

//    protected val rootView: View
//        get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)

    protected fun setupViewComponents(components: List<IActivityViewComponent>) {
	    components.forEach { setupViewComponent(it) }
    }

	protected fun setupViewComponent(component: IActivityViewComponent) {
		component.setup(this)
	}


    // =========================================================================================
    // Overridden
    // =========================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        makeID(savedInstanceState)
        makeComponents(mActivityId)
	    initSubscriptions()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_ACTIVITY_ID, mActivityId)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Timber.d("Clearing ConfigPersistentComponent id=%d", mActivityId)
            sComponentsMap.remove(mActivityId)
        }
        super.onDestroy()

	    unsubscribeFromLifecycle(lifecycle)
    }

    // =========================================================================================
    // Injection
    // =========================================================================================

    private fun makeID(savedInstanceState: Bundle?) {
        mActivityId = savedInstanceState?.getLong(KEY_ACTIVITY_ID) ?: NEXT_ID.getAndIncrement()
    }

    private fun makeComponents(activityID: Long) {
        val configPersistentComponent: ConfigPersistentComponent
        val index = sComponentsMap.indexOfKey(activityID)

        if (index < 0) { //component does not exist
            Timber.d("Creating new ConfigPersistentComponent id=%d", activityID)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(application().component)
                    .build()
            sComponentsMap.put(activityID, configPersistentComponent)
        } else {
            Timber.d("Reusing ConfigPersistentComponent id=%d", activityID)
            configPersistentComponent = sComponentsMap.get(index.toLong())
        }

        mActivityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
    }


    // =========================================================================================
    // View helpers
    // =========================================================================================

    @LayoutRes
    protected abstract fun layoutId(): Int


	// =========================================================================================
	// Subscriptions
	// =========================================================================================

	private fun subscribeToLifecycle(lifecycle: Lifecycle) {
		disposables?.subscribeToLifecycle(lifecycle)
	}

	private fun unsubscribeFromLifecycle(lifecycle: Lifecycle) {
		disposables?.unsubscribeToLifecycle(lifecycle)
	}

	protected open fun disposeOpts(): LifecycleDisposableOpts =
			LifecycleDisposableOpts(false, false, true)

	private fun initSubscriptions() {
		if (disposables == null)
			disposables = LifecycleDisposableList(disposeOpts())
		disposables?.initSubscriptions()
		subscribeToLifecycle(lifecycle)
	}

	protected fun addLifecycleDisposable(disposable: Disposable) = disposables?.add(disposable)

	protected fun removeLifecycleDisposable(disposable: Disposable) = disposables?.remove(disposable)

	protected fun clearLifecycleDisposables() {
		disposables?.clear()
	}

    // =========================================================================================
    // Getters
    // =========================================================================================

    @Throws(IllegalStateException::class)
    fun activityComponent(): ActivityComponent {
        return mActivityComponent ?:
                throw IllegalStateException("Activity component not instantiated")
    }

    fun application(): ExifStripperApplication {
        return ExifStripperApplication[this]
    }

    companion object {
        private const val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID = AtomicLong(0)
        private val sComponentsMap = LongSparseArray<ConfigPersistentComponent>()
    }

}
