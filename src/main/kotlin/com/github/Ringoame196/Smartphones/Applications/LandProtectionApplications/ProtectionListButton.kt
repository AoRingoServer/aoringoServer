package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import org.bukkit.entity.Player

class ProtectionListButton:LandProtectionApplicationButton {
    override fun click(player: Player,shift:Boolean) {
        player.closeInventory()
        LandPurchase().listRegionsInWorld(player)
    }
}