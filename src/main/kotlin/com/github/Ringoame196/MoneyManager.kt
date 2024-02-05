package com.github.Ringoame196

import com.github.Ringoame196.Accounts.Account
import com.github.Ringoame196.Accounts.PlayerAccount
import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.entity.Player

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
        if (targetAccount is PlayerAccount) {
            updateDisplay(targetAccount)
        }
    }
    private fun updateDisplay(account: PlayerAccount) {
        val player = account.player
        if (player !is Player) { return }
        val aoringoPlayer = AoringoPlayer(player)
        aoringoPlayer.moneyUseCase.displayMoney(aoringoPlayer)
    }

    fun addMoney(targetAccount: Account, amount: Int) {
        val money = getMoney(targetAccount) + amount
        setMoney(targetAccount, money)
    }
    fun canGetMoneyFromAdmin(adminAccount: Admin, amount: Int): Boolean {
        return getMoney(adminAccount) > amount
    }
    fun tradeMoney(receiveAccount: Account, giveAccount: Account, amount: Int): Boolean {
        if (getMoney(giveAccount) < amount) { return false }
        reduceMoney(giveAccount, amount)
        addMoney(receiveAccount, amount)
        return true
    }
    fun convertingInt(price: String): Int? {
        try {
            return price.toInt()
        } catch (e: NumberFormatException) {
            return null
        }
    }
}
