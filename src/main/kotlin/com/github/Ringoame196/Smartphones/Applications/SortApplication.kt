package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.ApplicationManager
import com.github.Ringoame196.GUIs.GUI
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Smartphones.Smartphone
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.Plugin

class SortApplication : Application, GUI {
    override fun getCustomModelData(): Int {
        return 8
    }
    override fun openGUI(player: Player, plugin: Plugin) {
        val applicationManager = ApplicationManager()
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.BLUE}スマートフォン(並び替え)")
        player.openInventory(gui)
        val apkList = applicationManager.get(plugin, player) ?: return
        for (apkName in apkList) {
            val apkLIst = Smartphone().apkList
            val customModelData = apkLIst[apkName]?.getCustomModelData() ?: 0
            gui.addItem(ItemManager().make(Material.GREEN_CONCRETE, "[アプリケーション]$apkName", customModelData = customModelData))
        }
    }

    override fun close(gui: InventoryView, player: Player, plugin: Plugin) {
        ApplicationManager().saveToYmlFile(player, gui, plugin)
    }
}
