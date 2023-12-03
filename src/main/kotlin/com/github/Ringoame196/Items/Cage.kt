package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class Cage {
    fun open(player: Player) {
        val item = player.inventory.itemInMainHand
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.BLUE}カゴ")
        if (item.itemMeta?.lore != null) {
            for (lore in item.itemMeta?.lore ?: return) {
                gui.addItem(get(lore))
            }
        }
        player.openInventory(gui)
        player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
    }
    private fun get(lore: String): ItemStack? {

        val parts = lore.split(",")
        val item = ItemStack(
            when (parts[4]) {
                "0" -> Material.MELON_SLICE
                "1" -> Material.WHEAT
                "2" -> Material.CARROT
                "3" -> Material.POTATO
                else -> return null
            }
        )
        val meta = item.itemMeta ?: return item // metaがnullの場合は元のアイテムを返す
        if (parts.size < 5) return item // 部分の数が足りない場合は元のアイテムを返す

        meta.setDisplayName(parts[0])
        meta.lore = mutableListOf(parts[1])
        meta.setCustomModelData(parts[2].toInt())
        item.amount = parts[3].toInt()

        item.itemMeta = meta // 更新したメタデータをアイテムに設定
        return item
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
