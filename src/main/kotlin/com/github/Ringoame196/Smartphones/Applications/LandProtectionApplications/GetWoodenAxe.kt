package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GetWoodenAxe : LandProtectionApplicationButton {
    override fun click(player: Player, shift: Boolean) {
        player.inventory.addItem(ItemStack(Material.WOODEN_AXE))
    }
}
