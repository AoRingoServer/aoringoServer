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
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return false }
        val aoringoPlayer = AoringoPlayer(sender)
        val size = args.size
        if (args.isEmpty()) {
            aoringoPlayer.moneyUseCase.showTargetPlayerAccount(sender.name, aoringoPlayer.playerAccount, sender)
            return true
        }
        if (size == 1) { return false }
        val menu = args[0]
        val targetPlayer = playerManager.acquisitionPlayer(args[1])
        val targetAccount = JointAccount(targetPlayer.uniqueId.toString())
        val companyAccount = JointAccount(args[1])
        if (menu == "show") {
            if (!aoringoPlayer.isOperator()) {
                aoringoPlayer.sendNoOpMessage()
                return true
            }
            aoringoPlayer.moneyUseCase.showTargetPlayerAccount(targetPlayer.name ?: return false, targetAccount, sender)
            return true
        }
        if (menu == "companyShow") {
            if (!aoringoPlayer.isOperator()) {
                aoringoPlayer.sendNoOpMessage()
                return true
            }
            aoringoPlayer.moneyUseCase.showTargetPlayerAccount(companyAccount.getAccountID(), companyAccount, sender)
            return true
        }
        if (size == 2) { return false }
        val price = moneyManager.convertingInt(args[2])
        if (price == null) {
            aoringoPlayer.sendErrorMessage("数字を入力してください")
            return false
        }
        when (menu) {
            "pay" -> {
                if (!aoringoPlayer.moneyUseCase.tradeMoney(aoringoPlayer, targetAccount, price)) { return true }
                aoringoPlayer.player.sendMessage("${org.bukkit.ChatColor.GREEN}[お金] ${targetPlayer.name}さんに$price 円送金しました")
            }
            "add" -> {
                if (!aoringoPlayer.isOperator()) {
                    aoringoPlayer.sendNoOpMessage()
                    return true
                }
                moneyManager.addMoney(targetAccount, price)
                aoringoPlayer.player.sendMessage("${ChatColor.GREEN}[お金] ${targetAccount.getRegisteredPerson()}の所持金を${price}円追加しました")
            }
            "set" -> {
                if (!aoringoPlayer.isOperator()) {
                    aoringoPlayer.sendNoOpMessage()
                    return true
                }
                moneyManager.setMoney(targetAccount, price)
                aoringoPlayer.player.sendMessage("${ChatColor.GREEN}[お金]  ${targetAccount.getRegisteredPerson()}の所持金を${price}円に設定しました")
            }
            "companySet" -> {
                if (!aoringoPlayer.isOperator()) {
                    aoringoPlayer.sendNoOpMessage()
                    return true
                }
                moneyManager.setMoney(companyAccount, price)
                aoringoPlayer.player.sendMessage("${ChatColor.GREEN}[お金]  ${companyAccount.getRegisteredPerson()}の所持金を${price}円に設定しました")
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf("show", "pay", "add", "set", "companyShow", "companySet")
            2 -> playerManager.acquisitionPlayerNameList()
            3 -> mutableListOf("[値段(数字)]")
            else -> mutableListOf("")
        }
    }
}
