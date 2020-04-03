package com.auqkwatech.auqkwacore.menus

import com.auqkwatech.auqkwacore.utils.color
import com.auqkwatech.auqkwacore.utils.server
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

abstract class Menu(val name: String, size: Int) : InventoryHolder {

    companion object {
        val SET_CANCELLED: (InventoryClickEvent) -> Unit = { it.isCancelled = true }
    }

    var inventoryOpen: ((InventoryOpenEvent) -> Unit)? = null
    var inventoryClose: ((InventoryCloseEvent) -> Unit)? = null
    private val itemClickCallbacks = Int2ObjectOpenHashMap<(InventoryClickEvent) -> Unit>()

    //TODO: Figure out why I get the warning below
    private val inventory: Inventory = server.createInventory(this, size, color(name))

    final override fun getInventory(): Inventory = inventory

    fun setItem(slot: Int, itemStack: ItemStack) {
        setItem(slot, itemStack, null)
    }

    fun setItem(slot: Int, itemStack: ItemStack, event: ((InventoryClickEvent) -> Unit)?) {
        getInventory().setItem(slot, itemStack)
        itemClickCallbacks[slot] = event ?: SET_CANCELLED
    }

    fun getItemClickCallback(slot: Int): ((InventoryClickEvent) -> Unit)? {
        return itemClickCallbacks[slot]
    }


}