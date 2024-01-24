package com.github.Ringoame196.Transmissions

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.TextChannel
import org.bukkit.plugin.java.JavaPlugin

class DiscordBot(plugin: JavaPlugin) {
    private lateinit var jda: net.dv8tion.jda.api.JDA

    fun startBot() {
        val token = "YOUR_DISCORD_BOT_TOKEN"

        jda = JDABuilder.createDefault(token).build()
        // この時点でDiscord Botが起動する
    }

    fun sendChatToDiscord(playerName: String, message: String) {
        // Discordにチャットメッセージを送信する
        val channelId = "YOUR_DISCORD_CHANNEL_ID" // DiscordのチャンネルIDを指定
        val textChannel: TextChannel? = jda.getTextChannelById(channelId)

        textChannel?.sendMessage("[$playerName] $message")?.queue()
    }
}
