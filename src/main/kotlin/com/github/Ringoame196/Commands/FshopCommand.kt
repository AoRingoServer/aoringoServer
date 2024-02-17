package com.github.Ringoame196.Commands

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Shop.Fshop
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class FshopCommand(val plugin: Plugin) : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size <= 2) { return false }
        if (sender !is Player) { return true }
        val fshop = Fshop(plugin)
        val aoringoPlayer = AoringoPlayer(sender)
        val shop = aoringoPlayer.getEntityInSight(15)
        if (shop?.type != EntityType.ITEM_FRAME || shop.customName != ("@Fshop")) {
            aoringoPlayer.sendErrorMessage("ショップの樽に目線を合わせ、近づいてください")
            return true
        }
        shop as ItemFrame
        val subCommand = args[1]
        val processingMap = mapOf(
            "lore" to {
                var lore = ""
                for (i in 2 until args.size) {
                    lore += args[i] + " "
                }
                val key = fshop.loreKey
                fshop.additionalNbt(shop, key, lore)
                aoringoPlayer.player.sendMessage("${ChatColor.YELLOW}[ショップ] 説明を設定しました")
            },
            "price" to {
                val key = fshop.priceKey
                val price = args[2]
                if (!fshop.checkInt(price)) {
                    aoringoPlayer.sendErrorMessage("数字を入力してください")
                } else {
                    fshop.additionalNbt(shop, key, price)
                    aoringoPlayer.player.sendMessage("${ChatColor.YELLOW}[ショップ] 値段を変更しました")
                }
            }
        )
        if (!fshop.isOwner(sender, shop) && !sender.isOp) {
            aoringoPlayer.sendErrorMessage("ショップを操作する権限がありません")
            return true
        }
        processingMap[subCommand]?.invoke() ?: return false
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
