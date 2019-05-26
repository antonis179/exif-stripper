package org.amoustakos.exifstripper.view.toolbars.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


interface ToolbarView {

	fun get(): Toolbar?

	fun setAsActionbar(activity: AppCompatActivity)
	fun toggleBackButton(enabled: Boolean)

	fun setTitle(title: String)
	fun setTitle(title: CharSequence)
	fun setTitle(titleResource: Int)

}