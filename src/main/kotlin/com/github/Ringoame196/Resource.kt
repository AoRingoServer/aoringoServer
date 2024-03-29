package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Worlds.HardcoreWorld
import com.github.Ringoame196.Worlds.WorldManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

class Resource(val plugin: Plugin) {
    private val worldNames = mutableListOf<String>(
        "${ChatColor.GREEN}資源ワールド",
        "${ChatColor.RED}ハードコア資源ワールド",
        "${ChatColor.RED}ネザー"
    )
    fun createSelectTpGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.GREEN}資源テレポート")
        val guiSize = worldNames.size
        val amount: Int = 9 / guiSize
        var inventoryNumber = - 2
        for (worldName in worldNames) {
            inventoryNumber += amount
            val item = ItemManager().make(Material.CRAFTING_TABLE, worldName)
            gui.setItem(inventoryNumber, item)
        }
        return gui
    }
    fun guiClick(player: Player, itemName: String) {
        val aoringoPlayer = AoringoPlayer(player)
        val worldManager = WorldManager(plugin)
        val worldID = worldManager.getWorldID(itemName)
        if (worldID == "hardcore") {
            if (HardcoreWorld().isBan(player, plugin)) {
                aoringoPlayer.sendErrorMessage("あなたはハードコアワールドで死んでBANされたため 移動することができませんでした")
                return
            }
        }
        aoringoPlayer.teleporterWorld(worldID ?: return)
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
}
