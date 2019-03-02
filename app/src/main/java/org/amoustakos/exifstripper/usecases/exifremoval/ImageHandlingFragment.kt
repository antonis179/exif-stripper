package org.amoustakos.exifstripper.usecases.exifremoval

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_image_handling.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifViewModel
import org.amoustakos.exifstripper.utils.FileUtils

/**
 * Created by Antonis Moustakos on 2/16/2019.
 */
class ImageHandlingFragment : BaseFragment() {

	companion object {
		private const val PERMISSION_REQUEST = 10566
		private const val REQUEST_IMAGE = 10999
	}


	private lateinit var viewModel: ExifViewModel



	override fun layoutId() = R.layout.fragment_image_handling

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel = ViewModelProviders.of(this).get(ExifViewModel::class.java)

		viewModel.imageUri.observeForever { uri -> loadPreview(uri) }
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (!hasPermissions())
			requestPermissions()

		fabSelectImage.setOnClickListener { pickImage() }
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
				if (context == null || activity == null) return
				viewModel.imageUri.value = data?.data
//				if (data?.data == null) //TODO: error

//				val schemeHandler = SchemeHandlerFactory(context!!)[data?.data!!.toString()]
//				ExifInterface(schemeHandler.getPath()!!)
			}
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	//TODO: Add full screen preview
	private fun loadPreview(uri: Uri?) {
		//TODO: Add placeholder
		if (uri == null) {
			Glide.with(this).clear(iv_preview)
		} else {
			Glide
					.with(this)
					.load(uri)
					.fitCenter()
					//					.placeholder(R.drawable.loading_spinner)
					.into(iv_preview)
		}
	}


	private fun pickImage() {
		context?.let {
			startActivityForResult(
					FileUtils.createGetContentIntent(
							it,
							ContentType.Image.TYPE_GENERIC,
							getString(R.string.title_image_select)
					),
					REQUEST_IMAGE
			)
		}
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