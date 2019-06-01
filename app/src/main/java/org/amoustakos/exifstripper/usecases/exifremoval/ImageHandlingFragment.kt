package org.amoustakos.exifstripper.usecases.exifremoval

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_image_handling.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.ResponseWrapper
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.ui.dialogs.ErrorDialog
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeAdapter
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifViewModel
import org.amoustakos.exifstripper.utils.ExifFile
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.view.toolbars.ImageHandlingToolbar
import org.amoustakos.utils.android.rx.disposer.disposeBy
import org.amoustakos.utils.android.rx.disposer.onDestroy
import timber.log.Timber


class ImageHandlingFragment : BaseFragment() {

	companion object {
		private const val PERMISSION_REQUEST = 10566
		private const val REQUEST_IMAGE = 10999
		private const val SAVE_IMAGE = 11000
	}


	private lateinit var viewModel: ExifViewModel
	private var adapter: ExifAttributeAdapter? = null

	private val clickListener: View.OnClickListener = View.OnClickListener { pickImage() }

	private val toolbar = ImageHandlingToolbar(R.id.toolbar)

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.fragment_image_handling

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel = ViewModelProviders.of(this).get(ExifViewModel::class.java)

		if (viewModel.exifFile.value == null)
			viewModel.exifFile.value = ExifFile(context!!)
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		setHasOptionsMenu(true)
		toolbar.setTitle(R.string.app_name)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	private fun toggleAppbar() {
		if (viewModel.exifFile.value?.isLoaded != true) {
			val p = ctToolbar.layoutParams as AppBarLayout.LayoutParams
			p.scrollFlags = SCROLL_FLAG_NO_SCROLL
			ctToolbar.layoutParams = p
		} else {
			val p = ctToolbar.layoutParams as AppBarLayout.LayoutParams
			p.scrollFlags = SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or SCROLL_FLAG_SCROLL
			ctToolbar.layoutParams = p
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setupToolbar()

		if (!hasPermissions())
			requestPermissions()

		viewModel.exifFile.value?.exifAttributesSubject
				?.observeOn(Schedulers.computation())
				?.doOnNext {
					refreshAdapter(it, false)
				}
				?.observeOn(AndroidSchedulers.mainThread())
				?.doOnNext { refreshUI() }
				?.doOnError(Timber::e)
				?.onErrorReturn { mutableListOf() }
				?.disposeBy(lifecycle.onDestroy)
				?.subscribe()

		iv_preview.setOnClickListener(clickListener)
		tv_select_image.setOnClickListener(clickListener)
		btn_remove_all.setOnClickListener { removeExifData() }
		toolbar.setShareListener { shareImage() }
		toolbar.setSaveListener { saveImage() }
//		srl_refresh.setOnRefreshListener { refresh() }

		setupRecycler()
		refreshUI()
	}

	private fun refreshUI() {
		setRefreshing(true)
		loadPreview()
		toggleRemoveAllButton()
		restoreActions()
		notifyAdapter()
		toggleAppbar()
		setRefreshing(false)
	}

	private fun refreshAdapter(items: List<ExifAttributeViewData>, notify: Boolean) {
		adapter?.replace(items)
		if (notify)
			notifyAdapter()
	}

	private fun notifyAdapter() {
		Single.fromCallable { }
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe { adapter?.notifyDataSetChanged() }
				.disposeBy(lifecycle.onDestroy)
				.subscribe()
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
//		srl_refresh.isRefreshing = refreshing
	}

	private fun shareImage() {
		try {
			viewModel.exifFile.value?.shareImage(getString(R.string.share_with))
		} catch (exc: Exception) {
			Timber.e(exc)
			showError(getString(R.string.error_msg_oops))
		}
	}

	private fun saveImage() {
		try {
			val intent = viewModel.exifFile.value?.saveImageIntent(getString(R.string.save_as))
			startActivityForResult(intent, SAVE_IMAGE)
		} catch (exc: Exception) {
			Timber.e(exc)
		}
	}

	private fun isImageLoaded() = viewModel.exifFile.value?.isLoaded ?: false


	private fun showError(message: String) {
		viewModel.errorDialog.value?.dismiss()
		viewModel.errorDialog.value = ErrorDialog.newInstance(message)
		viewModel.errorDialog.value?.show(childFragmentManager, null)
	}

	// =========================================================================================
	// Views
	// =========================================================================================

	private fun toggleRemoveAllButton() {
		btn_remove_all.visibility = if (isImageLoaded()) VISIBLE else GONE
	}

	private fun removeExifData() {
		viewModel.exifFile.value?.removeExifData()
	}

	private fun toggleActions(show: Boolean) {
		toolbar.toggleShare(show)
		toolbar.toggleSave(show)
	}

	private fun restoreActions() {
		toggleActions(isImageLoaded())
	}

	// =========================================================================================
	// Image handling
	// =========================================================================================

	private fun reset() {
		toggleActions(false)
		viewModel.exifFile.value?.reset()
//		srl_refresh.isRefreshing = false
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

			SAVE_IMAGE -> {
				if (data != null) {
					val uri = data.data ?: return

					Single.fromCallable {}
							.observeOn(Schedulers.computation())
							.map { viewModel.exifFile.value?.store(uri) }
							.observeOn(AndroidSchedulers.mainThread())
							.doOnError {
								Timber.e(it)
								showError(getString(R.string.error_msg_storage_issue))
							}
							.onErrorReturn { }
							.disposeBy(lifecycle.onDestroy)
							.subscribe()
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun handleUri(data: Intent?) {
		setRefreshing(true)

		val uri = data?.data
		if (uri == null) {
			reset()
			return
		}

		Single.fromCallable {}
				.observeOn(Schedulers.computation())
				.map { viewModel.exifFile.value?.load(uri) ?: ResponseWrapper() }
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSuccess {
					val message: String? = when (it!!.value) {
						ExifFile.LoadResult.ContextError -> getString(R.string.error_msg_oops)
						ExifFile.LoadResult.UriError -> getString(R.string.error_msg_uri_issue_external)
						ExifFile.LoadResult.FormatError -> getString(R.string.error_msg_format)
						ExifFile.LoadResult.CacheError -> getString(R.string.error_msg_oops)
						ExifFile.LoadResult.Success -> null
						null -> null
					}

					message?.let { msg -> showError(msg) }
				}
				.doOnError {
					Timber.e(it)
					reset()
					showError(getString(R.string.error_msg_oops))
				}
				.onErrorReturn { ResponseWrapper() }
				.disposeBy(lifecycle.onDestroy)
				.subscribe()
	}

	private fun refresh() {
		Single.fromCallable {}
				.observeOn(Schedulers.computation())
				.map { viewModel.exifFile.value?.loadExifAttributes() }
				.observeOn(AndroidSchedulers.mainThread())
				.doOnError(Timber::e)
				.onErrorReturn {}
				.disposeBy(lifecycle.onDestroy)
				.subscribe()
	}

	//TODO: Add full screen preview
	private fun loadPreview() {
		if (!isAdded || activity == null)
			return

		val path = viewModel.exifFile.value?.getPath()

		Glide
				.with(this)
				.load(path)
				.placeholder(R.drawable.ic_placeholder)
				.thumbnail(0.25f)
//				.asBitmap()
//				.format(DecodeFormat.PREFER_ARGB_8888)
//				.downsample(DownsampleStrategy.AT_MOST)
				.override(750)
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.dontAnimate()
				.into(iv_preview)


		tv_select_image.text =
				if (path == null)
					getString(R.string.select_image)
				else
					viewModel.exifFile.value?.getName()
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