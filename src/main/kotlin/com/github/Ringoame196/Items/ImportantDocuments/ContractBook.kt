package com.github.Ringoame196.Items.ImportantDocuments

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BookMeta

class ContractBook : ImportantDocument {
    override fun write(player: Player, subCommand: Array<out String>) {
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        val money = subCommand[0].toInt()
        meta.setDisplayName("${ChatColor.YELLOW}契約書[契約待ち]")
        val bookMessage = meta.getPage(1)
            .replace("甲方：[プレイヤー名]\nUUID：[UUID]", "甲方：${player.name}\nUUID：${player.uniqueId}")
            .replace("取引金額：[値段]", "取引金額：${money}円")
        meta.setPage(1, bookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }

    override fun subCommand(subCommandCount: Int): MutableList<String> {
        val subCommand = mapOf(
            1 to "[契約金]"
        )
        return mutableListOf(subCommand[subCommandCount] ?: "")
    }
}
