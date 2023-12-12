package com.github.Ringoame196.Smartphones.APKs

import com.github.Ringoame196.APKs
import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class TeleportAPK : APKs {
    override val customModelData = 4
    override fun openGUI(player: Player, plugin: Plugin) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.CHEST, "${ChatColor.GOLD}ロビー"))
        gui.setItem(3, Item().make(Material.GRASS_BLOCK, "${ChatColor.GREEN}生活ワールド"))
        gui.setItem(5, Item().make(Material.DIAMOND_PICKAXE, "${ChatColor.AQUA}資源ワールド"))
        gui.setItem(7, Item().make(Material.QUARTZ_BLOCK, "${ChatColor.YELLOW}ショップ"))
        gui.setItem(19, Item().make(Material.BEDROCK, "${ChatColor.RED}イベント"))
        player.openInventory(gui)
    }
}
