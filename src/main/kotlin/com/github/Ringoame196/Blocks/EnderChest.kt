package com.github.Ringoame196

import com.github.Ringoame196.GUIs.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

class EnderChest {
    fun investigateEnderChestSize(player: Player): Int {
        val level = mutableListOf(6, 5, 4, 3, 2, 1)
        for (i in level) {
            if (player.hasPermission("enderchest.size.$i")) {
                return i
            }
        }
        return 0
    }
}
