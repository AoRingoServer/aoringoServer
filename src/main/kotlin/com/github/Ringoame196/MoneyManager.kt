package com.github.Ringoame196

import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Items.Item
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import java.util.*

class MoneyManager(private val sender: MessageSender) {
    fun getMoney(targetAccount:Account):Int {
        val accountID = targetAccount.getAccountID()
        return Scoreboard().getValue("money", accountID)
    }

    fun reduceMoney(targetAccount:Account, amount:Int):Boolean {
        val money = getMoney(targetAccount) - amount
        if (money < 0) {
            sender.sendErrorMessage("所持金が足りません")
            return false
        }
        setMoney(targetAccount,money)
        sender.sendActionBar("${ChatColor.RED}-$amount")
        return true
    }

    fun setMoney(targetAccount:Account, money:Int) {
        val accountID = targetAccount.getAccountID()
        Scoreboard().set("money", accountID, money)
        val bossbar = PluginData.DataManager.playerDataMap.getOrPut(UUID.fromString(accountID)) { com.github.Ringoame196.Entity.AoringoPlayer.PlayerData() }.titleMoneyBossbar
        if (bossbar == null) {
            sender.createBossbar()
        } else {
            bossbar.setTitle(bossbarTitle(targetAccount))
        }
    }

    fun addMoney(targetAccount:Account, amount:Int) {
        val money = getMoney(targetAccount) + amount
        setMoney(targetAccount,money)
        sender.sendActionBar("${ChatColor.GREEN}+$amount")
    }
    fun getMoneyFromAdmin(targetAccount:Account, fromAccount: Account, amount: Int,inventory: Inventory){
        if(targetAccount is Admin){
            if (getMoney(targetAccount) < amount){
                sender.sendErrorMessage("運営のお金が不足したため 運営手形が発行されました")
                sender.sendMessage("${ChatColor.GOLD}運営に発行された手形をお渡しください")
                inventory.addItem(Item().make(Material.PAPER, "${ChatColor.GOLD}運営手形(${amount}円)", "手形を運営に渡してください", 11, 1))
            } else {
                tradeMoney(fromAccount, targetAccount, amount)
            }
        }
    }
    fun tradeMoney(fromAccount:Account, targetAccount: Account, amount: Int){
        reduceMoney(targetAccount,amount)
        addMoney(fromAccount,amount)
    }
    fun bossbarTitle(targetAccount:Account): String {
        return "${ChatColor.GOLD}所持金${formalCurrency(getMoney(targetAccount))}円"
    }
    private fun formalCurrency(money: Int): String {
        return money.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
    }
}