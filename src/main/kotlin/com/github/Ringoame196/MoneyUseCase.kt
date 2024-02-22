package com.github.Ringoame196

import com.github.Ringoame196.Accounts.Account
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
    fun formalCurrency(money: Int): String {
        return money.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
    }
    fun getMoney(account: Account): Int {
        return moneyManager.getMoney(account)
    }
    fun tradeMoney(aoringoPlayer: AoringoPlayer, fromAccount: Account, amount: Int): Boolean {
        if (getMoney(aoringoPlayer.playerAccount) < amount) {
            aoringoPlayer.sendErrorMessage("所持金が足りませんでした")
            return false
        }
        moneyManager.tradeMoney(fromAccount, aoringoPlayer.playerAccount, amount)
        return true
    }
    fun paymentItem(player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        val item = player.inventory.itemInMainHand
        val itemName = item.itemMeta?.displayName ?: ""
        val money = itemName.replace("${ChatColor.GREEN}", "").replace("円", "").toInt()
        if (money == 0) {
            return
        }
        val totalAmount = item.amount * money
        aoringoPlayer.moneyUseCase.addMoney(aoringoPlayer, totalAmount)
        player.inventory.setItemInMainHand(ItemStack(Material.AIR))
    }
    fun convertingInt(price: String, chatColor: String = ""): Int {
        val colorPullOut = price.replace(chatColor, "")
        val yenPullOut = colorPullOut.replace("円", "")
        return try {
            yenPullOut.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}
