package com.auqkwatech.auqkwacore.commands.arguments.exceptions

import com.auqkwatech.auqkwacore.utils.color
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class CommandParseException : Exception {
    var senderConsumer: (CommandSender) -> Unit

    constructor(type: String, index: Int) {
        senderConsumer = { it.sendMessage(ChatColor.RED.toString() + "Unable to parse $type at index $index") }
    }

    constructor(message: String) {
        senderConsumer = { it.sendMessage(color(message)) }
    }

    constructor(senderConsumer: (CommandSender) -> Unit) {
        this.senderConsumer = senderConsumer
    }
}