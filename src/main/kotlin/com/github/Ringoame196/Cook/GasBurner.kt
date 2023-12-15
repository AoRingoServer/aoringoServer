package com.github.Ringoame196.Cook

import com.github.Ringoame196.Entity.ArmorStand
import com.github.Ringoame196.Entity.ItemFrame
import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Job.Data.CookData
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.Smoker
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class GasBurner {
    val cook = Cook()
    val itemFrame = ItemFrame()
    val food = Food()
    val armorStandTag = cook.armorStandTag
    val cookData = CookData()
    val armorStand = ArmorStand()
    fun summonIronPlate(block: Block) {
        val location = block.location.clone().add(0.0, 1.0, 0.0)
        val ironPlate = itemFrame.summonItemFrame(location)
        itemFrame.changeTransparency(ironPlate)
    }
    fun bakingFoods(plugin: Plugin, player: Player, ironPlate: org.bukkit.entity.ItemFrame, smoker: Smoker) {
        var c = 0
        val completeTime = cook.calculateCookTime(10, player)
        itemFrame.changeTransparency(ironPlate)
        val display = armorStand.summonMarker(ironPlate.location, "", armorStandTag)
        val world = ironPlate.world
        val burnedTime = completeTime * 2
        val item = ironPlate.item
        if (food.isExpirationDateHasExpired(player, item)) {
            display.remove()
            return
        }
        object : BukkitRunnable() {
            override fun run() {
                c++
                world.playSound(ironPlate.location, Sound.BLOCK_FIRE_AMBIENT, 1f, 1f)
                smoker.burnTime = 40
                display.customName = "${ChatColor.YELLOW}${c}秒"
                smoker.update()
                if (c == completeTime) {
                    completeBaking(item, player, ironPlate)
                } else if (c == burnedTime || item.type == Material.AIR) {
                    if (item.type != Material.AIR) {
                        ironPlate.setItem(ItemStack(Material.AIR))
                        world.playSound(ironPlate.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                    }
                    display.remove()
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20) // 1秒間隔 (20 ticks) でタスクを実行
    }
    private fun completeBaking(item: ItemStack, player: Player, ironPlate: org.bukkit.entity.ItemFrame) {
        val bakeItem = cookData.bake(item) ?: return
        val world = player.world
        if (!cook.isCookLevel(bakeItem.itemMeta?.displayName?:return, player)) {
            return
        }
        ironPlate.setItem(bakeItem)
        world.playSound(ironPlate.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
    }
}