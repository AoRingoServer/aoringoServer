package com.github.Ringoame196.Foods

import com.github.Ringoame196.Entity.AoringoPlayer

class HeartHerb : PowerUpFood {
    override fun powerUp(aoringoPlayer: AoringoPlayer) {
        aoringoPlayer.addMaxHP()
    }
}
