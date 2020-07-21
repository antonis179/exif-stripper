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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
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
import org.amoustakos.exifstripper.io.ResponseWrapperList
import org.amoustakos.exifstripper.io.file.schemehandlers.ContentType
import org.amoustakos.exifstripper.io.model.ExifAttribute
import org.amoustakos.exifstripper.ui.dialogs.ErrorDialog
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.usecases.exifaddedit.ExifEditActivity
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeAdapter
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeViewHolder.Companion.DELETION_PUBLISHER_ID
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifAttributeViewHolder.Companion.EDIT_PUBLISHER_ID
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifImagePagerAdapter
import org.amoustakos.exifstripper.usecases.exifremoval.adapters.ExifImageViewData
import org.amoustakos.exifstripper.usecases.exifremoval.models.ExifAttributeViewData
import org.amoustakos.exifstripper.usecases.exifremoval.views.ImageHandlingToolbar
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.usecases.settings.SettingsUtil
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.FileUtils
import org.amoustakos.exifstripper.utils.Orientation
import org.amoustakos.exifstripper.utils.exif.ExifFile
import org.amoustakos.exifstripper.utils.rx.disposer.disposeBy
import org.amoustakos.exifstripper.utils.rx.disposer.onDestroy
import org.amoustakos.exifstripper.utils.ui.ViewHideScrollListener
import org.amoustakos.exifstripper.view.recycler.ClickEvent
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.Type
import timber.log.Timber
import java.io.IOException

class ImageHandlingFragment : BaseFragment() {

	private lateinit var viewModel: ExifViewModel

	private var adapter: ExifImagePagerAdapter? = null
	private var attrAdapter: ExifAttributeAdapter? = null

	private val imageSelectionListener: View.OnClickListener = View.OnClickListener { pickImage() }
	private val deletionPublisher: PublishSubject<ClickEvent<ExifAttributeViewData>> = PublishSubject.create()
	private val clickPublisher: PublishSubject<ClickEvent<ExifAttributeViewData>> = PublishSubject.create()

	private var attrSubscription: Disposable? = null

	private val toolbar = ImageHandlingToolbar(R.id.toolbar)

	private val updaterThread = Schedulers.newThread()
	private val exifThread = Schedulers.newThread()

	private var isLoading = false

	// =========================================================================================
	// View
	// =========================================================================================

	override fun layoutId() = R.layout.fragment_image_handling

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	private fun init(savedInstanceState: Bundle?) {
		viewModel = ViewModelProvider(
				this,
				SavedStateViewModelFactory(application()!!, this)
		).get(ExifViewModel::class.java)

		if (viewModel.exifFiles.value == null)
			viewModel.exifFiles.value = mutableListOf()

		if (savedInstanceState == null) {
			arguments?.getParcelableArrayList<Uri>(KEY_URI)?.let {
				if (context == null || activity == null) return
				handleUris(it)
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

		init(savedInstanceState)

		tvSelectImages.setOnClickListener(imageSelectionListener)
		abSelectImages.setOnClickListener(imageSelectionListener)
		btn_remove_all.setOnClickListener {
			setLoading(true)
			Single.fromCallable { }
					.observeOn(exifThread)
					.subscribeOn(AndroidSchedulers.mainThread())
					.map {
						val errors: MutableList<Throwable> = mutableListOf()
						viewModel.exifFiles.value?.forEach {
							Do.safe({
								removeExifData(it)
							}, {
								Timber.e(it)
								errors.add(it)
							})
						}
						errors
					}
					.observeOn(AndroidSchedulers.mainThread())
					.doOnSuccess { setLoading(false) }
					.doOnSuccess {
						if (it.isNotEmpty()) {
							if (it.filterIsInstance(IOException::class.java).isNotEmpty())
								showInvalidFormatError()
							else
								showGenericError()
						} else {
							if (SettingsUtil.getAutosave()) saveImages()
						}
					}
					.map {}
					.doOnError {
						AnalyticsUtil.logException(it)
						showGenericError()
					}
					.onErrorReturn {}
					.disposeBy(onDestroy)
					.subscribe()
		}
		toolbar.setShareListener { shareImage() }
		toolbar.setSaveListener { saveImages() }

		clickListener()
		deletionListener()

		setupViewPager()
		setupRecycler()
		updateAdapters()
	}

	private fun deletionListener() {
		deletionPublisher
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(AndroidSchedulers.mainThread())
				.doOnNext {
					Do.safe(
							{
								viewModel.exifFiles.value
										?.get(vpImageCollection.currentItem)
										?.removeAttribute(context!!, it.item.title)
							},
							{
								Timber.e(it)
								if (it !is IOException)
									AnalyticsUtil.logException(it, true)
							}
					)
				}
				.map { }
				.doOnError(Timber::e)
				.onErrorReturn { }
				.disposeBy(onDestroy)
				.doOnComplete { deletionListener() }
				.subscribe()
	}

	private fun clickListener() {
		clickPublisher
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(AndroidSchedulers.mainThread())
				.doOnNext {
					Do safe {
						val attr = ExifAttribute(it.item.title, it.item.value)
						startActivityForResult(
								ExifEditActivity.getStartIntent(context!!, attr),
								REQUEST_ATTR_EDIT
						)
					}
				}
				.map { }
				.doOnError(Timber::e)
				.onErrorReturn { }
				.disposeBy(onDestroy)
				.doOnComplete { clickListener() }
				.subscribe()
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		setHasOptionsMenu(true)
		toolbar.setTitle(R.string.app_name)
		toolbar.setAsActionbar(activity as AppCompatActivity)
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

	private fun setupRecycler() {
		if (viewModel.attrAdapterData.value == null)
			viewModel.attrAdapterData.value = mutableListOf()

		attrAdapter = ExifAttributeAdapter(viewModel.attrAdapterData.value!!, listOf(
				PublisherItem(deletionPublisher, Type.CLICK, DELETION_PUBLISHER_ID),
				PublisherItem(clickPublisher, Type.CLICK, EDIT_PUBLISHER_ID)
		))

		rvExif.setHasFixedSize(true)
		rvExif.adapter = attrAdapter

		rvExif.addOnScrollListener(ViewHideScrollListener(btn_remove_all, orientation = Orientation.VERTICAL))
	}

	private fun setLoading(loading: Boolean) {
		isLoading = loading
		setState()
	}

	private fun setState() {
		content.visibility = if (isImageLoaded()) VISIBLE else GONE
		rvExif.visibility = if (isImageLoaded()) VISIBLE else GONE
		viewLoading.visibility = if (isLoading) VISIBLE else GONE
		viewEmpty.visibility = if (isImageLoaded()) GONE else VISIBLE
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	private fun toggleAppbar() {
		if (!isImageLoaded()) {
			val p = ctToolbar.layoutParams as AppBarLayout.LayoutParams
			p.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
			ctToolbar.layoutParams = p
		} else {
			val p = ctToolbar.layoutParams as AppBarLayout.LayoutParams
			p.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
			ctToolbar.layoutParams = p
		}
	}

	private fun refreshUI() {
		toggleRemoveAllButton()
		restoreActions()
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
					AnalyticsUtil.logException(it)
				}
				.onErrorReturn { mutableListOf() }
				.disposeBy(lifecycle.onDestroy)
				.subscribe()
	}

	private fun refreshImageAdapter(items: List<ExifImageViewData>) {
		attrSubscription?.dispose()
		attrSubscription = null
		adapter?.replace(items)
		adapter?.notifyDataSetChanged()
	}

	private fun refreshAttributeAdapter(items: List<ExifAttributeViewData>) {
		attrAdapter?.replace(items)
		attrAdapter?.notifyDataSetChanged()
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
			AnalyticsUtil.logException(it)
		})
	}

	private fun saveImages() {
		Do.safe({
			context?.let { ctx ->
				Single.fromCallable {}
						.observeOn(Schedulers.computation())
						.map { viewModel.exifFiles.value?.forEach { it.saveToStripped(ctx) } }
						.observeOn(AndroidSchedulers.mainThread())
						.doOnSuccess {
							reset()
							notifyStored()
						}
						.doOnError {
							AnalyticsUtil.logException(it)
							showError(getString(R.string.error_msg_storage_issue))
						}
						.onErrorReturn { }
						.disposeBy(lifecycle.onDestroy)
						.subscribe()
			}
		}, {
			AnalyticsUtil.logException(it)
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

	private fun showGenericError() {
		showError(getString(R.string.error_msg_oops))
	}

	private fun showInvalidFormatError() {
		showError(getString(R.string.error_msg_write_format_invalid))
	}

	private fun showSnackbarError(msg: String) {
		Snackbar.make(content, msg, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok) {}.show()
	}

	// =========================================================================================
	// Views
	// =========================================================================================

	private fun toggleRemoveAllButton() {
		btn_remove_all.visibility = if (isImageLoaded()) VISIBLE else GONE
	}

	@Synchronized
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
			viewModel.exifFiles.value?.clear()
			updateAdapters()
			toggleActions(false)
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
					.observeOn(updaterThread)
					.map {
						it.map { exifData -> ExifAttributeViewData(exifData.title, exifData.value) }
					}
					.observeOn(AndroidSchedulers.mainThread())
					.doOnNext { refreshAttributeAdapter(it) }
					.doOnNext { refreshUI() }
					.map{}
					.doOnError(Timber::e)
					.onErrorReturn{}
					.disposeBy(onDestroy)
					.subscribe()

			Single.fromCallable{}
					.observeOn(updaterThread)
					.map {
						context?.let { if (image.isLoaded) image.loadExifAttributes(it) }
					}
					.doOnError(Timber::e)
					.onErrorReturn{}
					.disposeBy(onDestroy)
					.subscribe()
		}, {
			AnalyticsUtil.logException(it)
		})
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (resultCode != Activity.RESULT_OK || context == null || activity == null) {
			super.onActivityResult(requestCode, resultCode, data)
			return
		}

		when (requestCode) {
			REQUEST_IMAGE       -> handleUris(data)
			REQUEST_ATTR_EDIT   -> updateAttribute(data)
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun updateAttribute(data: Intent?) {
		if (data == null || data.extras == null)   return

		val attr = ExifEditActivity.getAttributeFromIntent(data)
		val item = getCurrentItem()

		if (item == null || attr == null) {
			showGenericError()
			return
		}

		Do.safe(
				{ item.setAttribute(context!!, attr.key, attr.value) },
				{
					Timber.e(it)
					if (it is IOException)
						showInvalidFormatError()
					else
						showGenericError()
				}
		)
	}

	private fun getCurrentItem(): ExifRemovalFile? {
		if (viewModel.exifFiles.value?.isNullOrEmpty() == true) return null
		return viewModel.exifFiles.value?.get(vpImageCollection.currentItem)
	}

	private fun handleUris(data: Intent?) {
		val uris = mutableListOf<Uri>()

		if (data?.clipData?.itemCount ?: 0 < 1)
			data?.data?.let { uris.add(it) }

		for (i in 0 until (data?.clipData?.itemCount ?: -1))
			uris.add(data!!.clipData!!.getItemAt(i).uri)

		handleUris(uris)
	}

	private fun handleUris(uris: List<Uri>) {
		reset()
		Single.fromCallable {}
				.doOnSubscribe { setLoading(true) }
				.observeOn(Schedulers.computation())
				.map {
					val response: ResponseWrapperList<ExifFile.LoadResult> = ResponseWrapperList(mutableListOf())
					context?.let {
						uris.forEach { uri ->
							val exifFile = ExifRemovalFile()
							val innerResponse = exifFile.load(uri, it)

							response plus ResponseWrapper(innerResponse.value)

							if (innerResponse.value == ExifFile.LoadResult.Success)
								viewModel.exifFiles.value?.add(exifFile)
						}
					}
					response
				}
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSuccess {
					val errorMessages: MutableList<String> = mutableListOf()

					it?.values?.forEach { innerResponse ->
						when (innerResponse.value) {
							ExifFile.LoadResult.ContextError -> getString(R.string.error_msg_generic)
							ExifFile.LoadResult.UriError -> getString(R.string.error_msg_uri_issue_external)
							ExifFile.LoadResult.CacheError -> getString(R.string.error_msg_generic)
							ExifFile.LoadResult.FormatError -> getString(R.string.error_msg_format)
							ExifFile.LoadResult.Success -> null
							null -> null
						}?.let { msg ->
							errorMessages.add(msg)
						}
					}

					errorMessages.forEach { msg ->
						showError(msg)
						return@forEach
					}

					updateAdapters()
				}
				.doOnSuccess { setLoading(false) }
				.doOnError {
					AnalyticsUtil.logException(it)
					reset()
					showGenericError()
				}
				.onErrorReturn { ResponseWrapperList() }
				.disposeBy(onDestroy)
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
		private const val REQUEST_ATTR_EDIT = 11000

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