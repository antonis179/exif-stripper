package org.amoustakos.exifstripper.injection.component


import android.app.Dialog
import dagger.Subcomponent
import org.amoustakos.exifstripper.injection.annotations.scopes.PerActivity
import org.amoustakos.exifstripper.injection.module.injectors.DialogModule

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
