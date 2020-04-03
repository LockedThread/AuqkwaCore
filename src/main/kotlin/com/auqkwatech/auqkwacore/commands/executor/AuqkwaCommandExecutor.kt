package com.auqkwatech.auqkwacore.commands.executor

import com.auqkwatech.auqkwacore.commands.Command
import com.auqkwatech.auqkwacore.commands.arguments.exceptions.CommandParseException
import com.auqkwatech.auqkwacore.commands.context.CommandContext
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class AuqkwaCommandExecutor : CommandExecutor {

    @Suppress("UNCHECKED_CAST")
    override fun onCommand(
            sender: CommandSender,
            bukkitCommand: org.bukkit.command.Command,
            label: String,
            args: Array<String>
    ): Boolean {
        val command: Command = Command.COMMAND_MAP[label.toLowerCase()]!!
        try {
            command.perform(CommandContext(sender, label, args))
        } catch (e: CommandParseException) {
            e.senderConsumer(sender)
        }
        return true
    }

}