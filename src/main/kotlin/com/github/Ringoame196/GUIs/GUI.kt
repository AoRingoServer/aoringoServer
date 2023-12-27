package com.github.Ringoame196.GUIs

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface GUI {
    fun createGUI(player: Player): Inventory?
    fun clickItem()
}
