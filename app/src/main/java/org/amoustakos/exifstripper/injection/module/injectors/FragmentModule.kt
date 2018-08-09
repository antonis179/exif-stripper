package org.amoustakos.exifstripper.injection.module.injectors

import android.app.Fragment
import android.content.Context
import dagger.Module
import dagger.Provides
import org.amoustakos.exifstripper.injection.annotations.context.ActivityContext


@Module
class FragmentModule(
		private val mFragment: Fragment
) {

	@Provides
	@ActivityContext
	internal fun providesContext(): Context = mFragment.activity

}
