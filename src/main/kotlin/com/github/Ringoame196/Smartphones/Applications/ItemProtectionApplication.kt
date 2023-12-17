package com.github.Ringoame196.Smartphone.APKs

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Smartphones.Applications.Application
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class ItemProtectionApplication : Application {
    override fun getCustomModelData(): Int {
        return 3
    }
    override fun openGUI(player: Player, plugin: Plugin) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}アイテム保護")
        for (i in 0 until gui.size) {
            gui.setItem(i, ItemManager().make(Material.RED_STAINED_GLASS_PANE, " "))
        }
        gui.setItem(3, ItemManager().make(Material.AIR, "",))
        gui.setItem(6, ItemManager().make(Material.ANVIL, "${ChatColor.RED}ロック/解除"))
        player.openInventory(gui)
    }
    fun isPlayerProtection(item: ItemStack, player: Player): Boolean {
        if (item.itemMeta?.lore == null) {
            return false
        }
        for (i in 0 until item.itemMeta?.lore!!.size) {
            val lore = item.itemMeta?.lore!![i]
            val playerName = item.itemMeta?.lore!![i + 1]
            if (!lore.contains("所有者:")) { continue }
            if (lore == "所有者:${player.uniqueId}") {
                return true
            }
        }
        return false
    }
    fun chekcProtection(item: ItemStack, player: Player): ItemStack {
        val aoringoPlayer = AoringoPlayer(player)
        if (item.itemMeta?.lore == null) {
            protection(item, player)
            return item
        }
        for (i in 0 until item.itemMeta?.lore!!.size) {
            val lore = item.itemMeta?.lore!![i]
            val playerName = item.itemMeta?.lore!![i + 1]
            if (!lore.contains("所有者:")) { continue }
            return if (lore == "所有者:${player.uniqueId}") {
                unProtection(item, lore, playerName, player)
            } else {
                aoringoPlayer.sendErrorMessage("所有者設定はできませんでした")
                item
            }
        }
        return protection(item, player)
    }
    private fun protection(item: ItemStack, player: Player): ItemStack {
        val meta = item.itemMeta
        val lore = meta?.lore ?: mutableListOf<String>()
        lore.add("所有者:${player.uniqueId}")
        lore.add("所有者:${player.name}")
        meta?.lore = lore
        item.setItemMeta(meta)
        player.sendMessage("${ChatColor.GREEN}所有者登録しました")
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        return item
    }
    private fun unProtection(item: ItemStack, lore: String, playerName: String, player: Player): ItemStack {
        val meta = item.itemMeta
        val itemLore = meta?.lore
        itemLore?.remove(lore)
        itemLore?.remove(playerName)
        meta?.lore = itemLore
        item.setItemMeta(meta)
        player.sendMessage("${ChatColor.RED}所有者を削除しました")
        return item
    }
    fun isProtection(item: ItemStack?): Boolean {
        val meta = item?.itemMeta
        for (lore in meta?.lore ?: return false) {
            if (!lore.contains("所有者:")) { continue }
            return true
        }
        return false
    }
}
