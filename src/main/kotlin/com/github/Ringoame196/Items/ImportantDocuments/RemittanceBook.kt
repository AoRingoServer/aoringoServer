package com.github.Ringoame196.Items.ImportantDocuments

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Items.WrittenBook
import com.github.Ringoame196.Accounts.JointAccount
import com.github.Ringoame196.MoneyManager
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

class RemittanceBook() : ImportantDocument {
    override fun write(player: Player, subCommand: Array<out String>) {
        val playerItem = player.inventory.itemInMainHand
        val writtenBook = WrittenBook(playerItem)
        var pageText = writtenBook.getCharactersPage(1)
        val targetAccount = subCommand[0]
        val price = subCommand[1]
        pageText = pageText.replace("送金先口座：[記入]", "送金先口座：$targetAccount")
        pageText = pageText.replace("送金金額：[記入]", "送金金額：${price}円")
        pageText = pageText.replace("お客様口座：[記入]", "お客様口座：${player.uniqueId}")
        writtenBook.edit(player, 1, pageText)
        if (!isFinished(player)) { return }
        writtenBook.changeItemName("${ChatColor.YELLOW}送金申込書")
    }
    override fun subCommand(subCommandCount: Int): MutableList<String> {
        val subCommand = mapOf(
            1 to "[送金口座名]",
            2 to "[振り込み金額]",
        )
        return mutableListOf(subCommand[subCommandCount] ?: "")
    }
    private fun isFinished(player: Player): Boolean {
        val playerItem = player.inventory.itemInMainHand
        val newPageText = WrittenBook(playerItem).getCharactersPage(1)
        return !newPageText.contains("[記入]")
    }
    fun remittance(player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        val playerItem = player.inventory.itemInMainHand
        val writtenBook = WrittenBook(playerItem)
        val pageText = writtenBook.getCharactersPage(1)
        val remittanceAccount = writtenBook.conversionMap(pageText)["送金先口座"] ?: return
        val reduceAccount = writtenBook.conversionMap(pageText)["お客様口座"] ?: return
        val price = writtenBook.conversionMap(pageText)["送金金額"]?.replace("円", "")?.toInt()
        if (!MoneyManager().tradeMoney(JointAccount(remittanceAccount), JointAccount(reduceAccount), price ?: return)) {
            aoringoPlayer.sendErrorMessage("所持金が足りません")
            return
        }
        sendRemittance(player, price)
        ItemManager().reduceMainItem(player)
    }
    private fun sendRemittance(player: Player, price: Int) {
        player.sendMessage("${ChatColor.AQUA}[送金]$price 円送金しました")
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
