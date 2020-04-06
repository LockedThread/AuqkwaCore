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

fun menu(lambda: Menu.() -> Unit, name: String, size: Int): Menu {
    val mod = Menu(name, size)
    lambda(mod)
    return mod
}

class Menu(name: String, size: Int) : InventoryHolder {

    companion object {
        val SET_CANCELLED: (InventoryClickEvent) -> Unit = { it.isCancelled = true }
    }

    var inventoryOpen: ((InventoryOpenEvent) -> Unit)? = null
    var inventoryClose: ((InventoryCloseEvent) -> Unit)? = null
    private var initialize: () -> Unit = {}
    private val itemClickCallbacks = Int2ObjectOpenHashMap<(InventoryClickEvent) -> Unit>()
    private val inventory: Inventory = server.createInventory(this, size, color(name))

    init {
        initialize()
    }

    fun onInitialize(lambda: () -> Unit) {
        this.initialize = lambda
    }

    override fun getInventory(): Inventory = inventory

    fun setItem(slot: Int, itemStack: ItemStack) {
        setItem(slot, itemStack, null)
    }

    fun setItem(slot: Int, itemStack: ItemStack, event: ((InventoryClickEvent) -> Unit)?) {
        getInventory().setItem(slot, itemStack)
        itemClickCallbacks[slot] = event ?: SET_CANCELLED
    }

    fun getItemClickLambda(slot: Int): ((InventoryClickEvent) -> Unit)? {
        return itemClickCallbacks[slot]
    }


}