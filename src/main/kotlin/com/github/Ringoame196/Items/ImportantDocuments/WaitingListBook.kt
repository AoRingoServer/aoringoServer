package com.github.Ringoame196.Items.ImportantDocuments

import com.github.Ringoame196.Contract
import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BookMeta

class WaitingListBook : ImportantDocument {
    override fun write(player: Player, subCommand: Array<out String>) {
        val aoringoPlayer = AoringoPlayer(player)
        val money = subCommand[0].toInt()
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        val bookMessage = meta.getPage(1)
        val priceIndex = bookMessage.indexOf("取引金額：")
        val priceMessage = bookMessage.substring(priceIndex + "取引金額：".length).replace("円", "").toInt()
        if (money != priceMessage) {
            aoringoPlayer.sendErrorMessage("金額が違います")
            return
        }
        if (aoringoPlayer.moneyUseCase.getMoney(aoringoPlayer.playerAccount) < money) {
            aoringoPlayer.sendErrorMessage("お金が足りません")
            return
        }
        aoringoPlayer.moneyUseCase.reduceMoney(aoringoPlayer, money)
        val setBookMessage = Contract().writeContractDate(meta, player, money)
        meta.setPage(1, setBookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }

    override fun subCommand(subCommandCount: Int): MutableList<String> {
        val subCommand = mapOf(
            1 to "[記入されている金額]"
        )
        return mutableListOf(subCommand[subCommandCount] ?: "")
    }
}
