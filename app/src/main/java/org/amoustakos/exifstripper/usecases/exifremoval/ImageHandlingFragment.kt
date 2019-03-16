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
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.google.android.material.snackbar.Snackbar
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


class ImageHandlingFragment : BaseFragment() {

	companion object {
		private const val PERMISSION_REQUEST = 10566
		private const val REQUEST_IMAGE = 10999
	}


	private lateinit var viewModel: ExifViewModel
	private var adapter: ExifAttributeAdapter? = null

	private val clickListener: View.OnClickListener = View.OnClickListener { pickImage() }


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

		iv_preview.setOnClickListener(clickListener)
		tv_select_image.setOnClickListener(clickListener)
		btn_remove_all.setOnClickListener { removeExifData() }
		srl_refresh.setOnRefreshListener { refreshUI() }

		setupRecycler()
	}

	private fun refreshUI() {
		setRefreshing(true)
		loadPreview()
		toggleRemoveAllButton()
		restoreShareAction()
		loadExifAttributes()
				.observeOn(AndroidSchedulers.mainThread())
				.doOnComplete { setRefreshing(false) }
				.subscribe()
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

	@Synchronized
	private fun setRefreshing(refreshing: Boolean) {
		if (srl_refresh.isRefreshing != refreshing)
			srl_refresh.isRefreshing = refreshing
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
		showShareAction()
	}

	private fun showShareAction() {
		if (viewModel.snackbar.value != null) hideShareAction()

		viewModel.snackbar.value = Snackbar.make(cl_content, R.string.save_as, Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.save) {
					val uri = viewModel.imageUri.value ?: return@setAction
					val ctx = context ?: return@setAction
					val mime = SchemeHandlerFactory(ctx)[uri.toString()].getContentType()
					val intent = FileUtils.shareFileIntent(uri, mime, getString(R.string.save_as))
					ctx.startActivity(intent)
				}
		viewModel.snackbar.value?.show()
	}

	private fun hideShareAction() {
		viewModel.snackbar.value?.dismiss()
		viewModel.snackbar.value = null
	}

	private fun restoreShareAction() {
		viewModel.snackbar.value?.show()
	}

	// =========================================================================================
	// Image handling
	// =========================================================================================

	private fun resetUI() {
		hideShareAction()
		viewModel.imageUri.value = null
	}

	override fun onActivityResult(
			requestCode: Int,
			resultCode: Int,
			@Nullable data: Intent?
	) {
		when (requestCode) {
			REQUEST_IMAGE -> if (resultCode == RESULT_OK) {
				if (context == null || activity == null) return
				handleUri(data)
			}
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun handleUri(data: Intent?) {
		setRefreshing(true)

		val uri = data?.data
		if (uri == null) {
			resetUI()
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
			resetUI()
			return
		}

		val name = "cache.${handler.getFileExtension()}"
		val cache = context?.externalCacheDir?.toString()

		Observable
				.just(ResponseWrapper(FileUtils.createFile(stream, "$cache/$name")))
				.observeOn(Schedulers.computation())
				.doOnNext {
					if (it.value == null) {
						resetUI()
						//TODO: Show Error
					} else {
						val path = SchemeHandlerFactory(context!!)[it.value!!.absolutePath].getPath()
						viewModel.imageUri.postValue(Uri.parse(path))
					}
				}
				.subscribeOn(AndroidSchedulers.mainThread())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnError {
					Timber.e(it)
					resetUI()
					//TODO: Show error
				}
				.onErrorReturn { ResponseWrapper() }
				.subscribe()
	}

	//TODO: Add full screen preview
	private fun loadPreview() {
		if (!isAdded || activity == null)
			return

		val uri = viewModel.imageUri.value

		Glide
				.with(this)
				.asBitmap()
				.format(DecodeFormat.PREFER_ARGB_8888)
				.load(uri?.path)
				.placeholder(R.drawable.ic_placeholder)
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.dontAnimate()
				.override(2000)
				.downsample(DownsampleStrategy.FIT_CENTER)
//					.fitCenter()
				.into(iv_preview)


		tv_select_image.text =
				if (uri == null)
					getString(R.string.select_image)
				else
					SchemeHandlerFactory(context!!)[uri.toString()].getName()
	}

	private fun loadExifAttributes() = Observable
			.just(ResponseWrapper(viewModel.imageUri.value))
			.subscribeOn(AndroidSchedulers.mainThread())
			.observeOn(Schedulers.computation())
			.doOnError {
				Timber.e(it)
				//TODO: Show error
			}
			.onErrorReturn { ResponseWrapper() }
			.map {
				if (it.value == null) {
					clearList()
					return@map ResponseWrapper<MutableList<ExifAttributeViewData>>()
				} else {
					val uri = it.value
					val schemeHandler = SchemeHandlerFactory(context!!)[uri.toString()]

					if (schemeHandler.getPath() == null) {
						clearList()
						//TODO: Error
						return@map ResponseWrapper<MutableList<ExifAttributeViewData>>()
					}

					val attrMap = ExifUtil.getAttributes(schemeHandler.getPath()!!, Attributes())
					ResponseWrapper(attrMap.map { entry ->
						ExifAttributeViewData(entry.key, entry.value)
					} as MutableList)
				}
			}
			.doOnNext {
				val list = it.value ?: arrayListOf()
				viewModel.adapterData.postValue(list)
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