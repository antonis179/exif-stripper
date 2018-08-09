package org.amoustakos.utils.analytics

import android.content.Context
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*


abstract class Event (context: Context) {

    protected val context: WeakReference<Context> = WeakReference(context)
    var attributes: Map<String, String>? = null

    var loggers: List<EventLogger<*>>? = null
        protected set

    var event: String? = null

    @EventAction
    var action: String = EventAction.NONE


    init {
        this.initLoggers()
    }


    constructor(
            context: Context,
            attributes: Map<String, String>,
            event: String,
            @EventAction action: String
    ) : this(context) {
        this.attributes = attributes
        this.event = event
        this.action = action
    }

    protected abstract fun initLoggers()

    // =========================================================================================
    // Event handling
    // =========================================================================================

    fun logAllAsync(): Observable<Boolean> {
        return Observable.fromCallable {
            logAll()
            true}
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnError { Timber.e(it) }
            .onErrorReturn { _ -> false }
    }

    @Throws(NullPointerException::class)
    fun logAll(
            eventName: String = event!!,
            @EventAction action: String = this.action,
            attributes: Map<String, String>? = this.attributes) {
        var attrs = attributes

        if (!shouldTag()) return
        if (attrs == null) attrs = HashMap()

        //Log
        for (logger in loggers!!)
            logger.logEvent(eventName, action, attrs)

        //Print info
        val message = "Event: " +
                eventName + SPACE +
                action + SPACE +
                if (attrs.isNotEmpty()) attributes.toString() else ""

        Timber.d(message)
    }


    // =========================================================================================
    // Options
    // =========================================================================================

    abstract fun shouldTag(): Boolean

    @Synchronized
    fun setEventData(
            attributes: Map<String, String>,
            event: String,
            @EventAction action: String
    ) {
        this.attributes = attributes
        this.event = event
        this.action = action
    }

    companion object {
        protected const val SPACE = " "
    }

}
