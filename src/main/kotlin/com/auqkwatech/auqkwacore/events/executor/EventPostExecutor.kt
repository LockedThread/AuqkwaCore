package com.auqkwatech.auqkwacore.events.executor

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.events.EventPost
import com.auqkwatech.auqkwacore.mods.Mod
import com.auqkwatech.auqkwacore.mods.addEventPost
import com.auqkwatech.auqkwacore.mods.removeEventPost
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

class EventPostExecutor<T : Event>(private val eventPost: EventPost<T>, private val mod: Mod?) :
        EventExecutor,
        Listener {

    fun registerListener() {
        AuqkwaCore.instance!!.server.pluginManager.registerEvent(eventPost.clazz.java, this, eventPost.eventPriority, this, AuqkwaCore.instance!!)
        if (mod != null) {
            addEventPost(mod, eventPost)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun execute(listener: Listener, event: Event) {
        if (eventPost.disabled) {
            if (mod != null) {
                removeEventPost(mod, eventPost)
            }
            event.handlers.unregister(this)
            return
        }
        if (event::class != eventPost.clazz) {
            return
        }
        if (eventPost.hasFilters()) {
            for (filter in eventPost.filters) {
                if (!filter(event as T)) {
                    return
                }
            }
        }
        eventPost.listenerConsumer!!.invoke(event as T)
    }
}
