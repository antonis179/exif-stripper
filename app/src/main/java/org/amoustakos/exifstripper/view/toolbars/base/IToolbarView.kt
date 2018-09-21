package org.amoustakos.exifstripper.view.toolbars.base

import android.support.v7.app.AppCompatActivity


interface IToolbarView {

	fun setAsActionbar(activity: AppCompatActivity)

	fun setTitle(title: String)
	fun setTitle(title: CharSequence)
	fun setTitle(titleResource: Int)

}