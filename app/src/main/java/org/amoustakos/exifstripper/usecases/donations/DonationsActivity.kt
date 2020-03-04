package org.amoustakos.exifstripper.usecases.donations

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_donations.*
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.donations.adapters.DonationViewData
import org.amoustakos.exifstripper.usecases.donations.adapters.DonationsAdapter
import org.amoustakos.exifstripper.usecases.home.MainActivity
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
	private var rewardedAd: RewardedAd? = null

	private var skuDetails: List<SkuDetails>? = null

	private var adapter: DonationsAdapter? = null
	private val purchasePublisher: PublishSubject<ClickEvent<DonationViewData>> = PublishSubject.create()


	override fun layoutId() = R.layout.activity_donations


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setupToolbar()
		setGithubListener()

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

	private fun setupToolbar() {
		setupViewComponent(toolbar)
		toolbar.toggleBackButton(true)
		toolbar.showHome(true)
		toolbar.setTitle(R.string.title_activity_donations)
		toolbar.setAsActionbar(this)
	}

	override fun onBackPressed() {
		super.onBackPressed()
		val intent = MainActivity.getReturnIntent(this)
		startActivity(intent)
		finish()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> onBackPressed()
			else              -> return super.onOptionsItemSelected(item)
		}
		return true
	}

	private fun setGithubListener() {
		val listener = View.OnClickListener {
			val browserIntent = Intent(ACTION_VIEW, Uri.parse("https://github.com/antonis179/exif-stripper"))
			Do.safe(
					{ startActivity(browserIntent) },
					{
						Timber.e(it)
						Crashlytics.logException(it)
					}
			)
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

	private fun makeAndLoadRewardedAd() {
		Do.safe({loadAdMobRewardedAd()},{
			Timber.e(it)
			Crashlytics.logException(it)
		})
	}

	private fun loadAdMobRewardedAd() {
		val adId = getString(R.string.admob_rewarded)
		rewardedAd = RewardedAd(this, adId)

		rewardedAd?.loadAd(AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
			override fun onRewardedAdFailedToLoad(p0: Int) {
				super.onRewardedAdFailedToLoad(p0)
				btnAd.isEnabled = true
			}

			override fun onRewardedAdLoaded() {
				super.onRewardedAdLoaded()
				btnAd.isEnabled = true
				rewardedAd?.show(this@DonationsActivity, object : RewardedAdCallback() {
					override fun onUserEarnedReward(p0: RewardItem) {
						showReward()
					}
				})
			}
		})
	}

	private fun showReward() {
		val dlg = AlertDialog.Builder(this).create().apply {
			setMessage(getString(R.string.label_donation_reward_text))
			setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)) { dlg, _ -> dlg.dismiss() }
		}
		dlg.show()
	}

}