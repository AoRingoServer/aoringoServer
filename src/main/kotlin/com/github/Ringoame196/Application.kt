package com.github.Ringoame196

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

interface Application {
    fun getcustomModelData(): Int
    fun openGUI(player: Player, plugin: Plugin)
}