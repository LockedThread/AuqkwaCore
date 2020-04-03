package com.auqkwatech.auqkwacore.utils

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.events.EventPost
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import java.util.stream.Collectors

val server get() = Bukkit.getServer()

val consoleSender get() = server.consoleSender

inline fun <reified T : Event> listen(eventPriority: EventPriority): EventPost<T> {
    return EventPost(T::class, eventPriority)
}

fun info(any: Any) = AuqkwaCore.instance?.logger?.info(any.toString())

fun severe(any: Any) = AuqkwaCore.instance?.logger?.severe(any.toString())

fun color(string: String): String = ChatColor.translateAlternateColorCodes('&', string)

fun color(strings: List<String>): List<String> = strings.stream().map { color(it) }.collect(Collectors.toList())
