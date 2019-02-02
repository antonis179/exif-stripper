package org.amoustakos.exifstripper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.collection.LongSparseArray
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.amoustakos.exifstripper.ExifApplication
import org.amoustakos.exifstripper.di.component.ConfigPersistentComponent
import org.amoustakos.exifstripper.di.component.DaggerConfigPersistentComponent
import org.amoustakos.exifstripper.di.component.FragmentComponent
import org.amoustakos.exifstripper.di.module.injectors.FragmentModule
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

abstract class BaseFragment: Fragment(), LifecycleOwner {

    private var mFragmentComponent: FragmentComponent? = null
    private var mFragmentId: Long = 0

    @Suppress("LeakingThis")
    private val registry: LifecycleRegistry = LifecycleRegistry(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        makeID(savedInstanceState)
        makeComponent(mFragmentId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutId(), container, false)
    }

    // =========================================================================================
    // View helpers
    // =========================================================================================

    @LayoutRes
    protected abstract fun layoutId(): Int

    // =========================================================================================
    // Injection
    // =========================================================================================

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_FRAGMENT_ID, mFragmentId)
    }

    private fun makeID(savedInstanceState: Bundle?) {
        mFragmentId = savedInstanceState?.getLong(KEY_FRAGMENT_ID) ?: NEXT_ID.getAndIncrement()
    }

    private fun makeComponent(id: Long) {
        val configPersistentComponent: ConfigPersistentComponent
        val index = sComponentsMap.indexOfKey(id)
	    val app = application() ?: return

        if (index < 0) { //component does not exist
            Timber.d("Creating new ConfigPersistentComponent id=%d", id)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(app.component)
                    .build()
            sComponentsMap.put(id, configPersistentComponent)
        } else {
            Timber.d("Reusing ConfigPersistentComponent id=%d", id)
            configPersistentComponent = sComponentsMap.get(index.toLong())
		            ?: throw NullPointerException("Persistent component not found")
        }

        mFragmentComponent =
                configPersistentComponent.fragmentComponent(FragmentModule(this))
    }

    // =========================================================================================
    // Getters
    // =========================================================================================

    fun fragmentComponent(): FragmentComponent {
        return mFragmentComponent ?:
            throw IllegalStateException("Activity component not instantiated")
    }

    fun application() = context?.let { ExifApplication[it] }

    // =========================================================================================
    // Lifecycle
    // =========================================================================================

    override fun getLifecycle() = registry

    override fun onStart() {
        super.onStart()
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

	override fun onDestroy() {
		if (activity?.isChangingConfigurations == false) {
			Timber.d("Clearing ConfigPersistentComponent id=%d", mFragmentId)
			sComponentsMap.remove(mFragmentId)
		}
		super.onDestroy()
		registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	}




    companion object {
        private const val KEY_FRAGMENT_ID = "KEY_FRAGMENT_ID"
        private val NEXT_ID = AtomicLong(0)
        private val sComponentsMap = LongSparseArray<ConfigPersistentComponent>()
    }
}