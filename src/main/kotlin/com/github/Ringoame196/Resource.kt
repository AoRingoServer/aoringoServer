package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class Resource {
    private val worldName = mapOf<String, String>(
        "${ChatColor.GREEN}オーバーワールド1" to "Survival",
        "${ChatColor.GREEN}オーバーワールド2[閉鎖中]" to "world",
        "${ChatColor.GREEN}オーバーワールド3[閉鎖中]" to "world",
        "${ChatColor.RED}ネザー" to "Nether"
    )
    fun createSelectTpGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.GREEN}資源テレポート")
        val guiSize = worldName.size
        val amount: Int = 9 / guiSize
        var inventoryNumber = - 1
        for ((key, value) in worldName) {
            inventoryNumber += amount
            val item = ItemManager().make(Material.CRAFTING_TABLE, key, value)
            gui.setItem(inventoryNumber, item)
        }
        return gui
    }
    fun guiClick(player: Player, itemName: String) {
        val aoringoPlayer = AoringoPlayer(player)
        aoringoPlayer.teleporterWorld(worldName[itemName] ?: "world")
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
}
