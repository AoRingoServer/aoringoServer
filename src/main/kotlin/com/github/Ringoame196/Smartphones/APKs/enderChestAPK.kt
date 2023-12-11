package com.github.Ringoame196.Smartphones.APKs

import com.github.Ringoame196.APKs
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class enderChestAPK:APKs {
    override val customModelData: Int = 1
    override fun openGUI(player:Player,plugin:Plugin) {
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable {
                Bukkit.dispatchCommand(player, "enderchest")
            }
        )
    }
}