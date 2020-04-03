package com.auqkwatech.auqkwacore.customitem

import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

data class CustomItem(val name: String, val item: ItemStack) {

    companion object {
        private val customItems: HashMap<String, CustomItem> = HashMap()
    }

    private lateinit var playerInteract: (PlayerInteractEvent) -> Unit

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> of(event: T.() -> Unit): CustomItem {
        if (event is PlayerInteractEvent) {
            this.playerInteract = event as (PlayerInteractEvent) -> Unit
        }
        return this
    }

    fun publish() {
        customItems[name] = this
    }

}