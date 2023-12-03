package com.github.Ringoame196

import com.github.Ringoame196.Data.Web
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class ResourcePack(plugin: Plugin) {
    private val configFile = File(plugin.dataFolder, "/config.yml")
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
        val ymlConfiguration = YamlConfiguration.loadConfiguration(configFile)
        val url = ymlConfiguration.getString("ResourcePack.URL")
        val gas = ymlConfiguration.getString("ResourcePack.GET") ?: return

        val newURL = Web().get(gas).toString()
        if (url == newURL) { return }
        save("ResourcePack.URL", newURL)
        Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー]リソースパックが更新されました")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー]次回参加時に適応されます")
    }
    fun adaptation(player: Player) {
        val url = YamlConfiguration.loadConfiguration(configFile).getString("ResourcePack.URL") ?: return
        player.setResourcePack(url)
    }
}
