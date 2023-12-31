package com.github.Ringoame196.Commands

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.JointAccount
import com.github.Ringoame196.MoneyManager
import com.github.Ringoame196.PlayerManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Money : CommandExecutor, TabCompleter {
    private val playerManager = PlayerManager()
    private val moneyManager = MoneyManager()
    private val tabMap = mapOf(
        "確認" to "show",
        "送金" to "pay",
        "追加" to "add",
        "設定" to "set"
    )
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return false }
        val aoringoPlayer = AoringoPlayer(sender)
        val size = args.size
        if (args.isEmpty()) {
            val possessionMoney = aoringoPlayer.moneyUseCase.getMoney(aoringoPlayer.playerAccount)
            sender.sendMessage("${ChatColor.GREEN}[お金] あなたの所持金は $possessionMoney 円")
            return true
        }
        if (size == 1) { return false }
        val menu = args[0]
        val targetPlayer = playerManager.acquisitionPlayer(args[1])
        val targetAccount = JointAccount(targetPlayer.uniqueId.toString())
        if (menu == tabMap["確認"]) {
            if (!aoringoPlayer.isOperator()) {
                aoringoPlayer.sendErrorMessage("このコマンドはオペレーターのみ使用可能です")
                return true
            }
            val possessionMoney = aoringoPlayer.moneyUseCase.getMoney(targetAccount)
            sender.sendMessage("${ChatColor.GREEN}[お金] ${targetPlayer.name}の所持金は $possessionMoney 円")
            return true
        }
        if (size == 2) { return false }
        val price: Int
        try {
            price = args[2].toInt()
        } catch (e: NumberFormatException) {
            aoringoPlayer.sendErrorMessage("数字を入力してください")
            return false
        }
        when (menu) {
            tabMap["送金"] -> {
                if (!aoringoPlayer.moneyUseCase.tradeMoney(aoringoPlayer, targetAccount, price)) { return true }
                aoringoPlayer.player.sendMessage("${org.bukkit.ChatColor.GREEN}[お金] ${targetPlayer.name}さんに$price 円送金しました")
            }
            tabMap["追加"] -> {
                if (!aoringoPlayer.isOperator()) {
                    aoringoPlayer.sendErrorMessage("このコマンドはオペレーターのみ使用可能です")
                    return true
                }
                moneyManager.addMoney(targetAccount, price)
                aoringoPlayer.player.sendMessage("${ChatColor.GREEN}[お金] ${targetPlayer.name}の所持金を${price}円追加しました")
            }
            tabMap["設定"] -> {
                if (!aoringoPlayer.isOperator()) {
                    aoringoPlayer.sendErrorMessage("このコマンドはオペレーターのみ使用可能です")
                    return true
                }
                moneyManager.setMoney(targetAccount, price)
                aoringoPlayer.player.sendMessage("${ChatColor.GREEN}[お金]  ${targetPlayer.name}の所持金を${price}円に設定しました")
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf(tabMap["確認"]?:"", tabMap["送金"]?:"", tabMap["追加"]?:"", tabMap["設定"]?:"")
            2 -> playerManager.acquisitionPlayerNameList()
            3 -> mutableListOf("[値段(数字)]")
            else -> mutableListOf("")
        }
    }
}
