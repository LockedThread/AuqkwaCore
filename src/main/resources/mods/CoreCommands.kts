import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.commands.Command
import com.auqkwatech.auqkwacore.commands.context.CommandContext
import com.auqkwatech.auqkwacore.commands.registerCommand
import com.auqkwatech.auqkwacore.mods.Mod
import com.auqkwatech.auqkwacore.plugin.AuqkwaPlugin
import com.auqkwatech.auqkwacore.utils.implode
import net.kyori.text.TextComponent
import net.kyori.text.event.HoverEvent
import net.kyori.text.format.TextColor

object : Mod {

    override fun start() {
        registerCommand(ModsCommand(), this)
    }

    override fun name(): String = "CoreCommands"

    override fun authors(): Array<String> = arrayOf("LockedThread")

    override fun parent(): AuqkwaPlugin = AuqkwaCore.instance!!

    inner class ModsCommand : Command("mods", "viewmods", "listmods") {

        init {
            this.permission = "auqkwacore.viewmods"
            this.description = "displays all of the mods"
        }

        override fun execute(context: CommandContext) {
            val nigger = TextComponent.builder().color(TextColor.GREEN).append("Mods: ")
            val mods = parent().myMods
            for ((index, entry) in mods.entries.withIndex()) {
                nigger.append(entry.key.name() + if (index == mods.size - 1) "" else ", ").hoverEvent(HoverEvent.of(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponent.builder()
                                .append("Plugin: ").append("${entry.key.parent().name}\n")
                                .append("Authors: ").append("${entry.key.authors().implode(", ")}\n")
                                .append("File: ").append(entry.value.path + "\n")
                                .build()
                ))
            }
            context.sendMessage(nigger.build())
        }
    }
}
