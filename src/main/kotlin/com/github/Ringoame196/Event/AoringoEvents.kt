package com.github.Ringoame196.Event

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

class AoringoEvents {
    fun onErrorEvent(player: Player, message: String) {
        player.sendMessage("${ChatColor.RED}$message")
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
    }
}
