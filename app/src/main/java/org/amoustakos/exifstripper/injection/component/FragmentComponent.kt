package org.amoustakos.exifstripper.injection.component


import android.support.v4.app.Fragment
import dagger.Subcomponent
import org.amoustakos.exifstripper.injection.annotations.scopes.PerActivity
import org.amoustakos.exifstripper.injection.module.injectors.FragmentModule

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerActivity
@Subcomponent(modules = [
	FragmentModule::class
])
interface FragmentComponent {

	fun inject(fragment: Fragment)

}
