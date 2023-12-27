package com.github.Ringoame196.Blocks

import com.github.Ringoame196.GUIs.closingGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.Plugin

class EnchantingTable : closingGUI {
    override fun close(gui: InventoryView, player: Player, plugin: Plugin) {
        val item = gui.getItem(4) ?: return
        if (item.type == Material.ENCHANTED_BOOK) {
            return
        }
        player.inventory.addItem(item)
    }
}
