package org.amoustakos.exifstripper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import org.amoustakos.exifstripper.ExifApplication
import org.amoustakos.exifstripper.view.base.ActivityViewComponent

abstract class BaseFragment: Fragment() {

    protected fun setupViewComponents(components: List<ActivityViewComponent>) {
        components.forEach { setupViewComponent(it) }
    }

    protected fun setupViewComponent(component: ActivityViewComponent) {
        activity?.let { component.setup(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

}