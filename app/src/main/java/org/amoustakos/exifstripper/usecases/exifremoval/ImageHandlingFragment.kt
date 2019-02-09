package org.amoustakos.exifstripper.usecases.exifremoval

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.fragments.BaseFragment

/**
 * Created by Antonis Moustakos on 2/16/2019.
 */
class ImageHandlingFragment : BaseFragment() {

	companion object {
		private const val PERMISSION_REQUEST = 10566
	}


	override fun layoutId() = R.layout.fragment_image_handling

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (!hasReadPermission())
			requestReadPermission()
	}


	// =========================================================================================
	// Permissions
	// =========================================================================================

	private fun hasReadPermission() = context?.let {
		checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
	} ?: false

	private fun requestReadPermission() {
		activity?.let {
			ActivityCompat.requestPermissions(
					it,
					arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
					PERMISSION_REQUEST
			)
		}
	}

	override fun onRequestPermissionsResult(
			requestCode: Int,
			permissions: Array<String>, grantResults: IntArray
	) {
		when (requestCode) {
			PERMISSION_REQUEST -> {
				if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
					activity?.finish()
			}
		}
	}
}