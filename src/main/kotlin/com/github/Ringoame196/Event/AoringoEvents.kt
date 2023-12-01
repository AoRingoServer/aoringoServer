package com.github.Ringoame196.Event

import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

class AoringoEvents {
    fun onErrorEvent(player: Player, message: String) {
        player.sendMessage("${ChatColor.RED}$message")
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
    }
    fun onFastJoinEvent(e: PlayerJoinEvent) {
        val player = e.player
        e.joinMessage = "${ChatColor.YELLOW}${player.name}さんが初めてサーバーに参加しました"
        player.inventory.addItem(Item().make(Material.ENCHANTED_BOOK, "${ChatColor.YELLOW}スマートフォン", null, 1, 1))
        player.inventory.addItem(Item().make(Material.NETHER_STAR, "職業スター", null, null, 1))
        player.scoreboardTags.add("member")
        Scoreboard().set("money", player.uniqueId.toString(), 30000)
    }
    fun onActivationTeleporterEvent(player: Player, worldName: String) {
        player.teleport(
            Bukkit.getWorld(
                if (player.world.name == "world") { worldName } else { "world" }
            )?.spawnLocation ?: return
        )
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
}
