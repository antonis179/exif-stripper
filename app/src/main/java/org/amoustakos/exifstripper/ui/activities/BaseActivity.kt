package org.amoustakos.exifstripper.ui.activities

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import org.amoustakos.exifstripper.ExifApplication
import org.amoustakos.exifstripper.view.base.ActivityViewComponent

abstract class BaseActivity : AppCompatActivity() {


    protected fun setupViewComponents(components: List<ActivityViewComponent>) {
	    components.forEach { setupViewComponent(it) }
    }

	protected fun setupViewComponent(component: ActivityViewComponent) {
		component.setup(this)
	}

    // =========================================================================================
    // Overridden
    // =========================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
    }

    // =========================================================================================
    // View helpers
    // =========================================================================================

    @LayoutRes
    protected abstract fun layoutId(): Int

    // =========================================================================================
    // Getters
    // =========================================================================================

    fun application(): ExifApplication {
        return ExifApplication[this]
    }

}
