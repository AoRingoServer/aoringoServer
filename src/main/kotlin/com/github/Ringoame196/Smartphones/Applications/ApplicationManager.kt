package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Smartphone.APKs.ItemProtectionApplication
import com.github.Ringoame196.Smartphones.Applications.Application
import com.github.Ringoame196.Smartphones.Applications.ConversionMoneyApplication
import com.github.Ringoame196.Smartphones.Applications.EnderChestApplication
import com.github.Ringoame196.Smartphones.Applications.HealthCcareApplication
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplication
import com.github.Ringoame196.Smartphones.Applications.PlayerRatingApplication
import com.github.Ringoame196.Smartphones.Applications.SortApplication
import com.github.Ringoame196.Smartphones.Applications.TeleportApplication
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.yaml.snakeyaml.Yaml
import java.io.File

class ApplicationManager {
    val apkList = mapOf<String, Application>(
        "${ChatColor.YELLOW}エンダーチェスト" to EnderChestApplication(),
        "${ChatColor.GREEN}所持金変換" to ConversionMoneyApplication(),
        "${ChatColor.RED}アイテム保護" to ItemProtectionApplication(),
        "${ChatColor.GREEN}テレポート" to TeleportApplication(),
        "${ChatColor.GREEN}プレイヤー評価" to PlayerRatingApplication(),
        "${ChatColor.GREEN}土地保護" to LandProtectionApplication(),
        "${ChatColor.YELLOW}アプリ並べ替え" to SortApplication(),
        "${ChatColor.AQUA}ヘルスケア" to HealthCcareApplication(),
    )
    fun get(plugin: Plugin, player: HumanEntity): MutableList<String>? {
        return Yml().getList(plugin, "playerData", player.uniqueId.toString(), "apkList")
    }
    fun install(player: org.bukkit.entity.Player, itemName: String, plugin: Plugin) {
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
    fun uninstall(player: org.bukkit.entity.Player, itemName: String, customModelData: Int, plugin: Plugin) {
        val application = ItemManager().make(Material.GREEN_CONCRETE, "[アプリケーション]$itemName", "", customModelData, 1)
        player.inventory.addItem(application)
        Yml().removeToList(plugin, "playerData", player.uniqueId.toString(), "apkList", itemName)
        player.sendMessage("${ChatColor.RED}[スマートフォン]${itemName}${ChatColor.RED}をアンインストールしました")
        player.playSound(player, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
    }
    fun saveToYmlFile(player: HumanEntity, gui: InventoryView, plugin: Plugin) {
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
    fun checkApp(item: ItemStack): Boolean {
        val prefix = "[アプリ]"
        val appItemId = Material.GREEN_CONCRETE
        val itemName = item.itemMeta?.displayName ?: ""
        if (item.type != appItemId) { return false }
        return itemName.contains(prefix)
    }
    fun acquisitionApplicationDataCustomModelData(applicationInfo: Map<String, Any>?): Int {
        val customModelData = applicationInfo?.get("customModelData")
        return customModelData?.toString()?.toInt() ?: 0
    }
    fun getApplicationInfo(applicationName: String, plugin: Plugin): Map<String, Any>? {
        val ymlFile = File(plugin.dataFolder, "Application.yml")

        try {
            java.io.FileReader(ymlFile).use { fileReader ->
                val yaml = Yaml()
                val yamlData = yaml.load(fileReader) as? Map<String, Any>

                // itemNameに対応する値があるか確認
                if (yamlData != null) {
                    return yamlData[applicationName] as? Map<String, Any>
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
