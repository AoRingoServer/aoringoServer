package com.github.Ringoame196.Items

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Food{
    private fun returnFoodHasLevel(item: ItemStack):Int {
        val itemName = item.itemMeta?.displayName
        return when (itemName) {
            "おにぎり" -> 6
            "ステーキ" -> 5
            "からあげ" -> 2
            "とんかつ" -> 2
            "ハンバーグ" -> 4
            else -> 2
        }
    }
    fun calculateFoodLevel(player: Player,food: ItemStack):Int {
        val playerFoodLevel = player.foodLevel
        val foodHasLevel = returnFoodHasLevel(food)
        val totalFoodLevel = playerFoodLevel + foodHasLevel
        return if(totalFoodLevel > 20) { 20 } else { totalFoodLevel }
    }
    fun increaseStatus(player: Player, food: ItemStack){
        val playerClass = AoringoPlayer(player)
        val foodName = food.itemMeta?.displayName
        when(foodName){
            "${ChatColor.YELLOW}力のプロテイン" -> playerClass.addPower()
            "${ChatColor.RED}ハートのハーブ" -> playerClass.addMaxHP()
        }
    }
    fun makeItem(name: String, customModelData: Int): ItemStack {
        val item = ItemStack(Material.MELON_SLICE)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        meta?.setCustomModelData(customModelData)
        meta?.lore = mutableListOf(makeExpirationDate(14))
        item.setItemMeta(meta)
        return item
    }
    fun makeExpirationDate(add: Int): String {
        val date = increaseDay(add)
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1 // 月は0から始まるため+1
        val day = date.get(Calendar.DAY_OF_MONTH)
        return "消費期限: $year/$month/$day"
    }
    private fun increaseDay(add: Int): Calendar {
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_WEEK, add)
        return now
    }
    fun isExpirationDateHasExpired(player: Player, item: ItemStack): Boolean {
        val playerClass = AoringoPlayer(player)
        val itemLore = item.itemMeta?.lore
        val expiration = itemLore?.get(0) ?: return false
        val date = takeOutDate(expiration)

        // 日付の差を計算
        val diff = compareDateFromCurreantDate(date)
        if (diff > 0) {
            playerClass.sendErrorMessage("消費期限が切れています")
            return true
        }
        return false
    }
    fun compareDateFromCurreantDate(date:Date): Long {
        val currentDate = Date()
        val diff = currentDate.time - date.time
        return diff / 1000 * 60 * 60 * 24
    }
    private fun takeOutDate(lore:String): Date {
        val expiration = lore
        val dateStr = expiration.replace("消費期限:", "")
        val dateFormat = SimpleDateFormat("yyyy/MM/dd") // フォーマットに合わせて変更
        return dateFormat.parse(dateStr)
    }
    fun lowered(player: Player, item: ItemStack) {
        AoringoEvents().onErrorEvent(player, "お腹を下した")
        val poisonEffect = PotionEffect(PotionEffectType.POISON, 5 * 20, 100) // 持続時間をticksに変換
        player.addPotionEffect(poisonEffect)
        val hungerEffect = PotionEffect(PotionEffectType.HUNGER, 5 * 20, 100) // 持続時間をticksに変換
        player.addPotionEffect(hungerEffect)
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
