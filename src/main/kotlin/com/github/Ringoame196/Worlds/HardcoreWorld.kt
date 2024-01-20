package com.github.Ringoame196.Worlds

import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class HardcoreWorld {
    private val yml = Yml()
    fun toBan(player: Player, plugin: Plugin) {
        yml.addToList(plugin, "Survival", "hardcore", "banPlayer", player.uniqueId.toString())
        player.sendMessage("${ChatColor.RED}あなたは死んだため ハードコアワールドからBANされました")
    }
    fun isBan(player: Player, plugin: Plugin): Boolean {
        return yml.getList(plugin, "Survival", "hardcore", "banPlayer")?.contains(player.uniqueId.toString()) ?: false
    }
    fun resetHardCoreWorld(plugin: Plugin) {
        Yml().setList(plugin, "Survival", "hardcore", "banPlayer", mutableListOf())
        Bukkit.broadcastMessage("${ChatColor.RED}[ハードコア] ハードコアのBANをリセットされました")
    }
}
