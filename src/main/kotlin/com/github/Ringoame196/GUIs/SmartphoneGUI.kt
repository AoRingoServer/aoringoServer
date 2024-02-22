package com.github.Ringoame196.GUIs

import com.github.Ringoame196.ApplicationManager
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.MoneyUseCase
import com.github.Ringoame196.Smartphones.Applications.ConversionMoneyApplication
import com.github.Ringoame196.Smartphones.Applications.TeleportApplication
import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class SmartphoneGUI(val plugin: Plugin) : GUI {
    private val applicationManager = ApplicationManager()
    override val guiName: String = "${ChatColor.BLUE}スマートフォン"
    private val smartphoneSlots = mutableListOf(1, 3, 5, 7, 10, 12, 14, 16, 19, 21, 23, 25)
    override fun createGUI(player: Player?): Inventory {
        val guiSize = 27
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        val playerHaveAPKList = Yml().getList(plugin, "playerData", player?.uniqueId.toString(), "apkList")
        if (playerHaveAPKList.isNullOrEmpty()) {
            return gui
        }
        val apkCount = minOf(smartphoneSlots.size, playerHaveAPKList.size)
        for (i in 0 until apkCount) {
            val apkName = playerHaveAPKList[i]
            val applicationInfo = applicationManager.getApplicationInfo(apkName, plugin)
            val customModelData = applicationManager.acquisitionApplicationDataCustomModelData(applicationInfo)
            val lore = applicationInfo?.get("lore").toString()
            setApp(gui, i, apkName, lore, customModelData)
        }
        return gui
    }
    private fun setApp(gui: Inventory, i: Int, apkName: String, lore: String, customModelData: Int) {
        gui.setItem(
            smartphoneSlots[i],
            ItemManager().make(
                Material.GREEN_CONCRETE, "${ChatColor.YELLOW}[アプリ]$apkName",
                lore,
                customModelData
            )
        )
    }

    override fun whenClickedItem(player: Player, item: ItemStack, shift: Boolean) {
        val itemName = item.itemMeta?.displayName ?: return
        val apkName = itemName.replace("${ChatColor.YELLOW}[アプリ]", "") ?: return
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        if (applicationManager.checkApp(item)) {
            if (shift) {
                ApplicationManager().uninstall(player, apkName, item.itemMeta?.customModelData ?: 0, plugin)
                player.openInventory(createGUI(player))
                return
            }
            applicationManager.apkList[apkName]?.bootApplication(player, plugin)
            TeleportApplication().teleportWorldFromPlayer(player, apkName, plugin)
        }
        val customModelData = item.itemMeta?.customModelData ?: return
        val hardeningCustomModelDataMaxCount = 4
        if (item.type == Material.EMERALD && customModelData >= 1) {
            if (customModelData > hardeningCustomModelDataMaxCount) { return }
            val price = MoneyUseCase().convertingInt(itemName, "${ChatColor.GREEN}")
            ConversionMoneyApplication().possessionGoldToItem(player, price, item)
        }
    }
}
