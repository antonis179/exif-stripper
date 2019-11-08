package org.amoustakos.exifstripper.usecases.exifremoval

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.crashlytics.android.Crashlytics
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_image_handling.*
import kotlinx.android.synthetic.main.include_empty_screen.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.io.ResponseWrapper
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.ui.dialogs.ErrorDialog
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeAdapter
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeViewHolder
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifImagePagerAdapter
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifImageViewData
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.usecases.exifremoval.views.ImageHandlingToolbar
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.exif.ExifFile
import org.amoustakos.exifstripper.view.recycler.ClickEvent
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.Type
import org.amoustakos.utils.android.kotlin.Do
import org.amoustakos.utils.android.rx.disposer.disposeBy
import org.amoustakos.utils.android.rx.disposer.onDestroy
import timber.log.Timber


class ImageHandlingFragment : BaseFragment() {

	private lateinit var viewModel: ExifViewModel

	private var adapter: ExifImagePagerAdapter? = null
	private var attrAdapter: ExifAttributeAdapter? = null

	private val imageSelectionListener: View.OnClickListener = View.OnClickListener { pickImage() }
	private val deletionPublisher: PublishSubject<ClickEvent<ExifAttributeViewData>> = PublishSubject.create()

	private var attrSubscription: Disposable? = null

	private val toolbar = ImageHandlingToolbar(R.id.toolbar)

	private val updaterThread = Schedulers.newThread()

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.fragment_image_handling

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel = ViewModelProvider(
				this,
				SavedStateViewModelFactory(application()!!, this)
		).get(ExifViewModel::class.java)

		if (viewModel.exifFiles.value == null)
			viewModel.exifFiles.value = mutableListOf()

		if (savedInstanceState == null) {
			arguments?.getParcelableArrayList<Uri>(KEY_URI)?.let {
				if (context == null || activity == null) return
				handleUris(it, false)
			}
		}
	}

	private fun notifyStored() {
		Snackbar.make(content, R.string.notif_stored, 5000).show()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setupToolbar()

		if (!hasPermissions())
			requestPermissions()

		tvSelectImages.setOnClickListener(imageSelectionListener)
		abSelectImages.setOnClickListener(imageSelectionListener)
		vpImageCollection.setOnClickListener(imageSelectionListener)
		btn_remove_all.setOnClickListener { removeAllExifData() }
		toolbar.setShareListener { shareImage() }
		toolbar.setSaveListener { saveImage() }

		deletionPublisher
				.observeOn(Schedulers.io())
				.doOnNext {
					viewModel.exifFiles.value?.get(vpImageCollection.currentItem)?.removeAttribute(context!!, it.item.title)
				}
				.doOnError(Timber::e)
				.map { }
				.onErrorReturn { }
				.disposeBy(onDestroy)
				.subscribe()

		setupViewPager()
		setupRecycler()
		updateAdapters()
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		setHasOptionsMenu(true)
		toolbar.setTitle(R.string.app_name)
	}

	private fun setupViewPager() {
		if (adapter == null) {
			if (viewModel.adapterData.value == null)
				viewModel.adapterData.value = mutableListOf()

			adapter = ExifImagePagerAdapter(viewModel.adapterData.value!!)
		}
		vpImageCollection.adapter = adapter
		ciImageIndicator.setViewPager(vpImageCollection)
		adapter?.registerAdapterDataObserver(ciImageIndicator.adapterDataObserver)

		vpImageCollection!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				super.onPageSelected(position)
				onImageSelected(position)
			}
		})
	}

	private fun setState() {
		//FIXME: Move to view state class
		//TODO: Add loading state
		if (isImageLoaded()) {
			viewContent.visibility = VISIBLE
			rv_exif.visibility = VISIBLE
			viewEmpty.visibility = GONE
			viewLoading.visibility = GONE
		} else {
			viewContent.visibility = GONE
			rv_exif.visibility = GONE
			viewEmpty.visibility = VISIBLE
			viewLoading.visibility = GONE
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	private fun toggleAppbar() {
		if (!isImageLoaded()) {
			val p = ctToolbar.layoutParams as AppBarLayout.LayoutParams
			p.scrollFlags = SCROLL_FLAG_NO_SCROLL
			ctToolbar.layoutParams = p
		} else {
			val p = ctToolbar.layoutParams as AppBarLayout.LayoutParams
			p.scrollFlags = SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or SCROLL_FLAG_SCROLL
			ctToolbar.layoutParams = p
		}
	}

	private fun refreshUI() {
		toggleRemoveAllButton()
		restoreActions()
		notifyAdapters()
		toggleAppbar()
		setState()
	}

	private fun updateAdapters() {
		Single.fromCallable { viewModel.exifFiles.value!! }
				.observeOn(updaterThread)
				.map {
					if (context == null) {
						mutableListOf()
					} else {
						it.filter { file -> file.getPath(context!!) != null }.map { exifFile ->
							ExifImageViewData(exifFile.getPath(context!!)!!)
						}
					}
				}
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSuccess {
					refreshImageAdapter(it)
					refreshUI()
				}
				.doOnError {
					Timber.e(it)
					Crashlytics.logException(it)
				}
				.onErrorReturn { mutableListOf() }
				.disposeBy(lifecycle.onDestroy)
				.subscribe()
	}

	private fun refreshImageAdapter(items: List<ExifImageViewData>) {
		attrSubscription?.dispose()
		attrSubscription = null
		adapter?.replace(items)
	}

	private fun refreshAttributeAdapter(items: List<ExifAttributeViewData>) {
		attrAdapter?.replace(items)
	}

	private fun notifyAdapters() {
		adapter?.notifyDataSetChanged()
		if (!viewModel.exifFiles.value.isNullOrEmpty() && attrSubscription == null)
			onImageSelected(0)
		attrAdapter?.notifyDataSetChanged()
	}

	private fun setupRecycler() {
		if (viewModel.attrAdapterData.value == null)
			viewModel.attrAdapterData.value = mutableListOf()

		attrAdapter = ExifAttributeAdapter(viewModel.attrAdapterData.value!!, listOf(
				PublisherItem(deletionPublisher, Type.CLICK, ExifAttributeViewHolder.DELETION_PUBLISHER_ID)
		))

		rv_exif.adapter = attrAdapter
	}

	private fun shareImage() {
		Do.safe({
			context?.let { ctx ->
				val uris: MutableList<Uri> = arrayListOf()

				viewModel.exifFiles.value?.forEach { uris.add(it.getUri(ctx)) }

				if (uris.isNotEmpty()) {
					val intent = FileUtils.shareMultipleFilesIntent(
							uris as ArrayList<Uri>,
							ContentType.Image.TYPE_GENERIC,
							getString(R.string.share_with)
					)

					startActivity(intent)
				}
			}
		}, {
			Timber.e(it)
			Crashlytics.logException(it)
		})
	}

	private fun saveImage() {
		Do.safe({
			context?.let { ctx ->
				Single.fromCallable {}
						.observeOn(Schedulers.computation())
						.map { viewModel.exifFiles.value?.forEach { it.saveToSigned(ctx) } }
						.observeOn(AndroidSchedulers.mainThread())
						.doOnSuccess {
							reset()
							notifyStored()
						}
						.doOnError {
							Timber.e(it)
							Crashlytics.logException(it)
							showError(getString(R.string.error_msg_storage_issue))
						}
						.onErrorReturn { }
						.disposeBy(lifecycle.onDestroy)
						.subscribe()
			}
		}, {
			Timber.e(it)
			Crashlytics.logException(it)
		})
	}

	private fun isImageLoaded() = viewModel.exifFiles.value?.let {
		!it.isNullOrEmpty() && it.filter { file -> !file.isLoaded }.isNullOrEmpty()
	} ?: false


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

	private fun removeAllExifData() {
		viewModel.exifFiles.value?.forEach { removeExifData(it) }
	}

	private fun removeExifData(file: ExifFile) {
		context?.let { file.removeExifData(it) }
	}

	private fun toggleActions(show: Boolean) {
		toolbar.toggleShare(show)
		toolbar.toggleSave(show)
	}

	private fun restoreActions() {
		toggleActions(isImageLoaded())
	}

	private fun reset() {
		Do safe {
			context?.let { ExifFile.clearCache(it) }
			toggleActions(false)
			viewModel.exifFiles.value?.clear()
			updateAdapters()
		}
	}

	// =========================================================================================
	// Image handling
	// =========================================================================================

	private fun onImageSelected(position: Int) {
		Do.safe({
			if (viewModel.exifFiles.value.isNullOrEmpty())
				return

			val image = viewModel.exifFiles.value!![position]
			attrSubscription?.dispose()

			attrSubscription = image.exifAttributesSubject
					.observeOn(Schedulers.computation())
					.map {
						it.map { exifData -> ExifAttributeViewData(exifData.title, exifData.value) }
					}
					.observeOn(AndroidSchedulers.mainThread())
					.doOnNext {
						refreshAttributeAdapter(it)
					}
					.doOnNext { refreshUI() }
					.doOnError(Timber::e)
					.onErrorReturn { mutableListOf() }
					.disposeBy(lifecycle.onDestroy)
					.subscribe()

			context?.let { image.loadExifAttributes(it) }
		}, {
			Timber.e(it)
			Crashlytics.logException(it)
		})
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
		when (requestCode) {
			REQUEST_IMAGE -> if (resultCode == Activity.RESULT_OK) {
				if (context == null || activity == null) return
				handleUris(data, false)
			}
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun handleUris(data: Intent?, ignoreError: Boolean = false) {
		val uris = mutableListOf<Uri>()

		if (data?.clipData?.itemCount ?: 0 < 1)
			data?.data?.let { uris.add(it) }

		for (i in 0 until (data?.clipData?.itemCount ?: -1))
			uris.add(data!!.clipData!!.getItemAt(i).uri)

		handleUris(uris, ignoreError)
	}

	private fun handleUris(uris: List<Uri>, ignoreError: Boolean) {
		reset()
		Single.fromCallable {}
				.observeOn(Schedulers.computation())
				.map {
					context?.let {
						var response: ResponseWrapper<ExifFile.LoadResult> = ResponseWrapper()
						uris.forEach { uri ->
							val exifFile = ExifRemovalFile()
							val innerResponse = exifFile.load(uri, it)

							viewModel.exifFiles.value?.add(exifFile)
							if (innerResponse.value != ExifFile.LoadResult.Success)
								response = ResponseWrapper(innerResponse.value)
						}
						response
					}
				}
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

					message?.let { msg ->
						if (!ignoreError) showError(msg)
					} ?: updateAdapters()
				}
				.doOnError {
					Timber.e(it)
					if (!ignoreError) {
						reset()
						showError(getString(R.string.error_msg_oops))
					}
				}
				.onErrorReturn { ResponseWrapper() }
				.disposeBy(lifecycle.onDestroy)
				.subscribe()
	}

	private fun pickImage() {
		context?.let {
			startActivityForResult(
					FileUtils.createGetContentIntent(
							it,
							ContentType.Image.TYPE_GENERIC,
							title = getString(R.string.title_image_select),
							allowMultiple = true
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


	companion object {
		private const val PERMISSION_REQUEST = 10566
		private const val REQUEST_IMAGE = 10999

		private const val KEY_URI = "key_uri"


		fun newInstance(uris: ArrayList<Uri>? = null): ImageHandlingFragment {
			val frag = ImageHandlingFragment()

			uris?.let {
				val bundle = Bundle()
				bundle.putParcelableArrayList(KEY_URI, it)
				frag.arguments = bundle
			}

			return frag
		}
	}
}