package com.github.Ringoame196.ActivityBlocks

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerInteractEvent

class SmithingTable : ActivityBlock {
    override fun clickBlock(e: PlayerInteractEvent, aoringoPlayer: AoringoPlayer) {
        if (aoringoPlayer.acquisitionJob() == "${ChatColor.GRAY}鍛冶屋") { return }
        e.isCancelled = true
        aoringoPlayer.sendErrorMessage("${ChatColor.RED}鍛冶屋以外は使用することができません")
    }
}
