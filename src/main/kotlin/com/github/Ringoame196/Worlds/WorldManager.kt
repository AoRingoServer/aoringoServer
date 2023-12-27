package com.github.Ringoame196.Worlds

import com.github.Ringoame196.Yml
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin

class WorldManager(plugin: Plugin) {
    private val yml = Yml()
    private val worldYml = yml.getYml(plugin, "", "World")
    private val map = worldYml.getValues(true)
    fun getWorldName(targetWorldID: String): String {
        return map.entries.find {
            it.value == targetWorldID
        }?.key ?: "${ChatColor.RED}未登録"
    }
    fun getWorldID(targetWorldName: String): String {
        return map[targetWorldName].toString()
    }
}
