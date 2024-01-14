package com.github.Ringoame196.Items.Cookware

import com.github.Ringoame196.CookManager
import com.github.Ringoame196.Items.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Fryer(private val cookManager: CookManager = CookManager()) {
    fun deepFry(player: Player, block: Block, item: ItemStack, plugin: Plugin) {
        val fryItem = cookManager.completionItem(cookManager.cookData.fly(item) ?: return, player)
        ItemManager().reduceMainItem(player)
        player.playSound(player, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
        if (cookManager.foodManager.isExpirationDateHasExpired(player, item)) { return }
        val summonLocation = block.location.clone().add(0.5, 1.0, 0.5)
        val timer = cookManager.cookArmorStand.summonMarker(summonLocation, " ", cookManager.armorStandTag)
        var c = cookManager.calculateCookTime(15, player)
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
                    val dropLocation = block.location.clone().add(0.5, 1.0, 0.5)
                    ItemManager().drop(dropLocation, fryItem)
                    timer.remove()
                    block.world.playSound(block.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
}
