package com.github.Ringoame196

import com.github.Ringoame196.Entity.ArmorStand
import com.github.Ringoame196.Foods.FoodManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class CookManager() {
    val cookData = CookData()
    val foodManager = FoodManager()
    val cookArmorStand = ArmorStand()
    val armorStandTag = "cookGame"
    fun acquisitionCookLevel(uuid: String): Int {
        val scoreboardName = "cookingLevel"
        return Scoreboard().getValue(scoreboardName, uuid)
    }
    fun calculateCookTime(cookTime: Int, player: Player): Int {
        val level = acquisitionCookLevel(player.uniqueId.toString())
        val shortening = level * 2
        return cookTime - shortening
    }
    fun knifeSharpness(item: ItemStack): ItemStack {
        val max = when (item.type) {
            Material.STONE_SWORD -> 8
            Material.IRON_SWORD -> 13
            Material.DIAMOND_SWORD -> 20
            else -> 0
        }
        val sharpness = Random.nextInt(0, max)
        val meta = item.itemMeta
        meta?.lore = mutableListOf("切れ味:$sharpness")
        item.setItemMeta(meta)
        return item
    }
    fun mix(player: Player, barrel: Barrel) {
        if (Random.nextInt(0, 8) != 0) { return }
        val recipe = mutableListOf<String>()
        var expiration = false
        for (i in 0 until barrel.inventory.size) {
            val item = barrel.inventory.getItem(i) ?: continue
            recipe.add(item.itemMeta?.displayName ?: continue)
            if (foodManager.isExpirationDateHasExpired(player, item)) {
                expiration = true
            }
        }
        val finishFood = if (expiration) { cookData.fermentationMix(recipe) } else { cookData.mix(recipe) } ?: return
        if (!isCookLevel(finishFood.itemMeta?.displayName?:return, player)) {
            return
        }
        barrel.world.dropItem(barrel.location.clone().add(0.5, 1.5, 0.5), finishFood)
        for (i in 0 until barrel.inventory.size) {
            val item = barrel.inventory.getItem(i) ?: continue
            item.amount = barrel.inventory.getItem(i)!!.amount - 1
        }
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }

    fun isCookLevel(itemName: String, player: Player): Boolean {
        val level = Scoreboard().getValue("cookLevel", player.uniqueId.toString())
        val cookLevel = getcookLevel(itemName)
        levelUP(player, itemName)
        return cookLevel <= level
    }
    private fun getcookLevel(itemName: String): Int {
        return when (itemName) {
            else -> 0
        }
    }
    fun levelUP(player: Player, itemName: String) {
        if (!itemName.contains("[完成品]")) { return }
        Scoreboard().add("cookCount", player.uniqueId.toString(), 1)
        val levelUP = mutableListOf(100, 1000, 10000)
        if (levelUP.contains(Scoreboard().getValue("cookCount", player.uniqueId.toString()))) {
            player.sendMessage("${ChatColor.YELLOW}料理人レベルアップ！")
            player.playSound(player, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
            Scoreboard().add("cookLevel", player.uniqueId.toString(), 1)
        } else {
            val nextLevel = when (Scoreboard().getValue("cookLevel", player.uniqueId.toString())) {
                in 0..100 -> 100
                in 101..1000 -> 1000
                in 1001..10000 -> 10000
                else -> 0
            }
            val remainingLevel = nextLevel - Scoreboard().getValue("cookCount", player.uniqueId.toString())
            com.github.Ringoame196.Entity.AoringoPlayer(player).sendActionBar("${ChatColor.AQUA}レベルマップまで残り${remainingLevel}料理")
        }
    }
}
