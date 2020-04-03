package com.auqkwatech.auqkwacore.commands

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.commands.arguments.exceptions.CommandParseException
import com.auqkwatech.auqkwacore.commands.context.CommandContext
import com.auqkwatech.auqkwacore.commands.implications.CommandImplication
import com.auqkwatech.auqkwacore.mods.Mod
import com.auqkwatech.auqkwacore.utils.command.CommandMapUtil
import com.auqkwatech.auqkwacore.utils.implode
import com.auqkwatech.auqkwacore.utils.info
import com.auqkwatech.auqkwacore.utils.isLazyInitialized
import com.auqkwatech.auqkwacore.utils.sendCenteredMessage
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import net.kyori.text.TextComponent
import net.kyori.text.format.TextColor
import net.kyori.text.format.TextDecoration
import org.bukkit.ChatColor
import java.util.Arrays
import java.util.EnumSet
import java.util.stream.Collectors

fun registerCommand(command: Command, mod: Mod) {
    info("Registering command: ${command.aliases[0]}")
    for (alias in command.aliases) {
        Command.COMMAND_MAP[alias] = command
    }
    Command.MOD_COMMANDS[mod].add(command)

    CommandMapUtil.registerCommand(AuqkwaCore.instance, command.aliases)
}

fun unregisterCommand(command: Command, mod: Mod) {
    command.aliases.forEach { Command.COMMAND_MAP.remove(it) }
    CommandMapUtil.unregisterCommand(command.aliases[0])
    Command.MOD_COMMANDS[mod].remove(command)
}

fun unregisterAllCommandsForMod(mod: Mod) {
    Command.MOD_COMMANDS[mod].forEach { unregisterCommand(it, mod) }
}

@Suppress("RemoveExplicitTypeArguments")
abstract class Command(vararg val aliases: String) {

    companion object {

        @Suppress("UnstableApiUsage")
        val MOD_COMMANDS: Multimap<Mod, Command> = MultimapBuilder.hashKeys().hashSetValues().build()
        val COMMAND_MAP: HashMap<String, Command> = HashMap()

        val WHITE_LINE = TextComponent.builder().color(TextColor.WHITE)
                .decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE)
                .content("----------------------------------------------------")
                .build()

        val DEFAULT_IMPLICATIONS =
                EnumSet.of(CommandImplication.ERROR_ON_TOO_FEW_ARGUMENTS, CommandImplication.ERROR_ON_TOO_MANY_ARGUMENTS)!!
    }

    var permission: String? = null
    var description: String? = null
    var helpTitle: String? = null

    private var commandImplications: EnumSet<CommandImplication>? = null

    private val subCommands: HashMap<String, Command> by lazy {
        HashMap<String, Command>()
    }

    private val requiredArguments: ArrayList<String> by lazy {
        ArrayList<String>()
    }

    private val optionalArguments: LinkedHashMap<String, String> by lazy {
        LinkedHashMap<String, String>()
    }

    @Throws(CommandParseException::class)
    abstract fun execute(context: CommandContext)

    @Throws(CommandParseException::class)
    fun perform(context: CommandContext) {
        val hasPermission = when {
            context.sender.hasPermission("auqkwacore.admin") -> true
            else -> permission != null && context.sender.hasPermission(permission!!)
        }
        if (hasPermission) {
            if (hasSubCommands() && context.rawArguments.isNotEmpty()) {
                context.label = context.rawArguments[0]
                context.rawArguments = context.rawArguments.copyOfRange(1, context.rawArguments.size)
                context.commandChain.add(this)
                subCommands[context.label]!!.perform(context)
            } else {
                if (validCommandImplcations(context)) {
                    execute(context)
                }
            }
        } else {
            context.sendMessage(ChatColor.RED.toString() + "Sorry, you don't have permission to execute this command! If you feel like this is incorrect contact a server admin.")
        }
    }

    fun validCommandImplcations(context: CommandContext): Boolean {
        if (commandImplications != null) {
            if (commandImplications!!.contains(CommandImplication.MUST_BE_PLAYER) && !context.isPlayer()) {
                return false
            }
        }
        return validArgs(context)
    }

    fun addOptionalArguments(arg: String, default: String) {
        optionalArguments[arg] = default
    }

    fun addRequiredArguments(vararg arg: String) {
        requiredArguments.addAll(arg)
    }

    fun addSubCommands(vararg commands: Command) {
        for (command in commands) {
            for (arg in command.aliases) {
                subCommands[arg] = command
            }
        }
    }

    fun hasOptionalArguments(): Boolean {
        return if (this::optionalArguments.isLazyInitialized) optionalArguments.isNotEmpty() else false
    }

    fun hasRequiredArguments(): Boolean {
        return if (this::requiredArguments.isLazyInitialized) requiredArguments.isNotEmpty() else false
    }

    fun hasSubCommands(): Boolean {
        return if (this::subCommands.isLazyInitialized) subCommands.isNotEmpty() else false
    }

    fun sendHelp(context: CommandContext): Boolean {
        val list = subCommands.values.stream().distinct().filter {
            it.permission == null || context.sender.hasPermission(it.permission!!)
        }.collect(Collectors.toList())

        if (list.isEmpty()) {
            return false
        }

        context.sendMessage(WHITE_LINE)
        if (helpTitle != null) {
            context.sender.sendCenteredMessage(helpTitle!!)
            context.sendMessage(WHITE_LINE)
        }

        list.forEach { context.sendMessage(it.getUsageTemplate(context)) }
        context.sendMessage(WHITE_LINE)
        return true
    }

    fun getUsageTemplate(context: CommandContext): TextComponent {
        val builder = TextComponent.builder().color(TextColor.AQUA).append("/")
        if (context.commandChain.isNotEmpty()) {
            builder.append(
                    context.commandChain.stream().map { it.aliases[0] }.collect(Collectors.toList()).implode(" ")
            )
        } else {
            builder.append(context.label)
        }
        builder.append(" ")
                .append(aliases.implode(",")).append(" ")
        if (hasRequiredArguments()) {
            val implodedArguments =
                    this.requiredArguments.stream().map { "<${it}>" }.collect(Collectors.toList())
                            .implode(" ")
            builder.append(implodedArguments)
        }

        if (hasOptionalArguments()) {
            val implodedArguments =
                    this.optionalArguments.entries.stream().map { "<${it.key}=${it.value}>" }
                            .collect(Collectors.toList())
                            .implode(" ")
            builder.append(implodedArguments)
        }

        if (this.description != null) {
            builder.append(" | ").append(this.description!!)
        }
        return builder.build()
    }

    private fun validImplication(commandImplication: CommandImplication): Boolean {
        return when {
            this.commandImplications != null -> this.commandImplications!!.contains(commandImplication)
            else -> DEFAULT_IMPLICATIONS.contains(commandImplication)
        }
    }

    fun addImplications(vararg commandImplication: CommandImplication) {
        if (this.commandImplications == null) {
            this.commandImplications = Arrays.stream(commandImplication)
                    .collect(Collectors.toCollection { EnumSet.noneOf(CommandImplication::class.java) })
        } else {
            this.commandImplications!!.addAll(commandImplication)
        }
    }

    private fun validArgs(context: CommandContext): Boolean {
        if (validImplication(CommandImplication.ERROR_ON_TOO_FEW_ARGUMENTS) && context.rawArguments.size < this.requiredArguments.size && this.subCommands.isEmpty()) {
            context.sendMessage("Too few arguments. Use like this:")
            context.sendMessage(getUsageTemplate(context))
            return false
        }
        if (context.rawArguments.size > this.requiredArguments.size + this.optionalArguments.size) {
            if (validImplication(CommandImplication.ERROR_ON_TOO_MANY_ARGUMENTS)) {
                context.sendMessage("Too many arguments. Use like this:")
                context.sendMessage(getUsageTemplate(context))
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        return "Command(aliases=${aliases.contentToString()}, permission=$permission, description=$description, helpTitle=$helpTitle, commandImplications=$commandImplications)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Command

        if (!aliases.contentEquals(other.aliases)) return false
        if (permission != other.permission) return false
        if (description != other.description) return false
        if (helpTitle != other.helpTitle) return false
        if (commandImplications != other.commandImplications) return false

        return true
    }

    override fun hashCode(): Int {
        var result = aliases.contentHashCode()
        result = 31 * result + (permission?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (helpTitle?.hashCode() ?: 0)
        result = 31 * result + (commandImplications?.hashCode() ?: 0)
        return result
    }
}
