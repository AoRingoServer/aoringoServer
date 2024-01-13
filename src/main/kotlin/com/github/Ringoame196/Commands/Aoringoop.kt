package com.github.Ringoame196.Commands

import com.github.Ringoame196.ResourcePack
import com.github.Ringoame196.Shop.FshopManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Aoringoop(plugin: Plugin) : CommandExecutor, TabExecutor {
    private var player: Player? = null
    private val subCommand = mapOf(
        "updateResourcePack" to { ResourcePack(plugin).update() },
        "testWorld" to { player?.teleport(Bukkit.getWorld("testworld")?.spawnLocation ?: player?.location!!) },
        "resetShopLand" to { FshopManager().resetShopLand(plugin) }
    )
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        player = sender as Player
        subCommand[args[0]]?.invoke()
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return subCommand.keys.toMutableList()
    }
}
