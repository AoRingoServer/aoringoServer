package com.github.Ringoame196

import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Items.Item
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import java.util.*

class MoneyManager {
    fun getMoney(targetAccount: Account): Int {
        val accountID = targetAccount.getAccountID()
        return Scoreboard().getValue("money", accountID)
    }

    fun reduceMoney(targetAccount: Account, amount: Int): Boolean {
        val money = getMoney(targetAccount) - amount
        if (money < 0) {
            return false
        }
        setMoney(targetAccount, money)
        return true
    }

    fun setMoney(targetAccount: Account, money: Int) {
        val accountID = targetAccount.getAccountID()
        Scoreboard().set("money", accountID, money)
    }
    fun isTargetPlayer(targetAccount: Account):Boolean{
        return targetAccount is PlayerAccount
    }

    fun addMoney(targetAccount: Account, amount: Int) {
        val money = getMoney(targetAccount) + amount
        setMoney(targetAccount, money)
    }
    fun canGetMoneyFromAdmin(adminAccount: Admin, amount: Int):Boolean {
        return getMoney(adminAccount) > amount
    }
    fun tradeMoney(fromAccount: Account, targetAccount: Account, amount: Int) {
        reduceMoney(targetAccount, amount)
        addMoney(fromAccount, amount)
    }
}
