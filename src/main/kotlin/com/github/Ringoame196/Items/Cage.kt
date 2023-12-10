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
        val itemName = parts[0]
        val expirationDate = parts[1]
        val customModelData = parts[2].toInt()
        val amount = parts[3].toInt()
        val materialNumber = parts[4].toInt()
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
    private fun acquisitionMaterial(number:Int):Material{
        val materials = listOf(
            Material.MELON_SLICE,
            Material.WHEAT,
            Material.CARROT,
            Material.POTATO
        )
        try {
            return materials[number]
        } catch(e: IndexOutOfBoundsException) {
            throw RuntimeException("Materialが見つかりませんでした")
        }
    }
    fun clone(player: HumanEntity, gui: InventoryView) {
        val lore = mutableListOf<String>()

        for (item in gui.topInventory) {
            item ?: continue
            val meta = item.itemMeta
            val material = compressionMaterial(item.type)
            val itemLore = listOf(
                meta?.displayName ?: "",
                meta?.lore?.get(0) ?: "",
                (meta?.customModelData ?: 0).toString(),
                item.amount.toString(),
                material.toString()
            )
            lore.add(itemLore.joinToString(","))
        }

        val cage = player.inventory.itemInMainHand
        val meta = cage.itemMeta
        meta?.lore = lore
        if (lore.size == 0) {
            meta?.setCustomModelData(1)
        } else {
            meta?.setCustomModelData(2)
        }
        cage.itemMeta = meta
        player.inventory.setItemInMainHand(cage)
    }
    private fun compressionMaterial(material: Material):Int{
        return when(material) {
            Material.MELON_SLICE -> 0
            Material.WHEAT -> 1
            Material.CARROT -> 2
            Material.POTATO -> 3
            else -> throw RuntimeException("未登録のアイテムを追加しようとしました")
        }
    }
}
