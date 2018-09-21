package org.amoustakos.exifstripper.view.toolbars

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import org.amoustakos.exifstripper.view.base.IActivityViewComponent
import org.amoustakos.exifstripper.view.toolbars.base.IToolbarView

class BasicToolbar(val id: Int): IActivityViewComponent, IToolbarView {

	private var toolbar: Toolbar? = null


	override fun setup(activity: Activity) {
		toolbar = activity.findViewById(id)
	}



	override fun setAsActionbar(activity: AppCompatActivity) {
		if (toolbar != null)
			activity.setSupportActionBar(toolbar)
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