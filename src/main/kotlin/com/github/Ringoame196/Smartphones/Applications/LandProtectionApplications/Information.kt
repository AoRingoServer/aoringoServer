package com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications

import com.github.Ringoame196.Items.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class Information : LandProtectionApplicationButton {
    override fun click(player: Player, shift: Boolean) {
        val gui = player.openInventory.topInventory
        gui.setItem(2, ItemManager().make(Material.MAP, "${ChatColor.YELLOW}保護情報",))
        gui.setItem(4, ItemManager().make(Material.PLAYER_HEAD, "${ChatColor.AQUA}メンバー追加"))
        gui.setItem(6, ItemManager().make(Material.PLAYER_HEAD, "${ChatColor.RED}メンバー削除"))
        gui.setItem(8, ItemManager().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}削除", "${ChatColor.DARK_RED}シフトで実行"))
    }
}
