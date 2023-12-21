package com.github.Ringoame196

import org.bukkit.OfflinePlayer

class PlayerAccount(private val player: OfflinePlayer) : Account {
    override fun getAccountID(): String {
        return player.uniqueId.toString()
    }
}
