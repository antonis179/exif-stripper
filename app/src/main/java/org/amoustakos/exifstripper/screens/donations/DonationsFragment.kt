package org.amoustakos.exifstripper.screens.donations

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.SkuDetails
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_donations.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.screens.donations.adapters.DonationViewData
import org.amoustakos.exifstripper.screens.donations.adapters.DonationsAdapter
import org.amoustakos.exifstripper.screens.privacy.AnalyticsUtil
import org.amoustakos.exifstripper.ui.fragments.BaseFragment
import org.amoustakos.exifstripper.utils.Do
import org.amoustakos.exifstripper.utils.rx.disposer.disposeBy
import org.amoustakos.exifstripper.utils.rx.disposer.onDestroy
import org.amoustakos.exifstripper.view.recycler.ClickEvent
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.Type
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar
import timber.log.Timber
import java.lang.ref.WeakReference

class DonationsFragment : BaseFragment() {

	private val toolbar = BasicToolbar(R.id.toolbar)
	private var adapter: DonationsAdapter? = null
	private lateinit var billing: PlayBilling
	private var skuDetails: List<SkuDetails>? = null
	private val purchasePublisher: PublishSubject<ClickEvent<DonationViewData>> = PublishSubject.create()


	override fun layoutId() = R.layout.fragment_donations

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setupToolbar()
		setGithubListener()

		purchasePublisher
				.observeOn(Schedulers.io())
				.doOnNext {
					skuDetails?.get(it.item.position)?.let { sku -> billing.purchase(sku) }
				}
				.doOnError(Timber::e)
				.map { }
				.onErrorReturn { }
				.disposeBy(onDestroy)
				.subscribe()

		loadBilling()

		btnAd.setOnClickListener {
			btnAd.isEnabled = false
			makeAndLoadRewardedAd()
		}
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.setTitle(R.string.app_name)
		toolbar.setAsActionbar(activity as AppCompatActivity)
	}

	private fun setGithubListener() {
		val listener = View.OnClickListener {
			val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/antonis179/exif-stripper"))
			Do.safe(
					{ startActivity(browserIntent) },
					{ AnalyticsUtil.logException(it) }
			)
		}
		ivGithub.setOnClickListener(listener)
		tvGithub.setOnClickListener(listener)
	}

	private fun loadBilling() {
		Do.safe(
				{
					billing = PlayBilling(WeakReference(requireActivity()))
					billing.init(this::showReward, this::showPlayDonations, this::loadBilling)
				},
				{
					AnalyticsUtil.logException(it)
				}
		)
	}

	private fun setupRecycler(viewData: MutableList<DonationViewData>) {
		if (adapter != null) {
			adapter?.replace(viewData)
			adapter?.notifyDataSetChanged()
			rvDonations.adapter = adapter
			return
		}

		adapter = DonationsAdapter(viewData, listOf(
				PublisherItem(purchasePublisher, Type.CLICK)
		))

		rvDonations.adapter = adapter
	}

	private fun showPlayDonations(skuDetailsList: List<SkuDetails>) {
		Do.safe(
				{
                    if (this.isDetached || activity == null) return //TODO

                    skuDetails = skuDetailsList

                    val viewData = skuDetailsList.mapIndexed { index, sku ->
                        DonationViewData(
                                index,
                                PlayBilling.getTitleForId(sku.sku, requireActivity()),
                                sku.description,
                                sku.price
                        )
                    }.toMutableList()

                    setupRecycler(viewData)
                },
				{
					AnalyticsUtil.logException(it)
				}
		)
	}


	private fun makeAndLoadRewardedAd() {
		Do.safe(
				{
					billing.loadAdMobRewardedAd({ btnAd.isEnabled = true }, { btnAd.isEnabled = true })
				},
				{
					AnalyticsUtil.logException(it)
				}
		)
	}

	private fun showReward() {
		val dlg = AlertDialog.Builder(requireActivity()).create().apply {
			setMessage(getString(R.string.label_donation_reward_text))
			setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)) { dlg, _ -> dlg.dismiss() }
		}
		dlg.show()
	}


	companion object {
		fun newInstance(): DonationsFragment {
			return DonationsFragment()
		}
	}
}