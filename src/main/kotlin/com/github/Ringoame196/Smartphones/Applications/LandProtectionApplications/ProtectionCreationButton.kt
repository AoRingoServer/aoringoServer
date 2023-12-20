package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ProtectionCreationButton : LandProtectionApplicationButton {
    override fun click(player: Player, shift: Boolean) {
        player.closeInventory()
        player.addScoreboardTag("rg")
        player.sendMessage("${ChatColor.AQUA}[土地保護]保護名を入力してください")
    }
}
