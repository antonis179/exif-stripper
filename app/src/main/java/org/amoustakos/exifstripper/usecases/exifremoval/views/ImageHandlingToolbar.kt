package org.amoustakos.exifstripper.usecases.exifremoval.views

import android.app.Activity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar


class ImageHandlingToolbar(id: Int) : BasicToolbar(id) {

	private lateinit var share: View
	private lateinit var save: View


	override fun setup(activity: Activity) {
		super.setup(activity)

		share = toolbar!!.findViewById(R.id.iv_share)
		save = toolbar!!.findViewById(R.id.iv_save)
	}


	fun toggleShare(enabled: Boolean) {
		share.visibility = if (enabled) VISIBLE else GONE
	}

	fun toggleSave(enabled: Boolean) {
		save.visibility = if (enabled) VISIBLE else GONE
	}


	fun setShareListener(action: () -> Unit) {
		share.setOnClickListener { action() }
	}

	fun setSaveListener(action: () -> Unit) {
		save.setOnClickListener { action() }
	}

}