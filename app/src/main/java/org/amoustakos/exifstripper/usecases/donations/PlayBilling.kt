package org.amoustakos.exifstripper.usecases.donations

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil
import java.lang.ref.WeakReference

/*
 * TODO: bind to lifecycle to avoid crashes
 */
class PlayBilling(
		private val activity: WeakReference<Activity>
) {

	companion object {
		private const val PLAY_DON_1 = "donation_1"
		private const val PLAY_DON_3 = "donation_3"
		private const val PLAY_DON_5 = "donation_5"

		val playIds = listOf(PLAY_DON_1, PLAY_DON_3, PLAY_DON_5)

		fun getTitleForId(id: String, context: Context) = when(id) {
			PLAY_DON_1 -> context.getString(R.string.donation_1_title)
			PLAY_DON_3 -> context.getString(R.string.donation_3_title)
			PLAY_DON_5 -> context.getString(R.string.donation_5_title)
			else       -> ""
		}
	}

	private lateinit var billingClient: BillingClient
	private var rewardedAd: RewardedAd? = null

	private lateinit var showReward: () -> Unit
	private lateinit var showDonations: (skuDetailsList: List<SkuDetails>) -> Unit
	private lateinit var onDisconnect: () -> Unit


	fun init(
			showReward: () -> Unit,
			showDonations: (skuDetailsList: List<SkuDetails>) -> Unit,
			onDisconnect: () -> Unit
	) {
		this.showReward = showReward
		this.showDonations = showDonations
		this.onDisconnect = onDisconnect
		initBilling()
	}

	fun purchase(item: SkuDetails) {
		val flowParams = BillingFlowParams.newBuilder()
				.setSkuDetails(item)
				.build()
		billingClient.launchBillingFlow(activity.get() ?: return, flowParams)
	}

	private fun initBilling() {
		billingClient = BillingClient
				.newBuilder(activity.get() ?: return)
				.setListener { billingResult, purchases ->
					if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty())
						purchases.forEach { if (!it.isAcknowledged) consumePurchase(it) }
				}
				.enablePendingPurchases()
				.build()

		billingClient.startConnection(object : BillingClientStateListener {
			override fun onBillingServiceDisconnected() {
				initBilling()
			}

			override fun onBillingSetupFinished(billingResult: BillingResult) {
				if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) return

				val params = SkuDetailsParams.newBuilder()
				params.setSkusList(playIds).setType(BillingClient.SkuType.INAPP)
				billingClient.querySkuDetailsAsync(params.build()) { result, skuDetailsList ->
					if (result.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty())
						showDonations(skuDetailsList)
				}
			}
		})
	}

	private fun consumePurchase(purchase: Purchase) {
		if (!purchase.isAcknowledged) {
			val consumeParams = ConsumeParams.newBuilder()
					.setPurchaseToken(purchase.purchaseToken)
					.build()
			billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
				if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
					AnalyticsUtil.logMessage("Failed to process purchase: ${purchase.orderId}. Token: $purchaseToken")
				} else {
					showReward()
				}
			}
		}
	}

	fun loadAdMobRewardedAd(onFail: () -> Unit, onLoad: () -> Unit) {
		val act = activity.get() ?: return
		val adId = act.getString(R.string.admob_rewarded)

		RewardedAd.load(act, adId, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
			override fun onAdFailedToLoad(adError: LoadAdError) {
				rewardedAd = null
				onFail()
			}

			override fun onAdLoaded(ad: RewardedAd) {
				rewardedAd = ad
				rewardedAd?.show(act) { showReward() }
				onLoad()
			}
		})
	}

}