package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.GUIs.WGGUI
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class LandProtectionApplication : Application {
    override fun getCustomModelData(): Int {
        return 6
    }
    override fun bootApplication(player: Player, plugin: Plugin) {
        val gui = WGGUI().createGUI(player)
        player.openInventory(gui)
    }
}
