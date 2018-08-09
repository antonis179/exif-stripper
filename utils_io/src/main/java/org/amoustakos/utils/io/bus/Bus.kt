package org.amoustakos.utils.io.bus

import org.greenrobot.eventbus.EventBus

object Bus {

    private val bus: EventBus

    init {
        EventBus.builder().installDefaultEventBus()
        bus = EventBus.getDefault()
    }

    @JvmStatic
    fun register(subscriber: Any) = bus.register(subscriber)

    @JvmStatic
    fun unregister(subscriber: Any) = bus.unregister(subscriber)


    @JvmStatic
    fun post(event: Any) = bus.post(event)

    @JvmStatic
    fun postSticky(event: Any) = bus.postSticky(event)

}