package org.amoustakos.exifstripper.injection.module.injectors

import dagger.Module
import dagger.Provides
import org.amoustakos.exifstripper.injection.annotations.context.ActivityContext
import org.amoustakos.exifstripper.ui.activities.base.BaseActivity

@Module
class ActivityModule(
		private val mActivity: BaseActivity
) {

	@Provides
	@ActivityContext
	internal fun providesContext(): BaseActivity = mActivity


}
