package com.github.Ringoame196.Commands

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Shop.Fshop
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class FshopCommand(val plugin: Plugin) : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return true }
        val fshop = Fshop(plugin)
        val aoringoPlayer = AoringoPlayer(sender)
        val shop = aoringoPlayer.getEntityInSight(15)
        if (shop?.type != EntityType.ITEM_FRAME || !shop.name.contains("@Fshop")) {
            aoringoPlayer.sendErrorMessage("ショップの樽に目線を合わせ、近づいてください")
            return true
        }
        if (!fshop.isOwner(sender, shop) && !sender.isOp) {
            aoringoPlayer.sendErrorMessage("ショップを操作する権限がありません")
            return true
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf("set")
            2 -> mutableListOf("lore", "price")
            else -> mutableListOf()
        }
    }
}
