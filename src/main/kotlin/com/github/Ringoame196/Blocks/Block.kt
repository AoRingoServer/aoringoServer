package com.github.Ringoame196.Blocks

import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

class Block {
    fun makeEnchantGUI(): Inventory {
        val guiSize = 9
        val gui = Bukkit.createInventory(null, guiSize, "${ChatColor.RED}エンチャント")
        val putInSlot = 4
        val enchantButtonSlot = 8
        for (i in 0 until guiSize) {
            gui.setItem(i, Item().make(Material.RED_STAINED_GLASS_PANE, " "))
        }
        gui.setItem(enchantButtonSlot, Item().make(Material.ENCHANTING_TABLE, "${ChatColor.AQUA}エンチャント"))
        gui.setItem(putInSlot, ItemStack(Material.AIR))
        return gui
    }
    fun giveEnchantBook(player: Player, gui: InventoryView, plugin: Plugin) {
        val item = Item()
        val enchantBookList = mutableListOf(
            item.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1),
            item.enchant(Enchantment.PROTECTION_FIRE, 1),
            item.enchant(Enchantment.PROTECTION_FALL, 1),
            item.enchant(Enchantment.PROTECTION_EXPLOSIONS, 1),
            item.enchant(Enchantment.PROTECTION_PROJECTILE, 1),
            item.enchant(Enchantment.WATER_WORKER, 1),
            item.enchant(Enchantment.THORNS, 1),
            item.enchant(Enchantment.DEPTH_STRIDER, 1),
            item.enchant(Enchantment.SOUL_SPEED, 1),
            item.enchant(Enchantment.BINDING_CURSE, 1),
            item.enchant(Enchantment.VANISHING_CURSE, 1),
            item.enchant(Enchantment.DAMAGE_ALL, 1),
            item.enchant(Enchantment.DAMAGE_ARTHROPODS, 1),
            item.enchant(Enchantment.DAMAGE_UNDEAD, 1),
            item.enchant(Enchantment.KNOCKBACK, 1),
            item.enchant(Enchantment.PROTECTION_FIRE, 1),
            item.enchant(Enchantment.LOOT_BONUS_MOBS, 1),
            item.enchant(Enchantment.SWEEPING_EDGE, 1),
            item.enchant(Enchantment.DIG_SPEED, 1),
            item.enchant(Enchantment.SILK_TOUCH, 1),
            item.enchant(Enchantment.ARROW_DAMAGE, 1),
            item.enchant(Enchantment.ARROW_KNOCKBACK, 1),
            item.enchant(Enchantment.ARROW_FIRE, 1),
            item.enchant(Enchantment.ARROW_INFINITE, 1),
            item.enchant(Enchantment.LOYALTY, 1),
            item.enchant(Enchantment.LUCK, 1),
            item.enchant(Enchantment.IMPALING, 1),
            item.enchant(Enchantment.RIPTIDE, 1),
            item.enchant(Enchantment.CHANNELING, 1),
            item.enchant(Enchantment.QUICK_CHARGE, 1),
            item.enchant(Enchantment.PIERCING, 1),
            item.enchant(Enchantment.MULTISHOT, 1),
            item.enchant(Enchantment.SWEEPING_EDGE, 1),
        )
        player.foodLevel -= 10
        val pieces = enchantBookList.size - 1
        val minimumPerformance = 10
        val maxPerformance = 20
        var performances = Random.nextInt(minimumPerformance, maxPerformance)
        val enchantBookSlot = 4
        val frequency = 5L
        object : BukkitRunnable() {
            override fun run() {
                performances--
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                gui.setItem(enchantBookSlot, enchantBookList[Random.nextInt(0, pieces)])
                if (performances == 0) {
                    val enchantBook = gui.getItem(enchantBookSlot)
                    giveEnchantBookFromPlayer(player,enchantBook?:return)
                    player.closeInventory()
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, frequency) // 1秒間隔 (20 ticks) でタスクを実行
    }
    private fun giveEnchantBookFromPlayer(player: Player, enchantBook:ItemStack){
        player.inventory.addItem(enchantBook)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
