package com.github.Ringoame196

import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Contract {
    fun writeContractDate(meta: BookMeta, player: Player, money: Int): String {
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(dateFormatter)
        meta.setDisplayName("${ChatColor.RED}契約本@${money}円契約")
        val setBookMessage = meta.getPage(1)
            .replace("乙方：[プレイヤー名]\nUUID：[UUID]", "乙方：${player.name}\nUUID：${player.uniqueId}")
            .replace("契約日：[日付]", "契約日：$formattedDate")
        return setBookMessage
    }
    fun returnMoney(player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        val playerAccount = aoringoPlayer.playerAccount
        val item = player.inventory.itemInMainHand
        val bookMessage = item.itemMeta as BookMeta
        if (!bookMessage.getPage(1).contains("UUID：${player.uniqueId}")) {
            return
        }
        val itemName = item.itemMeta?.displayName
        val money = itemName?.replace("${ChatColor.RED}契約本@", "")?.replace("円契約", "")?.toInt()?:0
        aoringoPlayer.moneyUseCase.addMoney(aoringoPlayer,money)
        player.inventory.setItemInMainHand(ItemStack(Material.AIR))
    }
    fun copyBlock(item: ItemStack, player: Player): ItemStack {
        val meta = item.itemMeta as BookMeta
        val currentDate = LocalDate.now()

        // 日付を指定したフォーマットで文字列として取得
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(dateFormatter)
        meta.setPage(
            1,
            "${ChatColor.DARK_RED}STOP COPYING\n\n" +
                "『契約書の複製は、青りんごサーバーの規約により禁止されています。』\n\n\n" +
                "プレイヤー名:${player.name}\n" +
                "日にち:$formattedDate"
        )
        item.setItemMeta(meta)
        return item
    }
}
