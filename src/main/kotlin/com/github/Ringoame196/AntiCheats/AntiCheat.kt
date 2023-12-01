package com.github.Ringoame196.AntiCheats

import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Entity.Player
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.UUID

class AntiCheat {
    fun speed(player: org.bukkit.entity.Player, plugin: Plugin, message: String) {
        val location = player.location.clone()
        PluginData.DataManager.playerDataMap.getOrPut(UUID.fromString(player.uniqueId.toString())) { Player.PlayerData() }.speedMeasurement = true
        Bukkit.getScheduler().runTaskLater(
            plugin,
            Runnable {
                PluginData.DataManager.playerDataMap.getOrPut(UUID.fromString(player.uniqueId.toString())) { Player.PlayerData() }.speedMeasurement = false
                if (location.world == player.location.world && Player().calculateDistance(location, player.location) >= 15 && Player().calculateDistance(location, player.location) <= 100) {
                    player.teleport(location)
                    player.sendMessage(message)
                }
            },
            30L
        ) // 20Lは1秒を表す（1秒 = 20ticks）
    }
}
