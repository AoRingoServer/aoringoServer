package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import com.github.Ringoame196.Data.WorldGuard
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ProtectionInformation : LandProtectionApplicationButton {
    override fun click(player: Player, shift: Boolean) {
        val worldGuard = WorldGuard()
        player.closeInventory()
        player.sendMessage("${ChatColor.YELLOW}-----保護情報-----")
        player.sendMessage("${ChatColor.GOLD}保護名:${worldGuard.getName(player.location)}")
        player.sendMessage("${ChatColor.YELLOW}オーナー:" + if (worldGuard.getOwnerOfRegion(player.location)?.contains(player.uniqueId) == true) { "${ChatColor.GOLD}あなたはオーナーです" } else { "${ChatColor.RED}あなたはオーナーではありません" })
        player.sendMessage("${ChatColor.AQUA}メンバー:" + if (worldGuard.getMemberOfRegion(player.location)?.contains(player.uniqueId) == true) { "${ChatColor.GOLD}あなたはメンバーです" } else { "${ChatColor.RED}あなたはメンバーではありません" })
    }
}
