package org.amoustakos.exifstripper.usecases.exifremoval

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import org.amoustakos.exifstripper.io.model.ExifAttribute
import org.amoustakos.exifstripper.usecases.exifaddedit.ExifEditActivity

class EditAttributeContract : ActivityResultContract<ExifAttribute, Intent?>() {
	override fun createIntent(context: Context, input: ExifAttribute) = ExifEditActivity.getStartIntent(
			context,
			input
	)
	override fun parseResult(resultCode: Int, intent: Intent?) = intent
}