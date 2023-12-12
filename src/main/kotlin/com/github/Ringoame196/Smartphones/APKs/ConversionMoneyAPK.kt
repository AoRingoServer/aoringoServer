package com.github.Ringoame196.Smartphones.APKs

import com.github.Ringoame196.APKs
import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ConversionMoneyAPK : APKs {
    override val customModelData: Int = 2
    override fun openGUI(player: Player, plugin: Plugin) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.EMERALD, "${ChatColor.GREEN}100円", customModelData = 1))
        gui.setItem(3, Item().make(Material.EMERALD, "${ChatColor.GREEN}1000円", customModelData = 2))
        gui.setItem(5, Item().make(Material.EMERALD, "${ChatColor.GREEN}10000円", customModelData = 3))
        gui.setItem(7, Item().make(Material.EMERALD, "${ChatColor.GREEN}100000円", customModelData = 4))
        player.openInventory(gui)
    }
}
