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
    private val uneiAccount: String = "unei"
    private fun bossbarTitle(accountID: String): String {
        return "${ChatColor.GOLD}所持金${formalCurrency(Money().get(accountID))}円"
    }
    fun get(accountID: String): Int {
        return Scoreboard().getValue("money", accountID)
    }
    fun add(accountID: String, add: Int, unei: Boolean) {
        val money = get(accountID) + add
        if (unei) {
            if (get(uneiAccount) < add) {
                AoringoEvents().onErrorEvent(Bukkit.getPlayer(UUID.fromString(accountID)) ?: return, "運営のお金が不足したため 運営手形が発行されました")
                Bukkit.getPlayer(UUID.fromString(accountID))?.sendMessage("${ChatColor.GOLD}運営に発行された手形をお渡しください")
                Bukkit.getPlayer(UUID.fromString(accountID))?.inventory?.addItem(Item().make(Material.PAPER, "${ChatColor.GOLD}運営手形(${add}円)", "手形を運営に渡してください", 11, 1))
                return
            }
            remove(uneiAccount, add, false)
        }
        set(accountID, money)
        if (!isUUIDFormat(accountID)) { return }
        Player().sendActionBar(Bukkit.getPlayer(UUID.fromString(accountID)) ?: return, "${ChatColor.GREEN}+$add")
    }
    fun remove(accountID: String, remove: Int, unei: Boolean): Boolean {
        val money = get(accountID) - remove
        if (money < 0) {
            AoringoEvents().onErrorEvent(Bukkit.getPlayer(UUID.fromString(accountID)) ?: return false, "所持金が足りません")
        }
        if (unei) {
            add(uneiAccount, remove, false)
        }
        set(accountID, money)
        if (!isUUIDFormat(accountID)) { return true }
        Player().sendActionBar(Bukkit.getPlayer(UUID.fromString(accountID)) ?: return true, "${ChatColor.RED}-$remove")
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
        val bossbar = Bukkit.createBossBar(bossbarTitle(player.uniqueId.toString()), BarColor.BLUE, BarStyle.SOLID)
        bossbar.addPlayer(player)
        PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { Player.PlayerData() }.titleMoneyBossbar = bossbar
    }
    fun set(accountID: String, money: Int) {
        Scoreboard().set("money", accountID, money)
        if (!isUUIDFormat(accountID)) { return }
        val bossbar = PluginData.DataManager.playerDataMap.getOrPut(UUID.fromString(accountID)) { Player.PlayerData() }.titleMoneyBossbar
        if (bossbar == null) {
            createBossbar(Bukkit.getPlayer(UUID.fromString(accountID)) ?: return)
        } else {
            bossbar.setTitle(bossbarTitle(accountID))
        }
    }
    fun formalCurrency(money: Int): String {
        return money.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
    }
}
