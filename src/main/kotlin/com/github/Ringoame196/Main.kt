package com.github.Ringoame196

import com.github.Ringoame196.Data.Config
import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Entity.Player
import com.github.Ringoame196.Event.Events
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        val event = Events(this)
        server.pluginManager.registerEvents(event, this)

        Yml().makePluginFolder(this)
        Yml().makePlayerDataFolder(this)
        Recipe().add(this)

        Scoreboard().delete("evaluationVote")
        Scoreboard().make("evaluationVote", "evaluationVote")
        Scoreboard().make("protectionContract", "protectionContract")
        Scoreboard().make("admingift", "admingift")
        Scoreboard().make("haveEnderChest", "haveEnderChest")
        Scoreboard().make("blockCount", "blockCount")
        Scoreboard().make("money", "money")
        Scoreboard().make("status_Power", "status_Power")
        Scoreboard().make("status_HP", "status_HP")
        Scoreboard().make("cookCount", "cookCount")
        Scoreboard().make("cookLevel", "cookLevel")
        Scoreboard().make("playerRating", "playerRating")

        saveDefaultConfig()
        Config(PluginData.DataManager, config).getDatabaseinfo()
        Config(PluginData.DataManager, config).getDiscordWebhook()
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) {
            PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { Player.PlayerData() }.titleMoneyBossbar?.removeAll()
        }
        super.onDisable()
    }
}
