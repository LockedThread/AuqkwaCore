package com.auqkwatech.auqkwacore.events

import com.auqkwatech.auqkwacore.events.executor.EventPostExecutor
import com.auqkwatech.auqkwacore.mods.Mod
import com.auqkwatech.auqkwacore.utils.isLazyInitialized
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import java.util.LinkedList
import kotlin.reflect.KClass

class EventPost<T : Event>(val clazz: KClass<T>, val eventPriority: EventPriority) {

    var disabled = false
    val filters: LinkedList<(T) -> Boolean> by lazy {
        LinkedList<(T) -> Boolean>()
    }
    var listenerConsumer: ((T) -> Unit)? = null

    fun hasFilters(): Boolean {
        return this::filters.isLazyInitialized
    }

    fun filter(event: (T) -> Boolean): EventPost<T> {
        filters.add(event)
        return this
    }

    fun handle(event: (T) -> Unit, mod: Mod?) {
        this.listenerConsumer = event
        EventPostExecutor(this, mod).registerListener()
    }
}