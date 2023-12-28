package com.github.Ringoame196.Smartphones.Applications

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class EnderChestApplication : Application {
    override fun getCustomModelData(): Int {
        return 1
    }
    override fun bootApplication(player: Player, plugin: Plugin) {
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable {
                Bukkit.dispatchCommand(player, "enderchest")
            }
        )
    }
}
