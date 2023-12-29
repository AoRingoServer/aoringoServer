package com.github.Ringoame196

import com.github.Ringoame196.Data.Web
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class ResourcePack(val plugin: Plugin) {
    private val configFile = File(plugin.dataFolder, "/config.yml")
    private val yml = Yml()
    private val resourcePackId = "ResourcePack.URL"
    private fun save(key: String, text: String) {
        val yamlConfiguration = YamlConfiguration.loadConfiguration(configFile)
        // 既存のデータを上書き
        yamlConfiguration.set(key, text)

        try {
            yamlConfiguration.save(configFile)
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
    fun update() {
        val gasID = "ResourcePack.GET"
        val configFIle = yml.getYml(plugin, "", "config")
        val url = configFIle.getString(resourcePackId)
        val gas = configFIle.getString(gasID) ?: return

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            Runnable {
                val newURL = Web().get(gas).toString()
                Bukkit.getScheduler().runTask(
                    plugin,
                    Runnable {
                        if (url == newURL) { return@Runnable }
                        save(resourcePackId, newURL)
                        sendUpdateMessage()
                    }
                )
            }
        )
    }
    private fun sendUpdateMessage() {
        Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー]リソースパックが更新されました")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー]次回参加時に適応されます")
    }
    fun adaptation(player: Player) {
        val url = YamlConfiguration.loadConfiguration(configFile).getString(resourcePackId) ?: return
        player.setResourcePack(url)
    }
}
