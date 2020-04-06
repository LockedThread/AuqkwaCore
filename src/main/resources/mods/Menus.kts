package mods

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.menus.Menu
import com.auqkwatech.auqkwacore.mods.mod
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

mod {
    withName { "Menus" }

    withAuthors { arrayOf("LockedThread") }

    withParent { AuqkwaCore.instance!! }

    withEventPosts {
        with<InventoryClickEvent> {
            priority { EventPriority.HIGHEST }
            filter { it.clickedInventory != null && it.clickedInventory!!.holder is Menu }
            handle(this@mod) {
                (it.clickedInventory!!.holder as Menu).getItemClickLambda(it.rawSlot)!!(it)
            }
        }
        with<InventoryCloseEvent> {
            priority { EventPriority.HIGHEST }
            filter { it.inventory.holder is Menu }
            handle(this@mod) {
                (it.inventory.holder as Menu).inventoryClose!!(it)
            }
        }
        with<InventoryOpenEvent> {
            priority { EventPriority.HIGHEST }
            filter { it.inventory.holder is Menu }
            handle(this@mod) {
                (it.inventory.holder as Menu).inventoryOpen!!(it)
            }
        }
    }
}
