package com.github.Ringoame196

import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class Resource {
    fun createSelectTpGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.GREEN}資源テレポート")
        gui.setItem(1, Item().make(material = Material.CHEST, name = "${ChatColor.GOLD}ロビー"))
        gui.setItem(4, Item().make(material = Material.GRASS_BLOCK, name = "${ChatColor.GREEN}オーバーワールド"))
        gui.setItem(7, Item().make(material = Material.NETHERRACK, name = "${ChatColor.RED}ネザー"))
        return gui
    }
    fun guiClick(player: Player, itemName: String) {
        when (itemName) {
            "${ChatColor.GOLD}ロビー" -> worldTP(player, "world")
            "${ChatColor.GREEN}オーバーワールド" -> worldTP(player, "Survival")
            "${ChatColor.RED}ネザー" -> worldTP(player, "Nether")
        }
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
    private fun worldTP(player: Player, worldName: String) {
        val world = Bukkit.getWorld(worldName)?.spawnLocation
        player.teleport(world ?: return)
    }
}
