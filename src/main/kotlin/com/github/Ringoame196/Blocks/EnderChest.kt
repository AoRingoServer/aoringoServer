package com.github.Ringoame196

import org.bukkit.entity.Player

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
