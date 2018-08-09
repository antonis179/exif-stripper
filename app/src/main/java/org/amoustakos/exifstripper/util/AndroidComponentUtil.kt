package org.amoustakos.exifstripper.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager


object AndroidComponentUtil {

	/**
	 * Enable or disable a component (Activity, Service, BroadcastReceiver, or ContentProvider)
	 */
	fun setComponentState(context: Context, componentClass: Class<*>, enable: Boolean) {
		val componentName = ComponentName(context, componentClass)
		val pm = context.packageManager
        pm.setComponentEnabledSetting(
                componentName,
                if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        )
	}

	/*TODO: fix deprecation if possible
	 * Checks whether a service is running
	 */
//	fun isServiceRunning(context: Context, serviceClass: Class<out Service>): Boolean {
//		val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//
//		manager.getRunningServices(Integer.MAX_VALUE).forEach { service ->
//			if (serviceClass.name == service.service.className)
//				return true
//		}
//
//		return false
//	}
}
