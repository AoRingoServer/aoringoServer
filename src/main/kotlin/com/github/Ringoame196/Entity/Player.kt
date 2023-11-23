package com.github.Ringoame196.Entity

import com.github.Ringoame196.Database
import com.github.Ringoame196.Scoreboard
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player

class Player {
    fun setName(player: Player) {
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
    fun setPlayerData(player: Player) {
        val uuid = player.uniqueId.toString()
        val playerName = player.name

        if (!Database().isExists("playerData", "uuid", uuid)) {
            // プレイヤーデータが登録されていない場合は新規登録
            Database().insertStringString("playerData", "uuid", "name", uuid, playerName)
        } else {
            // プレイヤーデータが既に登録されている場合は名前を更新
            Database().updateStrin("playerData", "uuid", "name", uuid, playerName)
        }
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
}
