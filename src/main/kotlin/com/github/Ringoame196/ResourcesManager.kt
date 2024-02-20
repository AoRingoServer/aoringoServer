package com.github.Ringoame196

import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

class ResourcesManager {
    val resourcesFileNames = mutableListOf("World.yml", "FoodData.yml", "Application.yml", "DropItem.yml")
    fun update(plugin: Plugin, fileName: String, player: Player) {
        plugin.saveResource(fileName, true)
        player.sendMessage("${ChatColor.YELLOW}${fileName}を更新しました")
    }
    fun makeSelectFileGUI(guiName: String): Inventory {
        val gui = Bukkit.createInventory(null, 27, guiName)
        var c = 0
        for (fileName in resourcesFileNames) {
            val item = ItemManager().make(Material.PAPER, fileName)
            gui.setItem(c, item)
            c++
            if (c == 27) { return gui }
        }
        return gui
    }
}
