package com.github.Ringoame196

import com.github.Ringoame196.Commands.Money
import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        val yml = Yml()
        val scoreboard = Scoreboard()
        val event = Events(this)
        server.pluginManager.registerEvents(event, this)
        getCommand("money")!!.setExecutor(Money())

        yml.makePluginFolder(this)
        yml.makePlayerDataFolder(this)
        Recipe().add(this)

        scoreboard.delete("evaluationVote")
        scoreboard.make("evaluationVote", "evaluationVote")
        scoreboard.make("protectionContract", "protectionContract")
        scoreboard.make("admingift", "admingift")
        scoreboard.make("haveEnderChest", "haveEnderChest")
        scoreboard.make("money", "money")
        scoreboard.make("status_Power", "status_Power")
        scoreboard.make("status_HP", "status_HP")
        scoreboard.make("cookCount", "cookCount")
        scoreboard.make("cookLevel", "cookLevel")
        scoreboard.make("playerRating", "playerRating")

        saveDefaultConfig()
        saveResource("World.yml", false)
        saveResource("FoodData.yml", false)
        Config(PluginData.DataManager, config).getDatabaseinfo()
        Config(PluginData.DataManager, config).getDiscordWebhook()
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) {
            val playerData = PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { AoringoPlayer.PlayerData() }
            val bossbar = playerData.titleMoneyBossbar ?: continue
            bossbar.removeAll()
        }
        super.onDisable()
    }
}
