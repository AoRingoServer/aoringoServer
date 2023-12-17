package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class TeleportApplication : Application {
    override fun getCustomModelData(): Int {
        return 4
    }
    override fun openGUI(player: Player, plugin: Plugin) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, ItemManager().make(Material.CHEST, "${ChatColor.GOLD}ロビー"))
        gui.setItem(3, ItemManager().make(Material.GRASS_BLOCK, "${ChatColor.GREEN}生活ワールド"))
        gui.setItem(5, ItemManager().make(Material.DIAMOND_PICKAXE, "${ChatColor.AQUA}資源ワールド"))
        gui.setItem(7, ItemManager().make(Material.QUARTZ_BLOCK, "${ChatColor.YELLOW}ショップ"))
        gui.setItem(19, ItemManager().make(Material.BEDROCK, "${ChatColor.RED}イベント"))
        player.openInventory(gui)
    }
}
