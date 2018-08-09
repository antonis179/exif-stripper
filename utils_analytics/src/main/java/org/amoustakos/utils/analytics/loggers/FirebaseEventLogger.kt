package org.amoustakos.utils.analytics.loggers

import android.content.Context
import android.os.Bundle
import android.support.annotation.NonNull
import com.google.firebase.analytics.FirebaseAnalytics
import org.amoustakos.utils.analytics.EventAction
import org.amoustakos.utils.analytics.EventLogger
import org.amoustakos.utils.analytics.EventLogger.Companion.SPACE
import org.amoustakos.utils.analytics.EventLogger.Companion.UNDERSCORE
import java.lang.ref.WeakReference

class FirebaseEventLogger(@NonNull context: Context) : EventLogger<Bundle> {

    private val context: WeakReference<Context> = WeakReference(context)

    // =========================================================================================
    // Logging methods
    // =========================================================================================

    override fun logEvent(eventName: String, action: String, parameters: Map<String, String>)  =
        FirebaseAnalytics.getInstance(context.get()!!).logEvent(
                makeEventName(eventName, action),
                transformParameters(parameters)
        )


    override fun transformParameters(attributes: Map<String, String>): Bundle {
        val bundle = Bundle()
        for ((key, value) in attributes) {
            bundle.putString(key, value)
        }
        return bundle
    }

    override fun makeEventName(eventName: String, action: String?): String {
        val event = StringBuilder(
                eventName.replace(SPACE.toRegex(), UNDERSCORE))

        if (action != null && action != EventAction.NONE) {
            event.append(UNDERSCORE)
            event.append(action)
        }

        return event.toString()
    }


}