package com.github.Ringoame196.Commands

import com.github.Ringoame196.Accounts.PlayerAccount
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.PluginData
import com.github.Ringoame196.Shop.Fshop
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class FshopCommand(val plugin: Plugin) : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size <= 2) { return false }
        if (sender !is Player) { return true }
        val fshop = Fshop(plugin)
        val aoringoPlayer = AoringoPlayer(sender)
        val shop = PluginData.DataManager.playerDataMap.getOrPut(aoringoPlayer.player.uniqueId) { AoringoPlayer.PlayerData() }.lastTouchShop
        if (shop == null) {
            aoringoPlayer.sendErrorMessage("変更したいショップを開いてください")
            return true
        }
        val subCommand = args[1]
        val processingMap = mapOf(
            "lore" to {
                var lore = ""
                for (i in 2 until args.size) {
                    lore += args[i] + " "
                }
                val key = fshop.loreKey
                fshop.additionalNbt(shop, key, lore)
                fshop.sendShopMessage(aoringoPlayer.player, "説明を設定しました")
            },
            "price" to {
                val key = fshop.priceKey
                val price = args[2]
                if (!fshop.checkInt(price)) {
                    aoringoPlayer.sendErrorMessage("数字を入力してください")
                } else {
                    fshop.additionalNbt(shop, key, price)
                    fshop.sendShopMessage(aoringoPlayer.player, "値段を変更しました")
                }
            },
            "account" to {
                val entry = args[2]
                val accountPlayer = Bukkit.getPlayer(entry)
                if (accountPlayer == null) {
                    aoringoPlayer.sendErrorMessage("プレイヤー名が取得できませんでした")
                } else {
                    val account = PlayerAccount(accountPlayer)
                    val key = fshop.accountKey
                    fshop.additionalNbt(shop, key, account.getAccountID())
                    fshop.sendShopMessage(aoringoPlayer.player, "送金先口座を変更しました")
                }
            }
        )
        if (!fshop.isOwner(sender, shop) && !sender.isOp) {
            aoringoPlayer.sendErrorMessage("ショップを操作する権限がありません")
            return true
        }
        processingMap[subCommand]?.invoke() ?: return false
        aoringoPlayer.player.playSound(aoringoPlayer.player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf("set")
            2 -> mutableListOf("lore", "price", "account")
            else -> mutableListOf()
        }
    }
}
