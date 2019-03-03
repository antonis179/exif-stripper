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
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import kotlinx.android.synthetic.main.fragment_image_handling.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.file.schemehandlers.SchemeHandlerFactory
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeAdapter
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifViewModel
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.exif.Attributes
import org.amoustakos.exifstripper.utils.exif.ExifUtil

/**
 * Created by Antonis Moustakos on 2/16/2019.
 */
class ImageHandlingFragment : BaseFragment() {

	companion object {
		private const val PERMISSION_REQUEST = 10566
		private const val REQUEST_IMAGE = 10999
	}


	private lateinit var viewModel: ExifViewModel
	private var adapter: ExifAttributeAdapter? = null


	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.fragment_image_handling

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel = ViewModelProviders.of(this).get(ExifViewModel::class.java)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (!hasPermissions())
			requestPermissions()

		viewModel.imageUri.observeForever { uri ->
			run {
				loadPreview(uri)
				loadExifAttributes(uri)
			}
		}

		viewModel.adapterData.observeForever { items ->
			run {
				adapter?.replace(items)
				adapter?.notifyDataSetChanged()
			}
		}

		fab_select_image.setOnClickListener { pickImage() }

		setupRecycler()
	}

	private fun setupRecycler() {
		if (adapter == null) {
			if (viewModel.adapterData.value == null)
				viewModel.adapterData.value = mutableListOf()
			adapter = ExifAttributeAdapter(viewModel.adapterData.value!!)
		}
		rv_exif.adapter = adapter
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
			}
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	//TODO: Add full screen preview
	private fun loadPreview(uri: Uri?) {
		if (!isAdded || activity == null)
			return

		//TODO: Add placeholder
		if (uri == null) {
			Glide.with(this).clear(iv_preview)
		} else {
			Glide
					.with(this)
					.load(uri)
					.fitCenter()
					.downsample(DownsampleStrategy.CENTER_INSIDE)
					.into(iv_preview)
		}
	}

	private fun loadExifAttributes(uri: Uri?) {
		if (uri == null) {
			clearList()
			return
		}

		val schemeHandler = SchemeHandlerFactory(context!!)[uri.toString()]

		if (schemeHandler.getPath() == null) {
			clearList()
			//TODO: Error
			return
		}

		val attrMap = ExifUtil.getAttributes(schemeHandler.getPath()!!, Attributes())

		viewModel.adapterData.value = attrMap.map { ExifAttributeViewData(it.key, it.value) } as MutableList
	}

	private fun clearList() {
		viewModel.adapterData.value?.clear()
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