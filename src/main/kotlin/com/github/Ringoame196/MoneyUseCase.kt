package com.github.Ringoame196

import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.Item
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.UUID

class MoneyUseCase {
    private val moneyManager = MoneyManager()
    fun getMoneyFromAdmin(playerClass:AoringoPlayer,amount:Int){
        val sender = playerClass.player
        val admin = Admin()
        if (moneyManager.canGetMoneyFromAdmin(admin,amount)) {
            playerClass.sendErrorMessage("運営のお金が不足したため 運営手形が発行されました")
            sender.sendMessage("${ChatColor.GOLD}運営に発行された手形をお渡しください")
            sender.inventory.addItem(Item().make(Material.PAPER, "${ChatColor.GOLD}運営手形(${amount}円)", "手形を運営に渡してください", 11, 1))
        } else {
            moneyManager.tradeMoney(admin, playerClass.playerAccount , amount)
        }
    }
    fun addMoney(playerClass:AoringoPlayer,amount:Int){
        moneyManager.addMoney(playerClass.playerAccount,amount)
        playerClass.sendActionBar("${ChatColor.GREEN}+$amount")
    }
    fun reduceMoney(playerClass: AoringoPlayer,amount: Int,account: Account){
        if(moneyManager.reduceMoney(account,amount)){
            playerClass.sendErrorMessage("所持金が足りません")
        } else {
            playerClass.sendActionBar("${ChatColor.RED}-$amount")
        }
    }
    fun displayMoney(playerClass: AoringoPlayer){
        val playerUUID = UUID.fromString(playerClass.playerAccount.getAccountID())
        val bossbar = PluginData.DataManager.playerDataMap.getOrPut(playerUUID) { AoringoPlayer.PlayerData() }.titleMoneyBossbar
        if (bossbar == null) {
            playerClass.createBossbar(bossbarTitle(playerClass.playerAccount))
        } else {
            bossbar.setTitle(bossbarTitle(playerClass.playerAccount))
        }
    }
    fun setMoney(account: Account,total: Int){
        moneyManager.setMoney(account,total)
    }
    private fun bossbarTitle(targetAccount: PlayerAccount): String {
        return "${ChatColor.GOLD}所持金${formalCurrency(moneyManager.getMoney(targetAccount))}円"
    }
    private fun formalCurrency(money: Int): String {
        return money.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
    }
    fun getMoney(account: Account):Int{
        return moneyManager.getMoney(account)
    }
}