package com.github.Ringoame196.Data

import com.github.Ringoame196.Entity.AoringoPlayer
import java.util.UUID

class PluginData {
    object DataManager {
        var serverlog: String? = null
        var shopPromotionWebhook: String? = null
        val playerDataMap: MutableMap<UUID, AoringoPlayer.PlayerData> = mutableMapOf()
    }
}
