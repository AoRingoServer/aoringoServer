package com.github.Ringoame196.Smartphones.Applications

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface ClosingApplication {
    fun close(player: Player,gui:Inventory)
}