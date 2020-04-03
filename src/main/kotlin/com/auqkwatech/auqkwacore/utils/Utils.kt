package com.auqkwatech.auqkwacore.utils

import com.auqkwatech.auqkwacore.utils.chat.DefaultFontInfo
import com.google.common.base.Joiner
import org.bukkit.command.CommandSender
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

infix fun List<String>.implode(glue: String): String {
    return Joiner.on(glue).skipNulls().join(this)
}

infix fun Array<out String>.implode(glue: String): String {
    return Joiner.on(glue).skipNulls().join(this)
}

val KProperty0<*>.isLazyInitialized: Boolean
    get() {
        isAccessible = true
        return (getDelegate() as Lazy<*>).isInitialized()
    }

/**
 * @author SirSpoodles
 * @source https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
 * @editedBy LockedThread, March 29th, 2020
 * @see DefaultFontInfo
 */
infix fun CommandSender.sendCenteredMessage(message: String) {
    if (message.isBlank()) {
        sendMessage("")
    } else {
        val colored = color(message)
        var messagePxSize = 0
        var previousCode = false
        var bold = false
        for (c in colored.toCharArray()) {
            when {
                c == '§' -> {
                    previousCode = true
                }
                previousCode -> {
                    previousCode = false
                    bold = c == 'l' || c == 'L'
                }
                else -> {
                    val length = DefaultFontInfo.getLength(c)
                    messagePxSize += if (!bold || c == ' ') length else length + 1
                    messagePxSize++
                }
            }
        }
        var compensated = 0
        val string = buildString {
            while (compensated < 154 - messagePxSize / 2) {
                append(" ")
                compensated += 4
            }
            append(colored)
        }
        sendMessage(string)
    }
}