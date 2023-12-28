package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import com.github.Ringoame196.PluginData
import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ProtectionCreationButton : LandProtectionApplicationButton {
    override fun click(player: Player, shift: Boolean) {
        PluginData.DataManager.playerDataMap[player.uniqueId]
        player.closeInventory()
        PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { AoringoPlayer.PlayerData() }.chatSettingItem = "rg"
        player.sendMessage("${ChatColor.AQUA}[土地保護]保護名を入力してください")
    }
}
