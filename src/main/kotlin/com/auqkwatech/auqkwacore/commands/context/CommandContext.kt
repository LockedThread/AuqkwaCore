package com.auqkwatech.auqkwacore.commands.context

import com.auqkwatech.auqkwacore.commands.Command
import com.auqkwatech.auqkwacore.commands.arguments.Argument
import com.auqkwatech.auqkwacore.utils.color
import net.kyori.text.TextComponent
import net.kyori.text.adapter.bukkit.TextAdapter
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

data class CommandContext(
        var sender: CommandSender,
        var label: String,
        var rawArguments: Array<String>
) {

    val commandChain: ArrayList<Command> = ArrayList()

    fun getArgument(index: Int): Argument = Argument(index, rawArguments[index])

    fun sendMessage(textComponent: TextComponent) = TextAdapter.sendComponent(sender, textComponent)

    fun sendMessage(message: String) = sender.sendMessage(color(message))

    fun sendMessage(vararg messages: String) = messages.forEach { this.sendMessage(it) }

    fun sendMustBePlayer() = sendMessage("&cYou must be a player to execute this command")

    fun isPlayer(): Boolean = sender is Player

    fun isConsole(): Boolean = sender is ConsoleCommandSender

    fun isAdmin(): Boolean = isConsole() || (isPlayer() && sender.hasPermission("auqkwacore.admin"))

    override fun toString(): String =
            "CommandContext(sender=$sender, label='$label', rawArguments=${rawArguments.contentToString()}, commandChain=$commandChain)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandContext

        if (sender != other.sender) return false
        if (label != other.label) return false
        if (!rawArguments.contentEquals(other.rawArguments)) return false
        if (commandChain != other.commandChain) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sender.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + rawArguments.contentHashCode()
        result = 31 * result + commandChain.hashCode()
        return result
    }

}