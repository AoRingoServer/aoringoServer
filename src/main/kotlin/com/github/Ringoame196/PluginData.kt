package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.util.UUID

class PluginData {
    object DataManager {
        var serverlogWebhook: String? = null
        var shopPromotionWebhook: String? = null
        val playerDataMap: MutableMap<UUID, AoringoPlayer.PlayerData> = mutableMapOf()
        var FoodData: YamlConfiguration? = null
    }
}
