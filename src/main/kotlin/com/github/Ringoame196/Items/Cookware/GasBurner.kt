package com.github.Ringoame196.Items.Cookware

import com.github.Ringoame196.CookManager
import com.github.Ringoame196.Entity.ItemFrame
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.Smoker
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class GasBurner(private val cookManager: CookManager = CookManager()) {
    private val itemFrame = ItemFrame()
    private val armorStandTag = cookManager.armorStandTag
    fun summonIronPlate(block: Block) {
        val location = block.location.clone().add(0.0, 1.0, 0.0)
        val ironPlate = itemFrame.summonItemFrame(location)
        itemFrame.changeTransparency(ironPlate)
    }
    fun bakingFoods(plugin: Plugin, player: Player, ironPlate: org.bukkit.entity.ItemFrame, smoker: Smoker) {
        var c = cookManager.calculateCookTime(10, player)
        itemFrame.changeTransparency(ironPlate)
        val display = cookManager.cookArmorStand.summonMarker(ironPlate.location, "", armorStandTag)
        val world = ironPlate.world
        val item = ironPlate.item
        if (cookManager.foodManager.isExpirationDateHasExpired(player, item)) {
            display.remove()
            return
        }
        object : BukkitRunnable() {
            override fun run() {
                c--
                val ingredient = ironPlate.item
                val power: Short = 40
                world.playSound(ironPlate.location, Sound.BLOCK_FIRE_AMBIENT, 1f, 1f)
                smoker.burnTime = power
                display.customName = if (c >= 0) {
                    "${ChatColor.YELLOW}完成まで${c}秒"
                } else {
                    "${ChatColor.DARK_RED}焼きすぎ注意！！"
                }
                smoker.update()
                if (c == 0) {
                    completeBaking(ingredient, player, ironPlate)
                } else if (c == -10 || ingredient.type == Material.AIR) {
                    singeFoods(ironPlate)
                    ironPlate.setItem(ItemStack(Material.AIR))
                    display.remove()
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20) // 1秒間隔 (20 ticks) でタスクを実行
    }
    private fun singeFoods(ironPlate: org.bukkit.entity.ItemFrame) {
        val world = ironPlate.world
        val item = ironPlate.item
        if (item.type == Material.AIR) { return }
        ironPlate.setItem(ItemStack(Material.AIR))
        world.playSound(ironPlate.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
    }
    private fun completeBaking(item: ItemStack, player: Player, ironPlate: org.bukkit.entity.ItemFrame) {
        val bakeItem = cookManager.cookData.bake(item) ?: return
        val world = player.world
        if (!cookManager.isCookLevel(bakeItem.itemMeta?.displayName?:return, player)) {
            return
        }
        ironPlate.setItem(bakeItem)
        world.playSound(ironPlate.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
    }
    fun breakGusBurner(block: Block) {
        for (
            entity in block.world.getNearbyEntities(
                block.location.clone().add(0.0, 1.0, 0.0),
                0.5,
                0.5,
                0.5
            )
        ) {
            if (entity !is org.bukkit.entity.ItemFrame) {
                continue
            }
            entity.remove()
        }
    }
}
