package org.amoustakos.exifstripper.di.component


import android.app.Dialog
import dagger.Subcomponent
import org.amoustakos.exifstripper.di.annotations.scopes.PerActivity
import org.amoustakos.exifstripper.di.module.injectors.DialogModule

/**
 * This component injects dependencies to all Dialogs across the application
 */
@PerActivity
@Subcomponent(modules = [
	DialogModule::class
])
interface DialogComponent {

	fun inject(dlg: Dialog)

}
