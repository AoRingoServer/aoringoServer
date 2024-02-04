package com.github.Ringoame196

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class PlayerAccount(private val player: OfflinePlayer) : Account {
    override fun getAccountID(): String {
        return player.uniqueId.toString()
    }
    fun getPlayer(): Player? {
        return if (player is Player) {
            player
        } else {
            null
        }
    }
}
