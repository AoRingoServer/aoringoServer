package com.github.Ringoame196.Cook

import com.github.Ringoame196.Entity.ArmorStand
import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Data.CookData
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.block.Barrel
import org.bukkit.block.Block
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

class Cook(val food: Food = Food(), val cookData: CookData = CookData(), private val cookArmorStand: ArmorStand = ArmorStand()) {
    val armorStandTag = "cookGame"
    private val itemFrame = com.github.Ringoame196.Entity.ItemFrame()
    fun summonChoppingBoard(block: Block) {
        val location = block.location.clone().add(0.0, 1.0, 0.0)
        val itemFrame = itemFrame.summonItemFrame(location)
        itemFrame.customName = "まな板"
    }
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
    fun dressing(player: Player, entity: ItemFrame) {
        val item = player.inventory.itemInMainHand
        val playerItem = player.inventory.itemInMainHand
        if (food.isExpirationDateHasExpired(player, entity.item)) { return }
        playerItem.amount = playerItem.amount - 1
        val dressingItem = cookData.dressing(item) ?: return
        if (!isCookLevel(dressingItem.itemMeta?.displayName?:return, player)) {
            return
        }
        player.inventory.setItemInMainHand(playerItem)
        player.inventory.addItem(dressingItem)
        entity.world.spawnParticle(Particle.EXPLOSION_HUGE, entity.location.add(0.0, 1.0, 0.0), 1)
        player.world.playSound(player.location, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
    }
    fun pot(block: Block, player: Player, plugin: Plugin) {
        val barrel = block.state as Barrel
        if (barrel.customName != null) { return }
        val ingredients = mutableListOf<String>()
        for (item in barrel.inventory) {
            item ?: continue
            ingredients.add(item.itemMeta?.displayName ?: continue)
            if (food.isExpirationDateHasExpired(player, item)) { return }
        }
        val finishFood = cookData.pot(ingredients) ?: return
        if (!isCookLevel(finishFood.itemMeta?.displayName?:return, player)) {
            return
        }
        for (item in barrel.inventory) {
            item ?: continue
            item.amount = item.amount - 1
        }
        posCooking(plugin, block, finishFood, player)
    }
    fun mix(player: Player, barrel: Barrel) {
        if (Random.nextInt(0, 8) != 0) { return }
        val recipe = mutableListOf<String>()
        var expiration = false
        for (i in 0 until barrel.inventory.size) {
            val item = barrel.inventory.getItem(i) ?: continue
            recipe.add(item.itemMeta?.displayName ?: continue)
            if (food.isExpirationDateHasExpired(player, item)) {
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
    fun fry(player: Player, block: Block, item: ItemStack, plugin: Plugin) {
        val fryItem = cookData.fly(item) ?: return
        if (!Cook().isCookLevel(fryItem.itemMeta?.displayName ?: return, player)) {
            return
        }
        Item().reduceMainItem(player)
        player.playSound(player, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
        if (food.isExpirationDateHasExpired(player, item)) { return }
        val timer = cookArmorStand.summonMarker(block.location.clone().add(0.5, 1.0, 0.5), " ", armorStandTag)
        val level = Scoreboard().getValue("cookingLevel", player.uniqueId.toString())
        var c = 15 - (level * 2)
        object : BukkitRunnable() {
            override fun run() {
                if (block.location.block.type != Material.LAVA_CAULDRON) {
                    timer.remove()
                    this.cancel()
                }
                c--
                timer.customName = "${ChatColor.YELLOW}${c}秒"
                block.world.playSound(block.location, Sound.BLOCK_LAVA_POP, 1f, 1f)
                if (c == 0) {
                    Item().drop(block.location.clone().add(0.5, 1.0, 0.5), cookData.fly(item) ?: return)
                    timer.remove()
                    block.world.playSound(block.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
    private fun posCooking(plugin: Plugin, block: Block, item: ItemStack, player: Player) {
        val armorStand = cookArmorStand.summonMarker(block.location.clone().add(0.5, 1.0, 0.5), "", armorStandTag)
        val level = Scoreboard().getValue("cookingLevel", player.uniqueId.toString())
        var c = 30 - (level * 2)
        val barrel = block.state as Barrel
        barrel.customName = "${ChatColor.RED}オープン禁止"
        barrel.update()
        object : BukkitRunnable() {
            override fun run() {
                c--
                armorStand.customName = "${ChatColor.YELLOW}${c}秒"
                block.world.playSound(block.location, Sound.BLOCK_LAVA_POP, 1f, 1f)
                if (block.location.block.type != Material.BARREL || block.location.clone().add(0.0, -1.0, 0.0).block.type != Material.CAMPFIRE) {
                    armorStand.remove()
                    this.cancel()
                }

                if (c == 0) {
                    barrel.customName = null
                    barrel.update()
                    Item().drop(block.location.clone().add(0.5, 1.0, 0.5), item)
                    armorStand.remove()
                    block.world.playSound(block.location, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
    fun findArmorStandsInRadius(location: Location, radius: Double): List<org.bukkit.entity.ArmorStand> {
        val world: World? = location.world?.name?.let { Bukkit.getWorld(it) }

        val armorStandsInRange = mutableListOf<org.bukkit.entity.ArmorStand>()

        if (world != null) {
            for (entity in world.entities) {
                if (entity is org.bukkit.entity.ArmorStand) {
                    val entityLocation = entity.location
                    val distance = location.distance(entityLocation)

                    if (distance <= radius) {
                        armorStandsInRange.add(entity)
                    }
                }
            }
        }

        return armorStandsInRange
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
            } - Scoreboard().getValue("cookCount", player.uniqueId.toString())
            com.github.Ringoame196.Entity.AoringoPlayer(player).sendActionBar("${ChatColor.AQUA}レベルマップまで残り${nextLevel}料理")
        }
    }
}
