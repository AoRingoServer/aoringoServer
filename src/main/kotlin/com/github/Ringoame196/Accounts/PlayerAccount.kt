package com.github.Ringoame196.Accounts

import org.bukkit.OfflinePlayer

class PlayerAccount(val player: OfflinePlayer) : Account {
    override fun getAccountID(): String {
        return player.uniqueId.toString()
    }

    override fun getRegisteredPerson(): String {
        return player.name ?: "取得不可能のプレイヤー"
    }
}
