package com.github.Ringoame196.Data

import com.github.Ringoame196.Entity.Player
import com.github.Ringoame196.Event.AoringoEvents
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class Money {
    fun get(playerUUID: String): Int {
        return Scoreboard().getValue("money", playerUUID)
    }
    fun add(playerUUID: String, add: Int, unei: Boolean) {
        val money = get(playerUUID) + add
        Scoreboard().set("money", playerUUID, money)
        Player().sendActionBar(Bukkit.getPlayer(playerUUID) ?: return, "${ChatColor.GREEN}+$add")
    }
    fun remove(playerUUID: String, remove: Int, unei: Boolean): Boolean {
        val money = get(playerUUID) - remove
        if (money < 0) {
            AoringoEvents().onErrorEvent(Bukkit.getPlayer(playerUUID) ?: return false, "所持金が足りません")
        }
        Scoreboard().set("money", playerUUID, money)
        Player().sendActionBar(Bukkit.getPlayer(playerUUID) ?: return true, "${ChatColor.RED}-$remove")
        return true
    }
}
