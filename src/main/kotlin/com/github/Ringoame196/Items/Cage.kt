package com.github.Ringoame196

import com.github.Ringoame196.GUIs.GUI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class Cage:GUI {
    override fun close(gui: InventoryView, player: Player) {
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
        val airStatus = 0
        val thingsStatus = 1
        val status = lore.size == 0
        meta?.lore = lore
        if (status) {
            meta?.setCustomModelData(airStatus)
        } else {
            meta?.setCustomModelData(thingsStatus)
        }
        cage.itemMeta = meta
        player.inventory.setItemInMainHand(cage)
        player.playSound(player, Sound.BLOCK_CHEST_CLOSE, 1f, 1f)
    }
    private val materialToIdMap = mapOf<Int, Material>(
        0 to Material.MELON_SLICE,
        1 to Material.WHEAT,
        2 to Material.CARROT,
        3 to Material.POTATO
    )
    private val idToMaterialMap = materialToIdMap.entries.associate { (key, value) -> value to key }
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
    private fun acquisitionMaterial(number: Int): Material {
        return materialToIdMap[number] ?: throw RuntimeException("Materialが見つかりませんでした")
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
        val airStatus = 0
        val thingsStatus = 1
        val status = lore.size == 0
        meta?.lore = lore
        if (status) {
            meta?.setCustomModelData(airStatus)
        } else {
            meta?.setCustomModelData(thingsStatus)
        }
        cage.itemMeta = meta
        player.inventory.setItemInMainHand(cage)
    }
    private fun compressionMaterial(material: Material): Int? {
        return idToMaterialMap[material]
    }
}
