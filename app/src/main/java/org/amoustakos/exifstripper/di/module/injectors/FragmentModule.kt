package org.amoustakos.exifstripper.di.module.injectors

import android.content.Context
import dagger.Module
import dagger.Provides
import org.amoustakos.exifstripper.di.annotations.context.ActivityContext


@Module
class FragmentModule(
		private val mFragment: androidx.fragment.app.Fragment
) {

	@Provides
	@ActivityContext
	internal fun providesContext(): Context? = mFragment.activity

}
