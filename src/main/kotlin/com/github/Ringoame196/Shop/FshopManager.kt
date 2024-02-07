package com.github.Ringoame196.Shop

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class FshopManager : CommandExecutor {
    fun resetShopLand(plugin: Plugin) {
        val list = Yml().getList(plugin, "conservationLand", "", "protectedName") ?: return
        for (name in list) {
            if (Scoreboard().getValue("protectionContract", name) == 2) {
                Scoreboard().reduce("protectionContract", name, 1)
                continue
            }
            WorldGuard().reset(name, Bukkit.getWorld("shop") ?: return)
            Yml().removeToList(plugin, "", "conservationLand", "protectedName", name)
        }
        Bukkit.broadcastMessage("${ChatColor.RED}[ショップ] ショップの購入土地がリセットされました")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return true }
        val fshop = Fshop()
        val aoringoPlayer = AoringoPlayer(sender)
        val shop = aoringoPlayer.getEntityInSight(15)
        if (shop?.type != EntityType.ITEM_FRAME || shop.customName?.contains("@Fshop") == false) {
            aoringoPlayer.sendErrorMessage("ショップの樽に目線を合わせ、近づいてください")
            return true
        }
        if (!fshop.isOwner(sender, shop) && !sender.isOp) {
            aoringoPlayer.sendErrorMessage("ショップを操作する権限がありません")
            return true
        }
        return true
    }
}
