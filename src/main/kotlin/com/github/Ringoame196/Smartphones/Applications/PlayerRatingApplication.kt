package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin

class PlayerRatingApplication(private val voidScoreboardName: String = "playerRating", private val voidJudgmentScoreboardName: String = "evaluationVote") :
    Application {
    override fun getCustomModelData(): Int {
        return 5
    }
    override fun openGUI(player: Player, plugin: Plugin) {
        val guiSize = 18
        val gui = Bukkit.createInventory(null, guiSize, "${ChatColor.BLUE}プレイヤー評価")
        var i = 0
        for (target in AoringoPlayer(player).getPlayersInRadius(player.location, 10.0) ?: return) {
            gui.addItem(playerHead(target))
            if (i == guiSize) { continue }
            i ++
        }
        player.openInventory(gui)
    }
    private fun playerHead(target: Player): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        val evaluation = getRating(target.uniqueId.toString())
        meta.setDisplayName(target.name)
        meta.setOwningPlayer(target)
        meta.lore = mutableListOf("評価:$evaluation", target.uniqueId.toString())
        item.setItemMeta(meta)
        return item
    }
    private fun getRating(targetUUID: String): Int {
        return Scoreboard().getValue(voidScoreboardName, targetUUID)
    }
    fun voidGUI(player: Player, target: ItemStack) {
        val guiSize = 9
        val aoringoPlayer = AoringoPlayer(player)
        if (Scoreboard().getValue(voidJudgmentScoreboardName, player.name) != 0) {
            aoringoPlayer.sendErrorMessage("評価は1日1回です")
            return
        }
        val gui = Bukkit.createInventory(null, guiSize, "${ChatColor.BLUE}プレイヤー評価")
        val targetPlayerHeadSlot = 2
        val highRatingSlot = 4
        val lowRatingSlot = 6
        gui.setItem(targetPlayerHeadSlot, target)
        gui.setItem(highRatingSlot, ItemManager().make(Material.STONE_BUTTON, "${ChatColor.GREEN}高評価"))
        gui.setItem(lowRatingSlot, ItemManager().make(Material.STONE_BUTTON, "${ChatColor.RED}低評価"))
        player.openInventory(gui)
    }
    fun void(target: ItemStack, button: String, player: Player) {
        val targetUUID = target.itemMeta?.lore?.get(1) ?: return
        val evaluation = getRating(targetUUID)
        Scoreboard().set(voidJudgmentScoreboardName, player.name, 1)
        Scoreboard().set(
            voidScoreboardName, targetUUID,
            when (button) {
                "${ChatColor.GREEN}高評価" -> evaluation + 1
                "${ChatColor.RED}低評価" -> evaluation - 1
                else -> return
            }
        )
        player.closeInventory()
        player.sendMessage("${ChatColor.YELLOW}プレイヤー評価しました")
    }
}
