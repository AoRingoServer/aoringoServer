package com.github.Ringoame196.GUIs

import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.Plugin

interface GUI {
    fun close(gui: InventoryView, player: Player, plugin: Plugin)
}
