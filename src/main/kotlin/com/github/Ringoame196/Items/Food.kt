package com.github.Ringoame196.Items

import com.github.Ringoame196.Event.AoringoEvents
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Food {
    fun recovery(player: Player, item: ItemStack) {
        val itemName = item.itemMeta?.displayName
        val addfood = when (itemName) {
            "おにぎり" -> 6
            "ステーキ" -> 5
            "からあげ" -> 2
            "とんかつ" -> 2
            "ハンバーグ" -> 4
            else -> 2
        }
        when (itemName) {
            "${ChatColor.YELLOW}力のプロテイン" -> com.github.Ringoame196.Entity.Player().addPower(player)
            "${ChatColor.RED}ハートのハーブ" -> com.github.Ringoame196.Entity.Player().addMaxHP(player)
        }
        player.foodLevel += addfood
    }
    fun makeItem(name: String, customModelData: Int): ItemStack {
        val item = ItemStack(Material.MELON_SLICE)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        meta?.setCustomModelData(customModelData)
        meta?.lore = mutableListOf(giveExpirationDate(14))
        item.setItemMeta(meta)
        return item
    }
    fun giveExpirationDate(add: Int): String {
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_WEEK, add)

        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH) + 1 // 月は0から始まるため+1
        val day = now.get(Calendar.DAY_OF_MONTH)
        return "消費期限: $year/$month/$day"
    }
    fun isExpirationDate(player: Player, item: ItemStack): Boolean {
        val expiration = item.itemMeta?.lore?.get(0) ?: return false
        val dateStr = expiration.replace("消費期限:", "")
        val dateFormat = SimpleDateFormat("yyyy/MM/dd") // フォーマットに合わせて変更
        val date = dateFormat.parse(dateStr) ?: return false

        // 現在の日付を取得
        val currentDate = Date()

        // 日付の差を計算
        val diff = (currentDate.time - date.time) / (1000 * 60 * 60 * 24) // ミリ秒から日数に変換
        if (diff > 0) {
            AoringoEvents().onErrorEvent(player, "消費期限が切れています")
            return true
        }
        return false
    }
    fun lowered(player: Player, item: ItemStack) {
        AoringoEvents().onErrorEvent(player, "お腹を下した")
        val poisonEffect = PotionEffect(PotionEffectType.POISON, 5 * 20, 100) // 持続時間をticksに変換
        player.addPotionEffect(poisonEffect)
        val hungerEffect = PotionEffect(PotionEffectType.HUNGER, 5 * 20, 100) // 持続時間をticksに変換
        player.addPotionEffect(hungerEffect)
    }
    fun eat(player: Player, item: ItemStack) {
        val itemType = item.type
        if ((itemType == Material.MILK_BUCKET || itemType == Material.MELON_SLICE) && item.itemMeta?.displayName != null) {
            recovery(player, item)
        } else {
            player.foodLevel = player.foodLevel + 2
        }
        player.saturation = 20.0F
        if (player.foodLevel > 20) {
            player.foodLevel = 20
        }
        if (player.gameMode == GameMode.CREATIVE) { return }
        val food = item.clone()
        food.amount = 1
        player.inventory.removeItem(food)
    }
    fun dropReplacement(e: EntityDeathEvent, beforeItem: Material, afterItem: ItemStack) {
        for (item in e.drops) {
            if (item.type != beforeItem) { continue }
            e.drops.remove(item)
            afterItem.amount = item.amount
            e.drops.add(afterItem)
        }
    }
}
