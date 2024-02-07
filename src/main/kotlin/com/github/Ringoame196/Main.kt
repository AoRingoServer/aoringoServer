package com.github.Ringoame196

import com.github.Ringoame196.Commands.Aoringoop
import com.github.Ringoame196.Commands.Money
import com.github.Ringoame196.Commands.Write
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Shop.FshopManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        val yml = Yml()
        val scoreboard = Scoreboard()
        val event = Events(this)
        server.pluginManager.registerEvents(event, this)
        getCommand("money")!!.setExecutor(Money())
        getCommand("aoringoop")!!.setExecutor(Aoringoop(this))
        getCommand("write")!!.setExecutor(Write())
        getCommand("fshop")!!.setExecutor(FshopManager())

        yml.makePluginFolder(this)
        yml.makePlayerDataFolder(this)
        Recipe().add(this)

        scoreboard.delete("evaluationVote")
        scoreboard.make("evaluationVote", "evaluationVote")
        scoreboard.make("protectionContract", "protectionContract")
        scoreboard.make("admingift", "admingift")
        scoreboard.make("money", "money")
        scoreboard.make("status_Power", "status_Power")
        scoreboard.make("status_HP", "status_HP")
        scoreboard.make("cookCount", "cookCount")
        scoreboard.make("cookLevel", "cookLevel")
        scoreboard.make("playerRating", "playerRating")

        saveDefaultConfig()
        saveResource("World.yml", true)
        saveResource("FoodData.yml", true)
        saveResource("Application.yml", true)
        saveResource("DropItem.yml", true)
        Config(PluginData.DataManager, config).getDatabaseinfo()
        Config(PluginData.DataManager, config).getDiscordWebhook()

        Yml().callData(this)

        for (player in Bukkit.getOnlinePlayers()) {
            val aoringoPlayer = AoringoPlayer(player)
            aoringoPlayer.moneyUseCase.displayMoney(aoringoPlayer)
        }
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
