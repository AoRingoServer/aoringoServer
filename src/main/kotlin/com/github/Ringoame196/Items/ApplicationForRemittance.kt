package com.github.Ringoame196.Items

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.JointAccount
import com.github.Ringoame196.MoneyManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ApplicationForRemittance(private val player: Player, private val book: ItemStack) {
    private val aoringoPlayer = AoringoPlayer(player)
    private val writtenBook = WrittenBook(book)
    private val pageText = writtenBook.getCharactersPage(1)
    fun registeredRemittanceRecipientPlayer(targetPlayerName: String) {
        val acquisitionPlayerFromName = Bukkit.getOfflinePlayer(targetPlayerName)
        val acquisitionPlayerUUID = acquisitionPlayerFromName.uniqueId
        val acquisitionPlayerFromUUID = Bukkit.getPlayer(acquisitionPlayerUUID)
        if (acquisitionPlayerFromUUID == null) {
            aoringoPlayer.sendErrorMessage("指定したプレイヤーのデータがありませんでした")
            return
        }
        if (acquisitionPlayerFromName != acquisitionPlayerFromUUID) {
            aoringoPlayer.sendErrorMessage("指定したプレイヤーが見つかりませんでした")
            return
        }
        writtenBook.edit(aoringoPlayer.player, 1, pageText.replace("送金先口座：[記入]", "送金先口座：$acquisitionPlayerUUID"))
        finalize()
    }
    fun remittanceAccountRegistration(targetAccount: String) {
        writtenBook.edit(player, 1, pageText.replace("送金先口座：[記入]", "送金先口座：$targetAccount"))
        finalize()
    }
    fun registrationAmount(price: UInt) {
        writtenBook.edit(player, 1, pageText.replace("送金金額：[記入]", "送金金額：${price}円"))
        finalize()
    }
    fun registerMyAccount() {
        writtenBook.edit(player, 1, pageText.replace("お客様口座：[記入]", "お客様口座：${player.uniqueId}"))
        finalize()
    }
    private fun finalize() {
        if (!isFinished()) { return }
        writtenBook.changeItemName("${ChatColor.YELLOW}送金申込書")
    }
    private fun isFinished(): Boolean {
        val playerItem = player.inventory.itemInMainHand
        val newPageText = WrittenBook(playerItem).getCharactersPage(1)
        return !newPageText.contains("[記入]")
    }
    fun remittance() {
        val pageText = writtenBook.getCharactersPage(1)
        val remittanceAccount = writtenBook.conversionMap(pageText)["送金先口座"] ?: return
        val reduceAccount = writtenBook.conversionMap(pageText)["お客様口座"] ?: return
        val price = writtenBook.conversionMap(pageText)["送金金額"]?.replace("円", "")?.toInt()
        if (!MoneyManager().tradeMoney(JointAccount(remittanceAccount), JointAccount(reduceAccount), price ?: return)) {
            aoringoPlayer.sendErrorMessage("所持金が足りません")
            return
        }
        sendRemittance(price)
        ItemManager().reduceMainItem(player)
    }
    private fun sendRemittance(price:Int){
        player.sendMessage("${ChatColor.AQUA}[送金]$price 円送金しました")
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
