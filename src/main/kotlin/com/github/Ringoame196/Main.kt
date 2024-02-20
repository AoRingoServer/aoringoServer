package com.github.Ringoame196

import com.github.Ringoame196.Commands.AoringoopCommand
import com.github.Ringoame196.Commands.FshopCommand
import com.github.Ringoame196.Commands.MoneyCommand
import com.github.Ringoame196.Commands.WriteCommand
import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    override fun onEnable() {
        val yml = Yml()
        val scoreboard = Scoreboard()
        val event = Events(this)
        server.pluginManager.registerEvents(event, this)
        getCommand("money")!!.setExecutor(MoneyCommand())
        getCommand("aoringoop")!!.setExecutor(AoringoopCommand(this))
        getCommand("write")!!.setExecutor(WriteCommand())
        getCommand("fshop")!!.setExecutor(FshopCommand(this))

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
        val resourcesFileNames = mutableListOf("World.yml","FoodData.yml","Application.yml","DropItem.yml")
        for (fileName in resourcesFileNames){
            val path = "${this.dataFolder.path}/$fileName"
            val file = File(path)
            if (file.exists()) { continue }
            saveResource(fileName,false)
        }
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
