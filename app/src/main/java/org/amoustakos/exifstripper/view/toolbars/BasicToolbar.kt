package org.amoustakos.exifstripper.view.toolbars

import android.app.Activity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.view.base.ActivityViewComponent
import org.amoustakos.exifstripper.view.toolbars.base.ToolbarView

class BasicToolbar(private val id: Int) : ActivityViewComponent, ToolbarView {

	operator fun invoke() = get()

	private var toolbar: Toolbar? = null

	private var title: TextView? = null


	override fun setup(activity: Activity) {
		toolbar = activity.findViewById(id)
		title = toolbar?.findViewById(R.id.tvToolbarTitle)

		setAsActionbar(activity as AppCompatActivity)
	}


	override fun get() = toolbar

	override fun setAsActionbar(activity: AppCompatActivity) {
		toolbar?.let { activity.setSupportActionBar(it) }
	}

	override fun setTitle(title: String) {
		this.title?.text = title
	}

	override fun setTitle(title: CharSequence) {
		this.title?.text = title
	}

	override fun setTitle(titleResource: Int) {
		this.title?.setText(titleResource)
	}
}