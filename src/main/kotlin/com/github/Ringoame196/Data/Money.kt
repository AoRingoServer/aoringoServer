package com.github.Ringoame196.Data

import com.github.Ringoame196.Entity.Player
import com.github.Ringoame196.Event.AoringoEvents
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import java.util.UUID

class Money {
    fun get(playerUUID: String): Int {
        return Scoreboard().getValue("money", playerUUID)
    }
    fun add(playerUUID: String, add: Int, unei: Boolean) {
        val money = get(playerUUID) + add
        if (unei) {
            if (get("unei") < add) {
                AoringoEvents().onErrorEvent(Bukkit.getPlayer(UUID.fromString(playerUUID)) ?: return, "運営のお金が不足したため 運営手形が発行されました")
                Bukkit.getPlayer(UUID.fromString(playerUUID))?.sendMessage("${ChatColor.GOLD}運営に発行された手形をお渡しください")
                Bukkit.getPlayer(UUID.fromString(playerUUID))?.inventory?.addItem(Item().make(Material.PAPER, "${ChatColor.GOLD}運営手形(${add}円)", "手形を運営に渡してください", 11, 1))
                return
            }
            remove("unei", add, false)
        }
        set(playerUUID, money)
        if (!isUUIDFormat(playerUUID)) { return }
        Player().sendActionBar(Bukkit.getPlayer(UUID.fromString(playerUUID)) ?: return, "${ChatColor.GREEN}+$add")
    }
    fun remove(playerUUID: String, remove: Int, unei: Boolean): Boolean {
        val money = get(playerUUID) - remove
        if (money < 0) {
            AoringoEvents().onErrorEvent(Bukkit.getPlayer(UUID.fromString(playerUUID)) ?: return false, "所持金が足りません")
        }
        if (unei) {
            add("unei", remove, false)
        }
        set(playerUUID, money)
        if (!isUUIDFormat(playerUUID)) { return true }
        Player().sendActionBar(Bukkit.getPlayer(UUID.fromString(playerUUID)) ?: return true, "${ChatColor.RED}-$remove")
        return true
    }
    fun isUUIDFormat(input: String): Boolean {
        return try {
            UUID.fromString(input)
            true
        } catch (ex: IllegalArgumentException) {
            false
        }
    }
    fun createBossbar(player: org.bukkit.entity.Player) {
        val bossbar = Bukkit.createBossBar("${ChatColor.GOLD}所持金${Money().get(player.uniqueId.toString())}円", BarColor.YELLOW, BarStyle.SOLID)
        bossbar.addPlayer(player)
        PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { Player.PlayerData() }.titleMoneyBossbar = bossbar
    }
    fun set(playerUUID: String, money: Int) {
        Scoreboard().set("money", playerUUID, money)
        if (!isUUIDFormat(playerUUID)) { return }
        val bossbar = PluginData.DataManager.playerDataMap.getOrPut(UUID.fromString(playerUUID)) { Player.PlayerData() }.titleMoneyBossbar
        if (bossbar == null) {
            createBossbar(Bukkit.getPlayer(UUID.fromString(playerUUID)) ?: return)
        } else {
            bossbar.setTitle("${ChatColor.GOLD}所持金:${money}円")
        }
    }
}
