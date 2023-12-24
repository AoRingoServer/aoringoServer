package com.github.Ringoame196.Items

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.Bukkit
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
    }
    fun remittanceAccountRegistration(targetAccount: String) {
        writtenBook.edit(player, 1, pageText.replace("送金先口座：[記入]", "送金先口座：$targetAccount"))
    }
    fun registrationAmount(price: Int) {
        writtenBook.edit(player, 1, pageText.replace("送金金額：[記入]", "送金金額：$price 円"))
    }
    fun registerMyAccount() {
        writtenBook.edit(player, 1, pageText.replace("お客様口座：[記入]", "お客様口座：${player.uniqueId}"))
    }
}
