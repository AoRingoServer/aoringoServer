package com.github.Ringoame196.Items

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class Item {
    fun make(material: Material, name: String, lore: String?, customModelData: Int?, amount: Int?): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        meta?.setCustomModelData(customModelData)
        if (lore != null) {
            meta?.lore = mutableListOf(lore)
        }
        item.setItemMeta(meta)
        if (amount != null) {
            item.amount = amount
        }
        return item
    }
    fun getInventoryItemCount(inventory: Inventory): Int {
        var c = 0
        for (i in 0 until inventory.size) {
            val item = inventory.getItem(i) ?: continue
            c += item.amount
        }
        return c
    }
    fun enchant(enchant: Enchantment, level: Int): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta as EnchantmentStorageMeta
        meta.addStoredEnchant(enchant, level, true)
        item.setItemMeta(meta)
        return item
    }

    fun drop(location: Location, itemStack: ItemStack) {
        val item: Item = location.world!!.spawn(location, org.bukkit.entity.Item::class.java)
        item.itemStack = itemStack
    }

    fun remove(player: Player) {
        val item = player.inventory.itemInMainHand
        item.amount = item.amount - 1
        player.inventory.setItemInMainHand(item)
    }
}
