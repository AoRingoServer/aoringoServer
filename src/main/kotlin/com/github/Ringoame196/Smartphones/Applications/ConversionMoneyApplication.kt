package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ConversionMoneyApplication : Application {
    override fun getCustomModelData(): Int {
        return 2
    }
    override fun bootApplication(player: Player, plugin: Plugin) {
        val itemManager = ItemManager()
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, itemManager.make(Material.EMERALD, "${ChatColor.GREEN}100円", customModelData = 1))
        gui.setItem(3, itemManager.make(Material.EMERALD, "${ChatColor.GREEN}1000円", customModelData = 2))
        gui.setItem(5, itemManager.make(Material.EMERALD, "${ChatColor.GREEN}10000円", customModelData = 3))
        gui.setItem(7, itemManager.make(Material.EMERALD, "${ChatColor.GREEN}100000円", customModelData = 4))
        player.openInventory(gui)
    }
}
