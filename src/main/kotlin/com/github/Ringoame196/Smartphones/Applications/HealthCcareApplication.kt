package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class HealthCcareApplication : Application {
    override fun getCustomModelData(): Int {
        return 9
    }
    override fun openGUI(player: Player, plugin: Plugin) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}ヘルスケア")
        gui.setItem(3, ItemManager().make(Material.MELON_SLICE, "${ChatColor.RED}マックスHP", "${player.maxHealth.toInt()}HP", 92, 1))
        gui.setItem(5, ItemManager().make(Material.MELON_SLICE, "${ChatColor.GREEN}Power", "${Scoreboard().getValue("status_Power",player.uniqueId.toString())}パワー", 91, 1))
        player.openInventory(gui)
    }
}
