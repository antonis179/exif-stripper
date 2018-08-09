package org.amoustakos.utils.analytics


interface EventLogger<out T> {

    fun logEvent(
            eventName: String,
            @EventAction action: String,
            parameters: Map<String, String>
    )

    fun transformParameters(attributes: Map<String, String>): T

    fun makeEventName(eventName: String, @EventAction action: String?): String

    companion object {
        const val SPACE = " "
        const val UNDERSCORE = "_"
    }
}
