package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import com.github.Ringoame196.Data.WorldGuard
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import org.bukkit.entity.Player

class DeletionMember : LandProtectionApplicationButton {
    override fun click(player: Player, shift: Boolean) {
        val aoringoPlayer = AoringoPlayer(player)
        if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
            aoringoPlayer.sendErrorMessage("自分の保護土地内で実行してください")
            return
        }
        LandPurchase().removeMemberGUI(player, WorldGuard().getName(player.location) ?: return)
    }
}
