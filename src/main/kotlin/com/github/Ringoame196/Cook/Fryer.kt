package com.github.Ringoame196.Cook

import com.github.Ringoame196.Entity.ArmorStand
import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Data.CookData
import com.github.Ringoame196.Scoreboard
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Fryer {
    private val cookData = CookData()
    private val cook = Cook()
    private val food = Food()
    private val cookArmorStand = ArmorStand()
    fun deepFry(player: Player, block: Block, item: ItemStack, plugin: Plugin) {
        val fryItem = cookData.fly(item) ?: return
        if (!cook.isCookLevel(fryItem.itemMeta?.displayName ?: return, player)) {
            return
        }
        Item().reduceMainItem(player)
        player.playSound(player, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
        if (food.isExpirationDateHasExpired(player, item)) { return }
        val summonLocation = block.location.clone().add(0.5, 1.0, 0.5)
        val timer = cookArmorStand.summonMarker(summonLocation, " ", cook.armorStandTag)
        var c = cook.calculateCookTime(15,player)
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
                    Item().drop(dropLocation, cookData.fly(item) ?: return)
                    timer.remove()
                    block.world.playSound(block.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
}