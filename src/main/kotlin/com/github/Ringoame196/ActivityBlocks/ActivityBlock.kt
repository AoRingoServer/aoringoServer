package com.github.Ringoame196.ActivityBlocks

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.event.player.PlayerInteractEvent

interface ActivityBlock {
    fun clickBlock(e: PlayerInteractEvent, aoringoPlayer: AoringoPlayer)
}
