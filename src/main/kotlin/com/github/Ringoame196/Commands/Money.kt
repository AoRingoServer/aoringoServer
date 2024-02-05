package com.github.Ringoame196.Commands

import com.github.Ringoame196.Accounts.Account
import com.github.Ringoame196.Accounts.JointAccount
import com.github.Ringoame196.Entity.AoringoPlayer
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
            mePossessionGoldDisplay(aoringoPlayer)
            return true
        }
        if (size == 1) { return false }
        val menu = args[0]
        val targetPlayer = playerManager.acquisitionPlayer(args[1])
        val targetAccount = JointAccount(targetPlayer.uniqueId.toString())
        val companyAccount = JointAccount(args[1])
        val showMap = mapOf(
            "show" to { showing(aoringoPlayer, targetAccount) },
            "companyShow" to { showing(aoringoPlayer, companyAccount) }
        )
        if (showMap.contains(menu)) {
            showMap[menu]?.invoke()
            return true
        }
        if (size == 2) { return false }
        val price = moneyManager.convertingInt(args[2])
        if (price == null) {
            aoringoPlayer.sendErrorMessage("数字を入力してください")
            return false
        }
        val processingMap = mapOf(
            "pay" to { remittance(aoringoPlayer, targetAccount, price) },
            "add" to { add(aoringoPlayer, targetAccount, price) },
            "set" to { set(aoringoPlayer, targetAccount, price) },
            "set" to { set(aoringoPlayer, targetAccount, price) },
            "companySet" to { set(aoringoPlayer, companyAccount, price) }
        )
        processingMap[menu]?.invoke() ?: return false
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
    private fun showing(aoringoPlayer: AoringoPlayer, account: Account) {
        if (!authorityMissingMessage(aoringoPlayer)) { return }
        val possessionMoney = moneyManager.getMoney(account)
        aoringoPlayer.player.sendMessage("${ChatColor.GREEN}${account.getRegisteredPerson()}の所持金は${possessionMoney}円です")
    }
    private fun remittance(aoringoPlayer: AoringoPlayer, targetAccount: Account, price: Int) {
        if (!aoringoPlayer.moneyUseCase.tradeMoney(aoringoPlayer, targetAccount, price)) { return }
        aoringoPlayer.player.sendMessage("${org.bukkit.ChatColor.GREEN}[お金] ${targetAccount.getRegisteredPerson()}に$price 円送金しました")
    }
    private fun add(aoringoPlayer: AoringoPlayer, targetAccount: Account, price: Int) {
        if (!authorityMissingMessage(aoringoPlayer)) { return }
        moneyManager.addMoney(targetAccount, price)
        aoringoPlayer.player.sendMessage("${ChatColor.GREEN}[お金] ${targetAccount.getRegisteredPerson()}の所持金を${price}円追加しました")
    }
    private fun set(aoringoPlayer: AoringoPlayer, targetAccount: Account, price: Int) {
        if (!authorityMissingMessage(aoringoPlayer)) { return }
        moneyManager.setMoney(targetAccount, price)
        aoringoPlayer.player.sendMessage("${ChatColor.GREEN}[お金]  ${targetAccount.getRegisteredPerson()}の所持金を${price}円に設定しました")
    }
    private fun authorityMissingMessage(aoringoPlayer: AoringoPlayer): Boolean {
        if (!aoringoPlayer.isOperator()) {
            aoringoPlayer.sendNoOpMessage()
            return false
        }
        return true
    }
    private fun mePossessionGoldDisplay(aoringoPlayer: AoringoPlayer){
        val account = aoringoPlayer.playerAccount
        aoringoPlayer.player.sendMessage("あなたの所持金は${aoringoPlayer.moneyUseCase.getMoney(account)}")
    }
}
