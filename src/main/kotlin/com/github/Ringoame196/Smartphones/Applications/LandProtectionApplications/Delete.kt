package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Delete : LandProtectionApplicationButton {
    override fun click(player: Player, shift: Boolean) {
        val worldGuard = WorldGuard()
        val aoringoPlayer = AoringoPlayer(player)
        if (worldGuard.getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
            aoringoPlayer.sendErrorMessage("自分の保護土地内で実行してください")
            return
        }
        if (!shift) { return }
        worldGuard.delete(player, WorldGuard().getName(player.location))
        player.sendMessage("${ChatColor.RED}保護を削除しました")
    }
}
