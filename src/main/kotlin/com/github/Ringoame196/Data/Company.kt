package com.github.Ringoame196.Data

import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory

class Company {
    fun createGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.RED}会社操作")
        gui.setItem(1, Item().make(material = Material.PAPER, name = "${ChatColor.GREEN}引き出す", customModelData = 3))
        return gui
    }
}
