package com.github.Ringoame196.AntiCheats

import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Discord
import com.github.Ringoame196.Entity.Player
import com.github.Ringoame196.Scoreboard
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import java.util.UUID
import kotlin.random.Random

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
    fun nuker(player:org.bukkit.entity.Player,plugin: Plugin,out:Int){
        val blockCount = Scoreboard().getValue("blockCount", player.name)
        if (blockCount == 0) {
            Bukkit.getScheduler().runTaskLater(
                plugin,
                Runnable {
                    Scoreboard().set("blockCount", player.name, 0)
                },
                100L
            )
        }
        Scoreboard().add("blockCount", player.name, 1)
        if (blockCount <= out) { return }
            val number = Random.nextInt(1, 9999)
            val message = "${ChatColor.RED}[アンチチート]チートを感知したためBANされました。 誤BANの場合は運営まで連絡をしてください(ナンバー$number)"
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.name, message, null, "AntiNuker")
            player.kickPlayer(message)
            Discord().setJson(player, "AntiCheatプラグイン", "https://static.wikia.nocookie.net/minecraft_ja_gamepedia/images/2/27/Barrier.gif/revision/latest?cb=20201228114801", "25500", "BAN", "5秒以内に${out}ブロック以上破壊したためBANされました(ナンバー$number)", PluginData.DataManager.serverlog ?: return)
    }
}
