package com.github.Ringoame196

import org.bukkit.configuration.file.FileConfiguration

class Config(private val dataManager: PluginData.DataManager, private val config: FileConfiguration) {
    fun getDatabaseinfo() {
        val host = config.getString("Database.host")
        val port = config.getString("Database.port")
        val databaseName = config.getString("Database.databaseName")
        val userName = config.getString("Database.userName")
        val password = config.getString("Database.password")
        Database.dataBaseinfo.connection = "jdbc:mysql://$host:$port/$databaseName"
        Database.dataBaseinfo.userName = userName ?: return
        Database.dataBaseinfo.password = password ?: return
    }
    fun getDiscordWebhook() {
        dataManager.serverlogWebhook = config.getString("Discord.serverlog")
        dataManager.shopPromotionWebhook = config.getString("Discord.shopPromotion")
    }
}
