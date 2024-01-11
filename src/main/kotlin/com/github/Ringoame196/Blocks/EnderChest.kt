package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer

class EnderChest {
    fun investigateEnderChestSize(aoringoPlayer: AoringoPlayer): Int {
        val level = mutableListOf(6, 5, 4, 3, 2)
        for (i in level) {
            if (aoringoPlayer.luckPerms.hasPermission("enderchest.size.$i")) {
                aoringoPlayer.player.sendMessage(i.toString())
                return i
            }
        }
        return 1
    }
}
