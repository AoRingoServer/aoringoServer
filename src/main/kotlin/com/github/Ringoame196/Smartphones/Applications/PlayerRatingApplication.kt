package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin

class PlayerRatingApplication : Application {
    private val voidScoreboardName: String = "playerRating"
    private val voidJudgmentScoreboardName: String = "evaluationVote"
    override fun getCustomModelData(): Int {
        return 5
    }
    override fun bootApplication(player: Player, plugin: Plugin) {
        player.closeInventory()
        PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { AoringoPlayer.PlayerData() }.chatSettingItem = "playerVoid"
        player.sendMessage("${ChatColor.AQUA}投票したいプレイヤーの名前を入力してください")
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
    private fun getPlayer(targetPlayerName: String): Player? {
        return Bukkit.getPlayer(targetPlayerName.replace("@", ""))
    }
    fun voidGUI(plugin: Plugin, player: Player, targetPlayerName: String) {
        val aoringoPlayer = AoringoPlayer(player)
        val targetPlayer = getPlayer(targetPlayerName)
        if (targetPlayer == null) {
            aoringoPlayer.sendErrorMessage("オンラインのプレイヤーのみ評価できます")
            return
        }
        if (targetPlayer == player) {
            aoringoPlayer.sendErrorMessage("自分を評価することは出来ません")
            return
        }
        val targetPlayerHead = playerHead(targetPlayer)
        val gui = makeVoidGUI(targetPlayerHead, isCanVoid(player))
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable {
                player.openInventory(gui)
            }
        )
    }
    private fun isCanVoid(player: Player): Boolean {
        val notPolledCount = 0
        val pollingTimes = Scoreboard().getValue(voidJudgmentScoreboardName, player.name)
        return pollingTimes == notPolledCount
    }
    private fun makeVoidGUI(targetPlayerHead: ItemStack, canVoid: Boolean): Inventory {
        val itemManager = ItemManager()
        val guiSize = 9
        val gui = Bukkit.createInventory(null, guiSize, "${ChatColor.BLUE}プレイヤー評価")
        val targetPlayerHeadSlot = 2
        val highRatingSlot = 4
        val lowRatingSlot = 6
        val noVoidItem = itemManager.make(Material.BARRIER, "${ChatColor.RED}クリック禁止", "評価は1日1回までです")
        val highEvaluation = if (canVoid) { itemManager.make(Material.STONE_BUTTON, "${ChatColor.GREEN}高評価") } else { noVoidItem }
        val lowEvaluation = if (canVoid) { itemManager.make(Material.STONE_BUTTON, "${ChatColor.RED}低評価") } else { noVoidItem }
        gui.setItem(targetPlayerHeadSlot, targetPlayerHead)
        gui.setItem(highRatingSlot, highEvaluation)
        gui.setItem(lowRatingSlot, lowEvaluation)
        return gui
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
