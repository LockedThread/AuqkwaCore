package com.auqkwatech.auqkwacore.mods

import com.auqkwatech.auqkwacore.commands.unregisterAllCommandsForMod
import com.auqkwatech.auqkwacore.events.EventPost
import com.auqkwatech.auqkwacore.plugin.AuqkwaPlugin
import java.io.File

val eventPostMap = HashMap<Mod, HashSet<EventPost<*>>>()

fun addEventPost(mod: Mod, eventPost: EventPost<*>) {
    eventPostMap.computeIfPresent(mod) { _, shit ->
        shit.add(eventPost)
        shit
    }
    eventPostMap.putIfAbsent(mod, hashSetOf(eventPost))
}

fun removeEventPost(mod: Mod, eventPost: EventPost<*>) {
    eventPostMap.computeIfPresent(mod) { _, shit ->
        shit.remove(eventPost)
        shit
    }
}

fun clearEventPosts(mod: Mod) {
    val iterator = eventPostMap[mod]?.iterator()!!
    while (iterator.hasNext()) {
        iterator.next().disabled = true
        iterator.remove()
    }
}

val modToFile = HashMap<Mod, File>()

interface Mod {

    fun start()

    fun stop() {
        clearEventPosts(this)
        unregisterAllCommandsForMod(this)
    }

    fun name(): String

    fun authors(): Array<String>

    fun parent(): AuqkwaPlugin

    fun refresh() {}
}