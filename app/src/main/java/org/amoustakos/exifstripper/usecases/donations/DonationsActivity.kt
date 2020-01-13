package org.amoustakos.exifstripper.usecases.donations

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.crashlytics.android.Crashlytics
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_donations.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.donations.adapters.DonationViewData
import org.amoustakos.exifstripper.usecases.donations.adapters.DonationsAdapter
import org.amoustakos.exifstripper.usecases.privacy.GdprUtil
import org.amoustakos.exifstripper.view.recycler.ClickEvent
import org.amoustakos.exifstripper.view.recycler.PublisherItem
import org.amoustakos.exifstripper.view.recycler.Type
import org.amoustakos.exifstripper.view.toolbars.BasicToolbar
import org.amoustakos.utils.android.kotlin.Do
import org.amoustakos.utils.android.rx.disposer.disposeBy
import org.amoustakos.utils.android.rx.disposer.onDestroy
import timber.log.Timber


/*
 * WIP
 */
class DonationsActivity : BaseActivity() {

	private val toolbar = BasicToolbar(R.id.toolbar)
	private lateinit var billingClient: BillingClient

	private var skuDetails: List<SkuDetails>? = null

	private var adapter: DonationsAdapter? = null
	private val purchasePublisher: PublishSubject<ClickEvent<DonationViewData>> = PublishSubject.create()


	override fun layoutId() = R.layout.activity_donations


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setupToolbar()
		setGithubListener()

		cacheRewardedAd()

		loadBilling()

		btnAd.setOnClickListener {
			btnAd.isEnabled = false
			makeAndLoadRewardedAd()
		}

		purchasePublisher
				.observeOn(Schedulers.io())
				.doOnNext {
					skuDetails?.get(it.item.position)?.let { sku -> purchase(sku) }
				}
				.doOnError(Timber::e)
				.map { }
				.onErrorReturn { }
				.disposeBy(onDestroy)
				.subscribe()
	}

	private fun setGithubListener() {
		val listener = View.OnClickListener {
			val browserIntent = Intent(ACTION_VIEW, Uri.parse("https://github.com/antonis179/exif-stripper"))
			startActivity(browserIntent)
		}

		ivGithub.setOnClickListener(listener)
		tvGithub.setOnClickListener(listener)
	}

	private fun loadBilling() {
		Do.safe({
			initBilling()
		}, {
			Crashlytics.logException(it)
			Timber.e(it)
		})
	}

	private fun setupRecycler(viewData: MutableList<DonationViewData>) {
		if (adapter != null) {
			adapter?.replace(viewData)
			adapter?.notifyDataSetChanged()
			return
		}

		adapter = DonationsAdapter(viewData, listOf(
				PublisherItem(purchasePublisher, Type.CLICK)
		))

		rvDonations.adapter = adapter
	}

	private fun initBilling() {
		billingClient = BillingClient
				.newBuilder(this)
				.setListener { billingResult, purchases ->
					if (billingResult.responseCode == OK && !purchases.isNullOrEmpty())
						purchases.forEach { if (!it.isAcknowledged) consumePurchase(it) }
				}
				.enablePendingPurchases()
				.build()

		billingClient.startConnection(object : BillingClientStateListener {
			override fun onBillingServiceDisconnected() {
				loadBilling()
			}

			override fun onBillingSetupFinished(billingResult: BillingResult) {
				if (billingResult.responseCode != OK) return

				val params = SkuDetailsParams.newBuilder()
				params.setSkusList(getDonationIds()).setType(BillingClient.SkuType.INAPP)
				billingClient.querySkuDetailsAsync(params.build()) { result, skuDetailsList ->
					if (result.responseCode == OK && !skuDetailsList.isNullOrEmpty())
						showPlayDonations(skuDetailsList)
				}
			}
		})

	}

	private fun showPlayDonations(skuDetailsList: List<SkuDetails>) {
		skuDetails = skuDetailsList

		val viewData = skuDetailsList.mapIndexed { index, sku ->
			DonationViewData(
					index,
					BillingUtil.getTitleForId(sku.sku, this),
					sku.description,
					sku.price
			)
		}.toMutableList()

		setupRecycler(viewData)
	}

	private fun purchase(item: SkuDetails) {
		val flowParams = BillingFlowParams.newBuilder()
				.setSkuDetails(item)
				.build()
		billingClient.launchBillingFlow(this, flowParams)
	}

	private fun consumePurchase(purchase: Purchase) {
		if (!purchase.isAcknowledged) {
			val consumeParams = ConsumeParams.newBuilder()
					.setPurchaseToken(purchase.purchaseToken)
					.build()
			billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
				if (billingResult.responseCode != OK) {
					Crashlytics.log("Failed to process purchase: ${purchase.orderId}. Token: $purchaseToken")
				} else {
					showReward()
				}
			}
		}
	}

	private fun getDonationIds() = BillingUtil.playIds

	private fun cacheRewardedAd() {
		Do safe {
			Appodeal.initialize(
					this,
					getString(R.string.appodeal_app_key),
					Appodeal.REWARDED_VIDEO,
					GdprUtil.hasAcceptedTerms(this)
			)

			Appodeal.setRewardedVideoCallbacks(object : RewardedVideoCallbacks {
				override fun onRewardedVideoLoaded(isPrecache: Boolean) {}
				override fun onRewardedVideoFailedToLoad() {}
				override fun onRewardedVideoClicked() {}
				override fun onRewardedVideoClosed(finished: Boolean) {}
				override fun onRewardedVideoExpired() {}
				override fun onRewardedVideoShown() {}

				override fun onRewardedVideoFinished(amount: Double, name: String) {
					showReward()
				}
			})
			Appodeal.cache(this, Appodeal.REWARDED_VIDEO)
		}
	}

	private fun makeAndLoadRewardedAd() {
		Do safe { Appodeal.show(this, Appodeal.REWARDED_VIDEO) }
	}

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.toggleBackButton(true)
		toolbar.showHome(true)
		toolbar.setTitle(R.string.title_activity_donations)
	}

	private fun showReward() {
		val dlg = AlertDialog.Builder(this).create().apply {
			setMessage(getString(R.string.label_donation_reward_text))
			setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)) { dlg, _ -> dlg.dismiss() }
		}
		dlg.show()
	}

}