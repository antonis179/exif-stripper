package org.amoustakos.exifstripper.di.component


import android.app.Activity
import dagger.Subcomponent
import org.amoustakos.exifstripper.di.annotations.context.ActivityContext
import org.amoustakos.exifstripper.di.annotations.scopes.PerActivity
import org.amoustakos.exifstripper.di.module.injectors.ActivityModule
import org.amoustakos.exifstripper.ui.activities.BaseActivity

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = [
	ActivityModule::class
])
interface ActivityComponent {


	// =========================================================================================
	// Injections
	// =========================================================================================

	fun inject(activity: Activity)


	// =========================================================================================
	// Base
	// =========================================================================================

	@ActivityContext
	fun activity(): BaseActivity

}
