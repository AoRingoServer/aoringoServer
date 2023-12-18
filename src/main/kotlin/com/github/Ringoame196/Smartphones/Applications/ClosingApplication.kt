package com.github.Ringoame196.Smartphones.Applications

import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.Plugin

interface ClosingApplication {
    fun close(player: Player, gui: InventoryView, plugin: Plugin)
}
