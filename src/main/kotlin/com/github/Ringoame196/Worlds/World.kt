package com.github.Ringoame196.Worlds

import org.bukkit.entity.Player

interface World {
    fun getWorldName():String
    fun teleportWorld(player:Player)
}