package com.github.Ringoame196.Entity

import org.bukkit.Location
import org.bukkit.entity.ItemFrame

class ItemFrame {
    fun summonItemFrame(location: Location):ItemFrame{
        val world = location.world
        return world?.spawn(location, org.bukkit.entity.ItemFrame::class.java)!!
    }
    fun
}