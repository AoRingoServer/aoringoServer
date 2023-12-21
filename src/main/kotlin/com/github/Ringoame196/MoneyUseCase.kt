package com.github.Ringoame196

import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.UUID

class MoneyUseCase {

    private val moneyManager = MoneyManager()
    fun getMoneyFromAdmin(aoringoPlayer: AoringoPlayer, amount: Int) {
        val sender = aoringoPlayer.player
        val admin = Admin()
        if (moneyManager.canGetMoneyFromAdmin(admin, amount)) {
            moneyManager.tradeMoney(aoringoPlayer.playerAccount, Admin(), amount)
        } else {
            aoringoPlayer.sendErrorMessage("運営のお金が不足したため 運営手形が発行されました")
            sender.sendMessage("${ChatColor.GOLD}運営に発行された手形をお渡しください")
            sender.inventory.addItem(ItemManager().make(Material.PAPER, "${ChatColor.GOLD}運営手形(${amount}円)", "手形を運営に渡してください", 11, 1))
        }
    }
    fun addMoney(aoringoPlayer: AoringoPlayer, amount: Int) {
        moneyManager.addMoney(aoringoPlayer.playerAccount, amount)
        aoringoPlayer.sendActionBar("${ChatColor.GREEN}+$amount")
    }
    fun reduceMoney(aoringoPlayer: AoringoPlayer, amount: Int) {
        if (moneyManager.reduceMoney(aoringoPlayer.playerAccount, amount)) {
            aoringoPlayer.sendActionBar("${ChatColor.RED}-$amount")
        } else {
            aoringoPlayer.sendErrorMessage("所持金が足りません")
        }
    }
    fun displayMoney(aoringoPlayer: AoringoPlayer) {
        val playerUUID = UUID.fromString(aoringoPlayer.playerAccount.getAccountID())
        val bossbar = PluginData.DataManager.playerDataMap.getOrPut(playerUUID) { AoringoPlayer.PlayerData() }.titleMoneyBossbar
        if (bossbar == null) {
            aoringoPlayer.createBossbar(bossbarTitle(aoringoPlayer.playerAccount))
        } else {
            bossbar.setTitle(bossbarTitle(aoringoPlayer.playerAccount))
        }
    }
    fun setMoney(account: Account, total: Int) {
        moneyManager.setMoney(account, total)
    }
    private fun bossbarTitle(targetAccount: PlayerAccount): String {
        return "${ChatColor.GOLD}所持金${formalCurrency(moneyManager.getMoney(targetAccount))}円"
    }
    private fun formalCurrency(money: Int): String {
        return money.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
    }
    fun getMoney(account: Account): Int {
        return moneyManager.getMoney(account)
    }
    fun tradeMoney(aoringoPlayer: AoringoPlayer, fromAccount: Account, amount: Int) {
        if (getMoney(aoringoPlayer.playerAccount) < amount) {
            aoringoPlayer.sendErrorMessage("所持金が足りませんでした")
            return
        }
        moneyManager.tradeMoney(fromAccount, aoringoPlayer.playerAccount, amount)
    }
}
