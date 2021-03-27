package org.amoustakos.exifstripper.screens.exifremoval

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import org.amoustakos.exifstripper.utils.FileUtils

class GetImagesContract : ActivityResultContract<GetImagesData, Intent?>() {
	override fun createIntent(context: Context, input: GetImagesData) = FileUtils.createGetContentIntent(
			context,
			input.type,
			title = input.title,
			allowMultiple = true
	)
	override fun parseResult(resultCode: Int, intent: Intent?) = intent
}

data class GetImagesData(
		val title: String,
		val type: String
)