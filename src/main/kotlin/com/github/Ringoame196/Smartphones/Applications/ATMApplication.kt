package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ATMApplication : Application {
    override fun getCustomModelData(): Int {
        return 0
    }

    override fun openGUI(player: Player, plugin: Plugin) {
        val itemManager = ItemManager()
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.BLUE}ATM")
        gui.setItem(1, itemManager.make(Material.EMERALD, "${ChatColor.GREEN}送金"))
        player.openInventory(gui)
    }
}
