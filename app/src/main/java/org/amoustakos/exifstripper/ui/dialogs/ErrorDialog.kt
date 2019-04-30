package org.amoustakos.exifstripper.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ErrorDialog : DialogFragment() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return activity?.let {
			val message: String = arguments?.getString(KEY_MESSAGE) ?: ""
			// Use the Builder class for convenient dialog construction
			val builder = AlertDialog.Builder(it)
			builder.setMessage(message)
					.setPositiveButton(android.R.string.ok) { _, _ ->
						//TODO
					}
//					.setNegativeButton(android.R.string.cancel) { _, _ ->
//						//TODO
//					}
			// Create the AlertDialog object and return it
			builder.create()

		} ?: throw IllegalStateException("Activity cannot be null")
	}


	companion object {
		private const val KEY_MESSAGE = "dlg_title"

		fun newInstance(message: String): ErrorDialog {
			val bundle = Bundle()
			bundle.putString(KEY_MESSAGE, message)

			val dlg = ErrorDialog()
			dlg.arguments = bundle

			return dlg
		}

	}

}