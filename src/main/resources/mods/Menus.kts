package mods

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.menus.Menu
import com.auqkwatech.auqkwacore.mods.mod
import com.auqkwatech.auqkwacore.utils.listen
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
            listen<InventoryClickEvent>(EventPriority.HIGHEST)
                    .filter { it.clickedInventory != null }
                    .filter { it.clickedInventory!!.holder is Menu }
                    .handle({ (it.clickedInventory!!.holder as Menu).getItemClickCallback(it.rawSlot)!!(it) }, this@mod)
        }
        with<InventoryCloseEvent> {
            listen<InventoryCloseEvent>(EventPriority.HIGHEST)
                    .filter { it.inventory.holder is Menu }
                    .handle({ (it.inventory.holder as Menu).inventoryClose!!(it) }, this@mod)
        }
        with<InventoryOpenEvent> {
            listen<InventoryOpenEvent>(EventPriority.HIGHEST)
                    .filter { it.inventory.holder is Menu }
                    .handle({ (it.inventory.holder as Menu).inventoryOpen!!(it) }, this@mod)
        }
    }
}
