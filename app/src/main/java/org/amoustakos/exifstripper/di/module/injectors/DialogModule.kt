package org.amoustakos.exifstripper.di.module.injectors

import android.app.Dialog
import android.content.Context
import dagger.Module
import dagger.Provides
import org.amoustakos.exifstripper.di.annotations.context.ActivityContext


@Module
class DialogModule(
		private val mDialog: Dialog
) {

	@Provides
	@ActivityContext
	internal fun providesContext(): Context = mDialog.context

}
