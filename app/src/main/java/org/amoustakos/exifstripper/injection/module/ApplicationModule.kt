package org.amoustakos.exifstripper.injection.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import org.amoustakos.exifstripper.injection.annotations.context.ApplicationContext

/**
 * Provide application-level dependencies.
 */
@Module
class ApplicationModule(
		private val mApplication: Application
) {

	@Provides
	internal fun provideApplication() = mApplication

	@Provides
	@ApplicationContext
	internal fun provideContext(): Context = mApplication

}
