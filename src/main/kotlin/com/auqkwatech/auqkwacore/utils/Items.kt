package com.auqkwatech.auqkwacore.utils

import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

infix fun ItemStack.named(name: String): ItemStack {
    val itemMeta = this.itemMeta
    itemMeta.setDisplayName(color(name))
    this.itemMeta = itemMeta
    return this
}

infix fun ItemStack.lored(lore: List<String>): ItemStack {
    val itemMeta = this.itemMeta
    itemMeta.lore = color(lore)
    this.itemMeta = itemMeta
    return this
}

infix fun ItemStack.replacePlaceholders(replacements: Array<String>): ItemStack {
    if (!this.hasItemMeta() || (this.hasItemMeta() && (!this.itemMeta.hasDisplayName() || !this.itemMeta.hasLore()))) {
        return this
    }
    var skip = false
    var placeholder = ""
    val itemMeta = this.itemMeta
    for (replacement in replacements) {
        if (skip) {
            if (itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(itemMeta.displayName.replace(placeholder, replacement))
            }
            if (itemMeta.hasLore()) {
                itemMeta.lore = itemMeta.lore!!.stream().map { it.replace(placeholder, replacement) }.collect(Collectors.toList())
            }
            skip = false
        } else {
            placeholder = replacement
            skip = true
        }
    }
    this.itemMeta = itemMeta

    return this
}