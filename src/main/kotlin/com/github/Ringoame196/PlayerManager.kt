package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class PlayerManager {
    fun acquisitionPlayerNameList(): MutableList<String> {
        val playerNameList = mutableListOf<String>("[プレイヤー名]")
        for (player in Bukkit.getOnlinePlayers()) {
            playerNameList.add(player.name)
        }
        return playerNameList
    }
    fun acquisitionPlayer(name: String): OfflinePlayer {
        return Bukkit.getOfflinePlayer(name)
    }
}
