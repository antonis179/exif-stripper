package org.amoustakos.exifstripper.view.toolbars

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.amoustakos.exifstripper.view.base.ActivityViewComponent
import org.amoustakos.exifstripper.view.toolbars.base.ToolbarView

class BasicToolbar(private val id: Int) : ActivityViewComponent, ToolbarView {

	operator fun invoke() = get()

	private var toolbar: Toolbar? = null


	override fun setup(activity: Activity) {
		toolbar = activity.findViewById(id)
	}


	override fun get() = toolbar

	override fun setAsActionbar(activity: AppCompatActivity) {
		toolbar?.let { activity.setSupportActionBar(it) }
	}

	override fun setTitle(title: String) {
		toolbar?.title = title
	}

	override fun setTitle(title: CharSequence) {
		toolbar?.title = title
	}

	override fun setTitle(titleResource: Int) {
		toolbar?.setTitle(titleResource)
	}
}