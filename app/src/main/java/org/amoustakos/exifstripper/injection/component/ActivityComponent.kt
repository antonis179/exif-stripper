package org.amoustakos.exifstripper.injection.component


import android.app.Activity
import dagger.Subcomponent
import org.amoustakos.exifstripper.examples.ui.activities.MainActivity
import org.amoustakos.exifstripper.injection.annotations.context.ActivityContext
import org.amoustakos.exifstripper.injection.annotations.scopes.PerActivity
import org.amoustakos.exifstripper.injection.module.injectors.ActivityModule
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
	fun inject(activity: MainActivity)


	// =========================================================================================
	// Base
	// =========================================================================================

	@ActivityContext
	fun activity(): BaseActivity

}
