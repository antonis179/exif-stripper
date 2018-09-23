package org.amoustakos.exifstripper.view.toolbars.base

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar


interface IToolbarView {

	fun get(): Toolbar?

	fun setAsActionbar(activity: AppCompatActivity)

	fun setTitle(title: String)
	fun setTitle(title: CharSequence)
	fun setTitle(titleResource: Int)

}