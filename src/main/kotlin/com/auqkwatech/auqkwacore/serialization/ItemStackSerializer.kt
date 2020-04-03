package com.auqkwatech.auqkwacore.serialization

import com.auqkwatech.auqkwacore.utils.color
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import kotlin.streams.toList

object ItemStackSerializer : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    override fun serialize(itemstack: ItemStack, type: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("material", itemstack.type.name)
        jsonObject.addProperty("amount", itemstack.amount)
        jsonObject.addProperty("durability", itemstack.durability)


        if (itemstack.hasItemMeta()) {
            val metaObject = JsonObject()
            val itemMeta = itemstack.itemMeta
            if (itemMeta.hasDisplayName()) {
                metaObject.addProperty("displayName", itemMeta.displayName)
            }
            if (itemMeta.hasLore()) {
                metaObject.add("lore", context.serialize(itemMeta.lore, object : TypeToken<List<String>>() {}.type))
            }
            if (itemMeta.itemFlags.isNotEmpty()) {
                metaObject.add("itemflags", context.serialize(itemMeta.itemFlags.stream().map { it.name }.toList(), object : TypeToken<List<String>>() {}.type))
            }
            if (itemMeta.hasEnchants()) {
                metaObject.add("enchants", context.serialize(itemMeta.enchants.mapKeys { it.key.key.key }, object : TypeToken<Map<String, Int>>() {}.type))
            }
            jsonObject.add("meta", metaObject)
        }
        return jsonObject
    }

    override fun deserialize(element: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ItemStack {
        val jsonObject = element.asJsonObject
        val material = Material.matchMaterial(jsonObject["material"]!!.asString)!!
        val amount = jsonObject["amount"].asInt
        val durability = jsonObject["durability"].asShort
        val itemStack = ItemStack(material, amount, durability)

        if (jsonObject.has("meta")) {
            val metaJson = jsonObject.getAsJsonObject("meta")
            val itemMeta = itemStack.itemMeta
            metaJson.entrySet().forEach { entry: MutableMap.MutableEntry<String, JsonElement> ->
                when (entry.key) {
                    "displayName" -> itemMeta.setDisplayName(color(entry.value.asString))
                    "lore" -> itemMeta.lore = entry.value.asJsonArray.map { color(it.asString) }
                    "itemflags" -> entry.value.asJsonArray.forEach { itemMeta.addItemFlags(ItemFlag.valueOf(it.asString.toUpperCase())) }
                    //"enchants" -> entry.value..forEach { itemMeta.addEnchant(Enchantment.valueOf(it.asString.toUpperCase())) }
                }
            }
            itemStack.itemMeta = itemMeta
        }
        return itemStack
    }
}