package mods

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.menus.Menu
import com.auqkwatech.auqkwacore.mods.Mod
import com.auqkwatech.auqkwacore.plugin.AuqkwaPlugin
import com.auqkwatech.auqkwacore.utils.listen
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

object : Mod {
    override fun start() {
        listen<InventoryClickEvent>(EventPriority.HIGHEST)
                .filter { it.clickedInventory != null }
                .filter { it.clickedInventory!!.holder is Menu }
                .handle({ (it.clickedInventory!!.holder as Menu).getItemClickCallback(it.rawSlot)?.invoke(it) }, this)

        listen<InventoryCloseEvent>(EventPriority.HIGHEST)
                .filter { it.inventory.holder is Menu }
                .handle({ (it.inventory.holder as Menu).inventoryClose?.invoke(it) }, this)

        listen<InventoryOpenEvent>(EventPriority.HIGHEST)
                .filter { it.inventory.holder is Menu }
                .handle({ (it.inventory.holder as Menu).inventoryOpen?.invoke(it) }, this)
    }

    override fun name(): String = "Menus"

    override fun authors(): Array<String> = arrayOf("LockedThread")
    override fun parent(): AuqkwaPlugin = AuqkwaCore.instance!!

}