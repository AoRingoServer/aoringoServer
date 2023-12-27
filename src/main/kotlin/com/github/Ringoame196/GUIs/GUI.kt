package com.github.Ringoame196.GUIs

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView

interface GUI {
    fun close(gui: InventoryView,player: Player)
}