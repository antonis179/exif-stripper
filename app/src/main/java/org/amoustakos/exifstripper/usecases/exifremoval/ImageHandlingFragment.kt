package org.amoustakos.exifstripper.usecases.exifremoval

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_image_handling.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.ResponseWrapper
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.file.schemehandlers.SchemeHandlerFactory
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeAdapter
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifViewModel
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.exif.Attributes
import org.amoustakos.exifstripper.utils.exif.ExifUtil
import timber.log.Timber

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

		viewModel.imageUri.observeForever { run { refreshUI() } }
		viewModel.adapterData.observeForever { items -> run { refreshAdapter(items) } }

		fab_select_image.setOnClickListener { pickImage() }
		btn_remove_all.setOnClickListener { removeExifData() }

		setupRecycler()
	}

	private fun refreshUI() {
		loadPreview()
		loadExifAttributes()
		toggleRemoveAllButton()
	}

	private fun refreshAdapter(items: MutableList<ExifAttributeViewData>): Unit? {
		adapter?.replace(items)
		return adapter?.notifyDataSetChanged()
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
	// Views
	// =========================================================================================

	private fun toggleRemoveAllButton() {
		btn_remove_all.visibility = if (null == viewModel.imageUri.value) GONE else VISIBLE
	}

	private fun removeExifData() {
		if (context == null) return
		val uri = viewModel.imageUri.value ?: return
		val schemeHandler = SchemeHandlerFactory(context!!)[uri.toString()]

		try {
			ExifUtil.removeAttributes(schemeHandler.getPath()!!, Attributes())
		} catch (e: Exception) {
			Timber.e(e)
			//TODO: Show error
		}

		refreshUI()
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

				val uri = data?.data
				if (uri == null) {
					viewModel.imageUri.value = null
					return
				}

				if (!SchemeHandlerFactory(context!!).isSupported(uri.toString())) {
					//TODO: Error
					return
				}

				val handler = SchemeHandlerFactory(context!!)[uri.toString()]
				val stream = handler.getInputStream()
				if (stream == null) {
					//TODO: Show error
					return
				}

				val name = "cache.${handler.getFileExtension()}"
				val cache = context?.externalCacheDir?.toString()

				Observable
						.just(ResponseWrapper(FileUtils.createFile(stream, "$cache/$name")))
						.observeOn(Schedulers.computation())
						.subscribeOn(AndroidSchedulers.mainThread())
						.doOnError {
							Timber.e(it)
							//TODO: Show error
						}
						.onErrorReturn { ResponseWrapper() }
						.observeOn(AndroidSchedulers.mainThread())
						.doOnNext {
							if (it.value == null) {
								//TODO: Show Error
							} else {
								val path = SchemeHandlerFactory(context!!)[it.value!!.absolutePath].getPath()
								viewModel.imageUri.value = Uri.parse(path)
							}
						}
						.subscribe()
			}
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	//TODO: Add full screen preview
	private fun loadPreview() {
		if (!isAdded || activity == null)
			return

		val uri = viewModel.imageUri.value

		//TODO: Add placeholder
		if (uri == null) {
			Glide.with(this).clear(iv_preview)
		} else {
			Glide
					.with(this)
					.load(uri.path)
					.override(2000)
					.downsample(DownsampleStrategy.FIT_CENTER)
					.fitCenter()
					.into(iv_preview)
		}
	}

	private fun loadExifAttributes() {
		val uri = viewModel.imageUri.value

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