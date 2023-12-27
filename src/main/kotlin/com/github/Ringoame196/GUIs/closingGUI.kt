package com.github.Ringoame196.GUIs

import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.Plugin

interface closingGUI {
    fun close(gui: InventoryView, player: Player, plugin: Plugin)
}
