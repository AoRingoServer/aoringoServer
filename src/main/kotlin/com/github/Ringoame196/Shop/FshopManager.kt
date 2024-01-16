package com.github.Ringoame196.Shop

import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FshopManager {
    fun resetShopLand(plugin: Plugin) {
        val list = Yml().getList(plugin, "conservationLand", "", "protectedName") ?: return
        for (name in list) {
            if (Scoreboard().getValue("protectionContract", name) == 2) {
                Scoreboard().reduce("protectionContract", name, 1)
                continue
            }
            WorldGuard().reset(name, Bukkit.getWorld("shop") ?: return)
            Yml().removeToList(plugin, "", "conservationLand", "protectedName", name)
        }
        Bukkit.broadcastMessage("${ChatColor.RED}[ショップ] ショップの購入土地がリセットされました")
    }
    fun saveShopData(data: Map<String, Any>, shopUUID: String, plugin: Plugin) {
        val filePath = "${plugin.dataFolder.path}/shopData/$shopUUID.yml"
        val dumperOptions = DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK

        val yaml = Yaml(dumperOptions)
        try {
            FileWriter(filePath).use { writer ->
                yaml.dump(data, writer)
            }
            println("YAMLファイルが正常に保存されました。")
        } catch (e: IOException) {
            e.printStackTrace()
            println("YAMLファイルの保存中にエラーが発生しました。")
        }
    }
}
