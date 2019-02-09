package org.amoustakos.exifstripper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.amoustakos.exifstripper.ExifApplication

abstract class BaseFragment: Fragment(), LifecycleOwner {

    @Suppress("LeakingThis")
    private val registry: LifecycleRegistry = LifecycleRegistry(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
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
    // Getters
    // =========================================================================================

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
		super.onDestroy()
		registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	}

}