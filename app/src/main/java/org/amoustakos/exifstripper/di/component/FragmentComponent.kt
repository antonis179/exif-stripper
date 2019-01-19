package org.amoustakos.exifstripper.di.component


import dagger.Subcomponent
import org.amoustakos.exifstripper.di.annotations.scopes.PerActivity
import org.amoustakos.exifstripper.di.module.injectors.FragmentModule

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerActivity
@Subcomponent(modules = [
	FragmentModule::class
])
interface FragmentComponent {

	fun inject(fragment: androidx.fragment.app.Fragment)

}
