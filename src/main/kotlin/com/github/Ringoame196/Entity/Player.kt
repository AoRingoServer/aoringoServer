package com.github.Ringoame196.Entity

import com.github.Ringoame196.Job.Job
import com.github.Ringoame196.Scoreboard
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Player {
    data class PlayerData(
        var smartphone: MutableList<String>? = null
    )
    fun setName(player: Player) {
        val jobID = Scoreboard().getValue("job", player.uniqueId.toString())
        val jobColor = mutableListOf("", "${ChatColor.DARK_PURPLE}", "${ChatColor.DARK_RED}", "${ChatColor.GRAY}")
        player.setDisplayName("${jobColor[jobID]}${player.displayName}@${Job().get(player)}")
        player.setPlayerListName("${jobColor[jobID]}${player.playerListName}")
        if (player.isOp) {
            player.setDisplayName("${ChatColor.YELLOW}[運営]" + player.displayName)
            player.setPlayerListName("${ChatColor.YELLOW}[運営]" + player.playerListName)
        }
    }
    private fun levelupMessage(player: Player, message: String) {
        player.sendMessage(message)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
    fun addPower(player: Player) {
        Scoreboard().add("status_HP", player.uniqueId.toString(), 1)
        levelupMessage(player, "${ChatColor.RED}最大HPアップ！！")
    }
    fun addMaxHP(player: Player) {
        Scoreboard().add("status_Power", player.uniqueId.toString(), 1)
        levelupMessage(player, "${ChatColor.YELLOW}パワーアップ！！")
    }
    fun sendActionBar(player: Player, message: String) {
        val actionBarMessage = ChatColor.translateAlternateColorCodes('&', message)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(actionBarMessage))
    }
    fun getPlayersInRadius(center: Location, radius: Double): List<Player>? {
        val playersInRadius = mutableListOf<Player>()

        for (player in center.world?.players ?: return null) {
            val playerLocation = player.location
            val distance = center.distance(playerLocation)

            if (distance <= radius) {
                // 半径内にいるプレイヤーをリストに追加
                playersInRadius.add(player)
            }
        }

        return playersInRadius
    }
    fun addPermission(player: Player, plugin: Plugin, permission: String) {
        val permissions = player.addAttachment(plugin) // "plugin" はプラグインのインスタンスを指します
        permissions.setPermission(permission, true)
        player.recalculatePermissions()
    }
    fun setTab(player: Player) {
        player.playerListHeader = "${ChatColor.AQUA}青りんごサーバー"
        player.playerListFooter = "${ChatColor.YELLOW}" + when (player.world.name) {
            "world" -> "ロビーワールド"
            "Survival" -> "資源ワールド"
            "Nether" -> "ネザー"
            "shop" -> "ショップ"
            "event" -> "イベントワールド"
            "Home" -> "建築ワールド"
            else -> "${ChatColor.RED}未設定"
        }
    }
}
