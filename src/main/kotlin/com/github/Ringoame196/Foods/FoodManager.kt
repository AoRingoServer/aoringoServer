package com.github.Ringoame196.Foods

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.PluginData
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

class FoodManager {
    fun ymlInfoAcquisition(itemName: String, key: String): String? {
        val foodData = PluginData.DataManager.FoodData
        return try {
            return foodData?.get("$itemName.$key").toString()
        } catch (e: NumberFormatException) {
            return null
        }
    }
    private fun acquisitionRecoveryAmount(item: ItemStack): Int {
        val itemName = item.itemMeta?.displayName ?: ""
        val key = "recoveryQuantity"

        val recoveryQuantity = ymlInfoAcquisition(itemName, key) ?: "2"
        return recoveryQuantity.toInt()
    }
    fun calculateFoodLevel(player: Player, food: ItemStack): Int {
        val playerFoodLevel = player.foodLevel
        val foodHasLevel = acquisitionRecoveryAmount(food)
        val totalFoodLevel = playerFoodLevel + foodHasLevel
        return if (totalFoodLevel > 20) { 20 } else { totalFoodLevel }
    }
    fun increaseStatus(player: Player, food: ItemStack) {
        val aoringoPlayer = AoringoPlayer(player)
        val foodName = food.itemMeta?.displayName
        val powerUpFood = mapOf<String, PowerUpFood>(
            "${ChatColor.YELLOW}力のプロテイン" to ForceProtein(),
            "${ChatColor.RED}ハートのハーブ" to HeartHerb()
        )
        powerUpFood[foodName]?.powerUp(aoringoPlayer)
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
    fun makeExpirationDate(period: Int): String {
        val date = increaseDay(period)
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1 // 月は0から始まるため+1
        val day = date.get(Calendar.DAY_OF_MONTH)
        return "消費期限: $year/$month/$day"
    }
    private fun increaseDay(amount: Int): Calendar {
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_WEEK, amount)
        return now
    }
    fun isExpirationDateHasExpired(player: Player, item: ItemStack): Boolean {
        val playerClass = AoringoPlayer(player)
        val itemLore = item.itemMeta?.lore
        val expiration = itemLore?.get(0) ?: return false
        val date = takeOutDate(expiration)

        val diff = compareDateFromCurreantDate(date)
        if (diff > 0) {
            playerClass.sendErrorMessage("消費期限が切れています")
            return true
        }
        return false
    }
    private fun compareDateFromCurreantDate(date: Date): Long {
        val currentDate = Date()
        val diff = currentDate.time - date.time
        return convertMillisecondsToDay(diff)
    }
    private fun convertMillisecondsToDay(diff: Long): Long {
        return diff / (1000 * 60 * 60 * 24)
    }
    private fun takeOutDate(lore: String): Date {
        val expiration = lore
        val dateStr = expiration.replace("消費期限:", "")
        val dateFormat = SimpleDateFormat("yyyy/MM/dd") // フォーマットに合わせて変更
        return dateFormat.parse(dateStr)
    }
    fun giveDiarrheaEffect(player: Player) {
        val playerClass = AoringoPlayer(player)
        val potionTime = 5 * 20
        val potionLevel = 100
        playerClass.sendErrorMessage("お腹を下した")
        val poisonEffect = PotionEffect(PotionEffectType.POISON, potionTime, potionLevel) // 持続時間をticksに変換
        val hungerEffect = PotionEffect(PotionEffectType.HUNGER, potionTime, potionLevel) // 持続時間をticksに変換
        player.addPotionEffect(poisonEffect)
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
