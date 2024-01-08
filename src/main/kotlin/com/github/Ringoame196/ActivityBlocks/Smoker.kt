package com.github.Ringoame196.ActivityBlocks

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.event.player.PlayerInteractEvent

class Smoker : ActivityBlock {
    override fun clickBlock(e: PlayerInteractEvent, aoringoPlayer: AoringoPlayer) {
        e.isCancelled = true
        aoringoPlayer.sendErrorMessage("ブロックの上部分に食材を持ってクリックしてください")
    }
}
