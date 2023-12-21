package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class LandProtectionApplication : Application {
    override fun getCustomModelData(): Int {
        return 6
    }
    override fun openGUI(player: Player, plugin: Plugin) {
        val itemManager = ItemManager()
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}WorldGuardGUI")
        gui.setItem(
            2,
            itemManager.make(Material.GOLDEN_AXE, "${ChatColor.YELLOW}保護作成")
        )
        gui.setItem(4, itemManager.make(Material.MAP, "${ChatColor.GREEN}情報"))
        gui.setItem(6, itemManager.make(Material.CHEST, "${ChatColor.AQUA}保護一覧"))
        gui.setItem(8, itemManager.make(Material.WOODEN_AXE, "${ChatColor.GOLD}木の斧ゲット"))
        player.openInventory(gui)
    }
}
