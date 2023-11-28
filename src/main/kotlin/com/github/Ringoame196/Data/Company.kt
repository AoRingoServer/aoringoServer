package com.github.Ringoame196.Data

import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material

class Company {
    fun openGUI(player: org.bukkit.entity.Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.RED}会社操作")
        gui.setItem(1, Item().make(Material.PAPER, "${ChatColor.GREEN}引き出す", null, 3, 1))
        player.openInventory(gui)
    }
}
