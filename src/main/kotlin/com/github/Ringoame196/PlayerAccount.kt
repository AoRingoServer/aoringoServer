package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer

class PlayerAccount(private val player:AoringoPlayer) : Account {
    override fun getAccountID(): String {
        return player.player.uniqueId.toString()
    }

}