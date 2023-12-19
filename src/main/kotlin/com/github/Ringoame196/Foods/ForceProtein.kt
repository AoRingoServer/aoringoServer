package com.github.Ringoame196.Foods

import com.github.Ringoame196.Entity.AoringoPlayer

class ForceProtein :PowerUpFood{
    override fun powerUp(aoringoPlayer: AoringoPlayer) {
        aoringoPlayer.addPower()
    }
}