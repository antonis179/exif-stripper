package org.amoustakos.utils.android

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.support.annotation.NonNull


object ComponentUtils {


    fun toggleComponent(
            @NonNull context: Context,
            componentClass: Class<*>,
            enable: Boolean,
            addDoNotKill: Boolean) {

        val componentName = ComponentName(context, componentClass)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
                componentName,

                if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                else        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,

                if (addDoNotKill)   PackageManager.DONT_KILL_APP
                else                0
        )
    }


    //TODO: check android O compatibility
//    @Throws(NullPointerException::class)
//    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
//        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        manager.getRunningServices(Integer.MAX_VALUE).forEach {
//            if (serviceClass.name == it.service.className)
//                return true
//        }
//        return false
//    }


}
