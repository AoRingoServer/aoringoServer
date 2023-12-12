package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.Item
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class APK {
    fun get(plugin: Plugin, player: HumanEntity): MutableList<String>? {
        return Yml().getList(plugin, "playerData", player.uniqueId.toString(), "apkList")
    }
    fun add(player: org.bukkit.entity.Player, itemName: String, plugin: Plugin) {
        val aoringoPlayer = AoringoPlayer(player)
        val apkName = itemName.replace("[アプリケーション]", "")
        val apks = get(plugin, player)
        if ((apks?.size ?: 0) > 12) {
            aoringoPlayer.sendErrorMessage("[スマートフォン]容量が足りませんでした")
            return
        }
        if (apks?.contains(apkName) == true) {
            aoringoPlayer.sendErrorMessage("[スマートフォン]同アプリをインストールすることはできません")
            return
        }
        val playerItem = player.inventory.itemInMainHand.clone()
        playerItem.amount = playerItem.amount - 1
        player.inventory.setItemInMainHand(playerItem)
        player.sendMessage("${ChatColor.AQUA}[スマートフォン]${apkName}${ChatColor.AQUA}のインストール開始…")
        var t = 0
        object : BukkitRunnable() {
            override fun run() {
                t ++
                player.playSound(player, Sound.BLOCK_LAVA_POP, 1f, 1f)
                if (t == 10) {
                    Yml().addToList(plugin, "playerData", "${player.uniqueId}", "apkList", apkName)
                    player.sendMessage("${ChatColor.YELLOW}[スマートフォン]${apkName}${ChatColor.YELLOW}のインストール完了")
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
    fun remove(player: org.bukkit.entity.Player, itemName: String, customModelData: Int, plugin: Plugin) {
        player.inventory.addItem(Item().make(Material.GREEN_CONCRETE, "[アプリケーション]$itemName", "", customModelData, 1))
        Yml().removeToList(plugin, "playerData", player.uniqueId.toString(), "apkList", itemName)
        player.sendMessage("${ChatColor.RED}[スマートフォン]${itemName}${ChatColor.RED}をアンインストールしました")
        player.playSound(player, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
    }
    fun setSort(player: HumanEntity, gui: InventoryView, plugin: Plugin) {
        val apkList = mutableListOf<String>()
        var c = 1
        for (apk in gui.topInventory) {
            if (apk == null) { continue }
            if (c <= 12) {
                apkList.add(apk.itemMeta?.displayName?.replace("[アプリケーション]", "") ?: continue)
            } else {
                player.inventory.addItem(apk)
            }
            c ++
        }
        if (get(plugin, player as org.bukkit.entity.Player) == apkList) {
            return
        }
        Yml().setList(plugin, "playerData", player.uniqueId.toString(), "apkList", apkList)
    }
}
