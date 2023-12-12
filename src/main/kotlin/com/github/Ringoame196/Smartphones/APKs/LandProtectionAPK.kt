package com.github.Ringoame196.Smartphones.APKs

import com.github.Ringoame196.APKs
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class LandProtectionAPK:APKs {
    override val customModelData = 6
    override fun openGUI(player: Player, plugin: Plugin) {
        val item = Item()
        val landPurchase = LandPurchase()
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}WorldGuardGUI")
        gui.setItem(
            2,
            item.make(Material.GOLDEN_AXE, "${ChatColor.YELLOW}保護作成", "${landPurchase.calculatePrice(player)}円")
        )
        gui.setItem(4, item.make(Material.MAP, "${ChatColor.GREEN}情報"))
        gui.setItem(6, item.make(Material.CHEST, "${ChatColor.AQUA}保護一覧"))
        gui.setItem(8, item.make(Material.WOODEN_AXE, "${ChatColor.GOLD}木の斧ゲット"))
        player.openInventory(gui)
    }
}