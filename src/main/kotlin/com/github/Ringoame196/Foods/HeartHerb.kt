package com.github.Ringoame196.Foods

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.entity.Player

class HeartHerb:PowerUpFood {
    override fun powerUp(aoringoPlayer: AoringoPlayer) {
        aoringoPlayer.addMaxHP()
    }
}