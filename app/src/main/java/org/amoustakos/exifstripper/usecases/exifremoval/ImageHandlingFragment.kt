package org.amoustakos.exifstripper.usecases.exifremoval

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.android.synthetic.main.fragment_image_handling.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.utils.FileUtils

/**
 * Created by Antonis Moustakos on 2/16/2019.
 */
class ImageHandlingFragment : BaseFragment() {

	companion object {
		private const val PERMISSION_REQUEST = 10566
		private const val REQUEST_IMAGE = 10999
	}


	override fun layoutId() = R.layout.fragment_image_handling

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (!hasPermissions())
			requestPermissions()

		fabSelectImage.setOnClickListener {
			if (context == null)
				return@setOnClickListener
			startActivityForResult(
					FileUtils.createGetContentIntent(
							context!!,
							ContentType.Image.TYPE_GENERIC,
							"Select Image" //TODO: Add to strings
					),
					REQUEST_IMAGE
			)
		}
	}


	// =========================================================================================
	// Image handling
	// =========================================================================================

	override fun onActivityResult(
			requestCode: Int,
			resultCode: Int,
			@Nullable data: Intent?
	) {
		when (requestCode) {

			REQUEST_IMAGE -> if (resultCode == RESULT_OK) {

				val uri = data?.data ?: return //TODO: Show error

				if (context == null || activity == null)
					return

			}

		}
		super.onActivityResult(requestCode, resultCode, data)
	}


	// =========================================================================================
	// Permissions
	// =========================================================================================

	private fun hasPermissions() = context?.let {
		checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
				&& checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
	} ?: false

	private fun requestPermissions() {
		activity?.let {
			ActivityCompat.requestPermissions(
					it,
					arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
					PERMISSION_REQUEST
			)
		}
	}

	override fun onRequestPermissionsResult(
			requestCode: Int,
			permissions: Array<String>,
			grantResults: IntArray
	) {
		when (requestCode) {
			PERMISSION_REQUEST -> {
				if (grantResults.isEmpty()
						|| grantResults[0] != PackageManager.PERMISSION_GRANTED
						|| grantResults[1] != PackageManager.PERMISSION_GRANTED) {
					activity?.finish()
					//TODO show error
				}
			}
		}
	}
}