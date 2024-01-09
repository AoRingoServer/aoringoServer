package com.github.Ringoame196.Worlds

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Resource
import com.github.Ringoame196.Yml
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class WorldManager(val plugin: Plugin) {
    private val yml = Yml()
    private val worldYml = yml.getYml(plugin, "", "World")
    private val map = worldYml.getValues(true)
    fun getWorldName(targetWorldID: String): String {
        return map.entries.find {
            it.value == targetWorldID
        }?.key ?: "${ChatColor.RED}未登録"
    }
    fun getWorldID(targetWorldName: String): String? {
        return map[targetWorldName]?.toString()
    }
    fun changeRespawn(worldName: String, player: Player): String? {
        val respawnTarget = mapOf(
            "Survival" to "world",
            "dungeon" to "world",
            "pvpSurvival" to "world",
            "dungeonBoss" to "dungeon",
            "hardcore" to "world"
        )
        AoringoPlayer(player).reduceFoodLevel(plugin)
        if (respawnTarget.contains(worldName)) {
            return respawnTarget[worldName]
        }
        return null
    }
    fun survivalTeleport(aoringoPlayer: AoringoPlayer, selectBlock: Material) {
        val supportedWorld = mapOf(
            Material.IRON_BLOCK to { aoringoPlayer.player.openInventory(Resource(plugin).createSelectTpGUI()) },
            Material.QUARTZ_BLOCK to { aoringoPlayer.teleporterWorld("shop") },
            Material.GOLD_BLOCK to { aoringoPlayer.teleporterWorld("Home") }
        )
        if (supportedWorld.contains(selectBlock)) {
            supportedWorld[selectBlock]?.invoke()
        }
    }
}
