package com.github.Ringoame196.Items

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Scoreboard
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
