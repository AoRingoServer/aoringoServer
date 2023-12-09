package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class Cage {
    fun createGUi(item: ItemStack): Inventory {
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.BLUE}カゴ")
        if (item.itemMeta?.lore != null) {
            for (lore in item.itemMeta?.lore ?: return gui) {
                gui.addItem(restorationItem(lore))
            }
        }
        return gui
    }
    private fun restorationItem(lore: String): ItemStack {
        val requiredData = 5
        val parts = lore.split(",")
        val materialNumber = parts[4]
        val itemName = parts[0]
        val expirationDate = parts[1]
        val customModelData = parts[2].toInt()
        val amount = parts[3].toInt()
        val item = ItemStack(acquisitionMaterial(materialNumber))
        val meta = item.itemMeta ?: return item // metaがnullの場合は元のアイテムを返す
        if (parts.size < requiredData) return item // 部分の数が足りない場合は元のアイテムを返す

        meta.setDisplayName(itemName)
        meta.lore = mutableListOf(expirationDate)
        meta.setCustomModelData(customModelData)
        item.amount = amount

        item.itemMeta = meta // 更新したメタデータをアイテムに設定
        return item
    }
    private fun acquisitionMaterial(number:String):Material{
        return when(number) {
            "0" -> Material.MELON_SLICE
            "1" -> Material.WHEAT
            "2" -> Material.CARROT
            "3" -> Material.POTATO
            else -> throw RuntimeException("Materialが見つかりませんでした")
        }
    }
    fun clone(player: HumanEntity, gui: InventoryView) {
        val lore = mutableListOf<String>()

        for (item in gui.topInventory) {
            item ?: continue
            val meta = item.itemMeta
            val material = when (item.type) {
                Material.MELON_SLICE -> 0
                Material.WHEAT -> 1
                Material.CARROT -> 2
                Material.POTATO -> 3
                else -> return
            }
            val itemLore = listOf(
                meta?.displayName ?: "",
                meta?.lore?.get(0) ?: "",
                (meta?.customModelData ?: 0).toString(),
                item.amount.toString(),
                material.toString()
            )
            lore.add(itemLore.joinToString(","))
        }

        val playerItem = player.inventory.itemInMainHand
        val meta = playerItem.itemMeta
        meta?.lore = lore
        if (lore.size == 0) {
            meta?.setCustomModelData(1)
        } else {
            meta?.setCustomModelData(2)
        }
        playerItem.itemMeta = meta
        player.inventory.setItemInMainHand(playerItem)
    }
}
