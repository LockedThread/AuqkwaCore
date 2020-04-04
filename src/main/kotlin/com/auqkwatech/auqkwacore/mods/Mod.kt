package com.auqkwatech.auqkwacore.mods

import com.auqkwatech.auqkwacore.commands.Command
import com.auqkwatech.auqkwacore.commands.registerCommand
import com.auqkwatech.auqkwacore.commands.unregisterAllCommandsForMod
import com.auqkwatech.auqkwacore.events.EventPost
import com.auqkwatech.auqkwacore.plugin.AuqkwaPlugin
import org.bukkit.event.Event

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

fun mod(lambda: Mod.() -> Unit): Mod {
    val mod = Mod()
    lambda(mod)
    return mod
}

class Mod {

    private var startLambda: () -> Unit = {}
    private var stopLambda: () -> Unit = {}
    private var refreshLambda: () -> Unit = {}
    internal var name: String = ""
    internal var authors: Array<String> = emptyArray()
    internal var parent: AuqkwaPlugin? = null
    private var commands: Commands? = null
    private var eventPosts: EventPosts? = null

    fun onStart(lambda: () -> Unit) {
        this.startLambda = lambda
    }

    fun onStop(lambda: () -> Unit) {
        this.stopLambda = lambda
    }

    fun onRefresh(lambda: () -> Unit) {
        this.refreshLambda = lambda
    }

    fun withName(lambda: () -> String) {
        this.name = lambda()
    }

    fun withAuthors(lambda: () -> Array<String>) {
        this.authors = lambda()
    }

    fun withParent(lambda: () -> AuqkwaPlugin) {
        this.parent = lambda()
    }

    fun withCommands(lambda: Commands.() -> Unit) {
        this.commands = Commands().apply(lambda)
    }

    fun withEventPosts(lambda: EventPosts.() -> Unit) {
        this.eventPosts = EventPosts().apply(lambda)
    }

    fun stop() {
        clearEventPosts(this)
        unregisterAllCommandsForMod(this)
        stopLambda()
    }

    fun start() {
        commands?.forEach { registerCommand(it, this) }
        startLambda()
    }
}

val BLANK_COMMAND = Command()

class Commands : ArrayList<Command>() {
    fun with(command: Command.() -> Unit) {
        add(BLANK_COMMAND.apply(command))
    }
}

class EventPosts : ArrayList<EventPost<*>>() {
    inline fun <T : Event> with(command: EventPost<T>.() -> Unit) {
        add(EventPost<T>().apply(command))
    }
}