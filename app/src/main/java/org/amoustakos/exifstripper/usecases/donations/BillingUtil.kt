package org.amoustakos.exifstripper.usecases.donations

import android.content.Context
import org.amoustakos.exifstripper.R

/*
 * WIP
 */
object BillingUtil {

	// =========================================================================================
	// Google Play
	// =========================================================================================
	const val PLAY_DON_1 = "donation_1"
	const val PLAY_DON_3 = "donation_3"
	const val PLAY_DON_5 = "donation_5"

	val playIds = listOf(PLAY_DON_1, PLAY_DON_3, PLAY_DON_5)

//	val testPlayIds = listOf("android.test.purchased", "android.test.canceled", "android.test.item_unavailable")


	fun getTitleForId(id: String, context: Context) = when(id) {
		PLAY_DON_1 -> context.getString(R.string.donation_1_title)
		PLAY_DON_3 -> context.getString(R.string.donation_3_title)
		PLAY_DON_5 -> context.getString(R.string.donation_5_title)
		else       -> ""
	}

}