package com.github.Ringoame196.Shop

import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin

class ShopLand {
    fun resetShopLand(plugin: Plugin) {
        val list = Yml().getList(plugin, "conservationLand", "", "protectedName") ?: return
        for (name in list) {
            if (Scoreboard().getValue("protectionContract", name) == 2) {
                Scoreboard().reduce("protectionContract", name, 1)
                continue
            }
            WorldGuard().reset(name, Bukkit.getWorld("shop") ?: return)
            Yml().removeToList(plugin, "", "conservationLand", "protectedName", name)
        }
        Bukkit.broadcastMessage("${ChatColor.RED}[ショップ] ショップの購入土地がリセットされました")
    }
}
