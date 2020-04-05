import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.mods.mod
import com.auqkwatech.auqkwacore.utils.implode
import net.kyori.text.TextComponent
import net.kyori.text.event.HoverEvent
import net.kyori.text.format.TextColor

mod {
    withName { "CoreCommands" }

    withAuthors { arrayOf("LockedThread") }

    withParent { AuqkwaCore.instance!! }

    withCommands {
        with {
            permission {
                "auqkwacore.viewmods"
            }
            description {
                "displays all of the mods"
            }
            execution {
                val parentMessage = TextComponent.builder().color(TextColor.GREEN).append("Mods: ")
                val mods = parent!!.myMods
                for ((index, entry) in mods.entries.withIndex()) {
                    parentMessage.append(entry.key.name + if (index == mods.size - 1) "" else ", ").hoverEvent(HoverEvent.of(
                            HoverEvent.Action.SHOW_TEXT,
                            TextComponent.builder()
                                    .append("Plugin: ").append("${entry.key.parent!!.name}\n")
                                    .append("Authors: ").append("${entry.key.authors.implode(", ")}\n")
                                    .append("File: ").append(entry.value.path + "\n")
                                    .build()
                    ))
                }
                it.sendMessage(parentMessage.build())
            }
        }
    }
}
