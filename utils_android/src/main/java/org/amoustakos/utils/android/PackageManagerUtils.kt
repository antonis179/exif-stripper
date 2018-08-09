package org.amoustakos.utils.android

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber
import java.util.*


/**
 * Utility class that provides helper methods for [PackageManager].
 */
object PackageManagerUtils {

    /**
     * Retrieve activities defined in the Manifest.
     *
     * @param context     Needed to get a [PackageManager] instance.
     * @param basePackage Required to specify the manifest to look through.
     * @param <T>         Defines the casting performed to the activities retrieved.
    </T> */
    fun <T : Activity> definedActivities(
            context: Context,
            basePackage: String
    ): List<Class<out T>> {
        val pm = context.packageManager
        return definedActivities(pm, basePackage)
    }

    /**
     * Retrieve activities defined in the Manifest.
     *
     * @param pm          The [PackageManager] instance to be used.
     * @param basePackage Required to specify the manifest to look through.
     * @param <T>         Defines the casting performed to the activities retrieved.
    </T> */
    fun <T : Activity> definedActivities(
            pm: PackageManager,
            basePackage: String
    ): List<Class<out T>> {
        val activities = ArrayList<Class<out T>>()
        try {
            val packageInfo = pm.getPackageInfo(basePackage, PackageManager.GET_ACTIVITIES)
            val act = packageInfo.activities
            for (ai in act) {
                try {
                    if (!ai.name.contains(basePackage)) continue
                    val cl = Class.forName(ai.name) as Class<out T>
                    activities.add(cl)
                } catch (cle: ClassNotFoundException) {
                    Timber.v(cle)
                } catch (cle: ClassCastException) {
                    Timber.v(cle)
                }

            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }

        return activities
    }


}
