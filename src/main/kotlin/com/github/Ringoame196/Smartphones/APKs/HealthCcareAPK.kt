package com.github.Ringoame196.Smartphones.APKs

import com.github.Ringoame196.APKs
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class HealthCcareAPK : APKs {
    override val customModelData: Int = 9
    override fun openGUI(player: Player, plugin: Plugin) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}ヘルスケア")
        gui.setItem(3, Item().make(Material.MELON_SLICE, "${ChatColor.RED}マックスHP", "${player.maxHealth.toInt()}HP", 92, 1))
        gui.setItem(5, Item().make(Material.MELON_SLICE, "${ChatColor.GREEN}Power", "${Scoreboard().getValue("status_Power",player.uniqueId.toString())}パワー", 91, 1))
        player.openInventory(gui)
    }
}
