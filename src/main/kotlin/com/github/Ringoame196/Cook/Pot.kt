package com.github.Ringoame196.Cook

import com.github.Ringoame196.Entity.ArmorStand
import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Data.CookData
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Pot {
    private val cookData = CookData()
    private val cook = Cook()
    private val food = Food()
    private val cookArmorStand = ArmorStand()
    fun boil(block: Block, player: Player, plugin: Plugin) {
        val barrel = block.state as Barrel
        if (barrel.customName != null) { return }
        val ingredients = mutableListOf<String>()
        for (item in barrel.inventory) {
            item ?: continue
            ingredients.add(item.itemMeta?.displayName ?: continue)
            if (food.isExpirationDateHasExpired(player, item)) { return }
        }
        val finishFood = cookData.pot(ingredients) ?: return
        if (!cook.isCookLevel(finishFood.itemMeta?.displayName?:return, player)) {
            return
        }
        for (item in barrel.inventory) {
            item ?: continue
            item.amount = item.amount - 1
        }
        posCooking(plugin, block, finishFood, player)
    }
    private fun posCooking(plugin: Plugin, block: Block, item: ItemStack, player: Player) {
        val summonLocation = block.location.clone().add(0.5, 1.0, 0.5)
        val armorStand = cookArmorStand.summonMarker(summonLocation, "", cook.armorStandTag)
        var c = cook.calculateCookTime(30, player)
        val barrel = block.state as Barrel
        barrel.customName = "${ChatColor.RED}オープン禁止"
        barrel.update()
        object : BukkitRunnable() {
            override fun run() {
                c--
                armorStand.customName = "${ChatColor.YELLOW}${c}秒"
                block.world.playSound(block.location, Sound.BLOCK_LAVA_POP, 1f, 1f)
                val downBlock = block.location.clone().add(0.0, -1.0, 0.0).block
                if (block.location.block.type != Material.BARREL || downBlock.type != Material.CAMPFIRE) {
                    armorStand.remove()
                    this.cancel()
                }

                if (c == 0) {
                    barrel.customName = null
                    barrel.update()
                    val dropLocation = block.location.clone().add(0.5, 1.0, 0.5)
                    Item().drop(dropLocation, item)
                    armorStand.remove()
                    block.world.playSound(block.location, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
}
