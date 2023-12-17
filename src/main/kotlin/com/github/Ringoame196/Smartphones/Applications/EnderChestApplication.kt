package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Application
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class EnderChestApplication : Application {
    override fun getcustomModelData(): Int {
        return 1
    }
    override fun openGUI(player: Player, plugin: Plugin) {
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable {
                Bukkit.dispatchCommand(player, "enderchest")
            }
        )
    }
}
