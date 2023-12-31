package com.github.Ringoame196.Items

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Yml
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.plugin.Plugin
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class ItemManager {
    fun make(material: Material, name: String = "", lore: String = "", customModelData: Int = 0, amount: Int = 1): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        if (customModelData != 0) {
            meta?.setCustomModelData(customModelData)
        }
        if (lore.isNotEmpty()) {
            meta?.lore = mutableListOf(lore)
        }
        item.setItemMeta(meta)
        item.amount = amount
        return item
    }
    fun reduceMainItem(player: Player) {
        val playerItem = player.inventory.itemInMainHand.clone()
        reduceOneItem(player, playerItem)
    }

    fun enchant(enchant: Enchantment, level: Int): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta as EnchantmentStorageMeta
        meta.addStoredEnchant(enchant, level, true)
        item.setItemMeta(meta)
        return item
    }

    fun drop(location: Location, itemStack: ItemStack) {
        val item: Item = location.world!!.spawn(location, org.bukkit.entity.Item::class.java)
        item.itemStack = itemStack
    }

    fun request(player: Player, message: String) {
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        val money = message.replace("!契約 ", "").toInt()
        if (money == 0) {
            return
        }
        meta.setDisplayName("${ChatColor.YELLOW}契約書[契約待ち]")
        val bookMessage = meta.getPage(1)
            .replace("甲方：[プレイヤー名]\nUUID：[UUID]", "甲方：${player.name}\nUUID：${player.uniqueId}")
            .replace("取引金額：[値段]", "取引金額：${money}円")
        meta.setPage(1, bookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }

    fun contract(player: Player, message: String) {
        val aoringoPlayer = com.github.Ringoame196.Entity.AoringoPlayer(player)
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        val moneyUseCase = aoringoPlayer.moneyUseCase
        val money = message.replace("!契約 ", "")
        val bookMessage = meta.getPage(1)
        val priceIndex = bookMessage.indexOf("取引金額：")
        val priceMessage = bookMessage.substring(priceIndex + "取引金額：".length).replace("円", "")
        if (money != priceMessage) {
            aoringoPlayer.sendErrorMessage("金額が違います")
            return
        }
        val playerMoney = moneyUseCase.getMoney(aoringoPlayer.playerAccount)
        if (playerMoney < money.toInt()) {
            aoringoPlayer.sendErrorMessage("お金が足りません")
            return
        }
        moneyUseCase.reduceMoney(aoringoPlayer, money.toInt())
        val currentDate = LocalDate.now()

        // 日付を指定したフォーマットで文字列として取得
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(dateFormatter)
        meta.setDisplayName("${ChatColor.RED}契約本@${money}円契約")
        val setBookMessage = meta.getPage(1)
            .replace("乙方：[プレイヤー名]\nUUID：[UUID]", "乙方：${player.name}\nUUID：${player.uniqueId}")
            .replace("契約日：[日付]", "契約日：$formattedDate")
        meta.setPage(1, setBookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }

    fun returnMoney(player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        val moneyUseCase = aoringoPlayer.moneyUseCase
        val item = player.inventory.itemInMainHand
        val bookMessage = item.itemMeta as BookMeta
        if (!bookMessage.getPage(1).contains("UUID：${player.uniqueId}")) {
            return
        }
        val money = item.itemMeta?.displayName?.replace("${ChatColor.RED}契約本@", "")?.replace("円契約", "")?.toInt()
        moneyUseCase.addMoney(aoringoPlayer, money ?: return)
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

    fun smartphone(): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta
        meta?.setDisplayName("${ChatColor.YELLOW}スマートフォン")
        meta?.setCustomModelData(1)
        item.setItemMeta(meta)
        return item
    }
    fun breakLadle(player: Player) {
        if (Random.nextInt(0, 100) != 0) {
            return
        }
        ItemManager().reduceMainItem(player)
        com.github.Ringoame196.Entity.AoringoPlayer(player).sendErrorMessage("おたまがぶっ壊れた")
    }
    fun breakHandle(itemFrame: ItemFrame, playerClass: com.github.Ringoame196.Entity.AoringoPlayer) {
        if (Random.nextInt(0, 100) != 0) {
            return
        }
        itemFrame.setItem(ItemStack(Material.AIR))
        playerClass.sendErrorMessage("ハンドルがぶっ壊れた")
    }
    fun giveBarrelGift(player: Player, barrel: Barrel, management: String) {
        if (Scoreboard().getValue(management, player.uniqueId.toString()) != 0) {
            com.github.Ringoame196.Entity.AoringoPlayer(player).sendErrorMessage("既にギフトを受け取っています")
            return
        }
        player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1f, 1f)
        Scoreboard().set(management, player.uniqueId.toString(), 1)
        for (barrelItem in barrel.inventory) {
            barrelItem ?: continue
            player.inventory.addItem(barrelItem)
        }
        player.sendMessage("${ChatColor.AQUA}Great gift for you!")
    }
    fun reduceOneItem(player: Player, item: ItemStack) {
        val reduceItem = item.clone()
        reduceItem.amount = 1
        player.inventory.removeItem(reduceItem)
    }
}
