package com.github.Ringoame196.Smartphones.Applications

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

interface Application {
    fun getCustomModelData(): Int
    fun openGUI(player: Player, plugin: Plugin)
    fun closeGUI(player: Player,gui:Inventory)
}
