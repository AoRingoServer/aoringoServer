package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Worlds.WorldManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class TeleportApplication : Application {
    override fun getCustomModelData(): Int {
        return 4
    }
    override fun bootApplication(player: Player, plugin: Plugin) {
        val itemManager = ItemManager()
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(0, itemManager.make(Material.CRAFTING_TABLE, "${ChatColor.YELLOW}チュートリアル"))
        gui.setItem(1, itemManager.make(Material.CHEST, "${ChatColor.GOLD}ロビー"))
        gui.setItem(3, itemManager.make(Material.GRASS_BLOCK, "${ChatColor.GREEN}生活ワールド"))
        gui.setItem(5, itemManager.make(Material.DIAMOND_PICKAXE, "${ChatColor.GREEN}資源ワールド"))
        gui.setItem(7, itemManager.make(Material.QUARTZ_BLOCK, "${ChatColor.YELLOW}ショップ"))
        player.openInventory(gui)
    }
    private fun getWorldSpawnLocation(worldName: String): Location? {
        return Bukkit.getWorld(worldName)?.spawnLocation
    }
    private fun getWorldID(worldName: String, plugin: Plugin): String? {
        val worldManager = WorldManager(plugin)
        return worldManager.getWorldID(worldName)
    }
    fun teleportWorldFromPlayer(player: Player, worldName: String, plugin: Plugin) {
        val worldID = getWorldID(worldName, plugin)
        val playerLocation = player.location
        val location = getWorldSpawnLocation(worldID ?: return)
        player.teleport(location ?: playerLocation)
    }
}
