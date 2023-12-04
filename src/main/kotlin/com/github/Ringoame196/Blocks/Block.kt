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
    fun enchantGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.RED}エンチャント")
        for (i in 0..7) {
            gui.setItem(i, Item().make(Material.RED_STAINED_GLASS_PANE, " ", null, null, 1))
        }
        gui.setItem(8, Item().make(Material.ENCHANTING_TABLE, "${ChatColor.AQUA}エンチャント", null, null, 1))
        gui.setItem(4, ItemStack(Material.AIR))
        return gui
    }
    fun enchant(player: Player, gui: InventoryView, plugin: Plugin) {
        val enchantBook = mutableListOf(
            Item().enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1),
            Item().enchant(Enchantment.PROTECTION_FIRE, 1),
            Item().enchant(Enchantment.PROTECTION_FALL, 1),
            Item().enchant(Enchantment.PROTECTION_EXPLOSIONS, 1),
            Item().enchant(Enchantment.PROTECTION_PROJECTILE, 1),
            Item().enchant(Enchantment.WATER_WORKER, 1),
            Item().enchant(Enchantment.THORNS, 1),
            Item().enchant(Enchantment.DEPTH_STRIDER, 1),
            Item().enchant(Enchantment.SOUL_SPEED, 1),
            Item().enchant(Enchantment.BINDING_CURSE, 1),
            Item().enchant(Enchantment.VANISHING_CURSE, 1),
            Item().enchant(Enchantment.DAMAGE_ALL, 1),
            Item().enchant(Enchantment.DAMAGE_ARTHROPODS, 1),
            Item().enchant(Enchantment.DAMAGE_UNDEAD, 1),
            Item().enchant(Enchantment.KNOCKBACK, 1),
            Item().enchant(Enchantment.PROTECTION_FIRE, 1),
            Item().enchant(Enchantment.LOOT_BONUS_MOBS, 1),
            Item().enchant(Enchantment.SWEEPING_EDGE, 1),
            Item().enchant(Enchantment.DIG_SPEED, 1),
            Item().enchant(Enchantment.SILK_TOUCH, 1),
            Item().enchant(Enchantment.ARROW_DAMAGE, 1),
            Item().enchant(Enchantment.ARROW_KNOCKBACK, 1),
            Item().enchant(Enchantment.ARROW_FIRE, 1),
            Item().enchant(Enchantment.ARROW_INFINITE, 1),
            Item().enchant(Enchantment.LOYALTY, 1),
            Item().enchant(Enchantment.LUCK, 1),
            Item().enchant(Enchantment.IMPALING, 1),
            Item().enchant(Enchantment.RIPTIDE, 1),
            Item().enchant(Enchantment.CHANNELING, 1),
            Item().enchant(Enchantment.QUICK_CHARGE, 1),
            Item().enchant(Enchantment.PIERCING, 1),
            Item().enchant(Enchantment.MULTISHOT, 1),
            Item().enchant(Enchantment.SWEEPING_EDGE, 1),
        )
        player.foodLevel -= 10
        val max = enchantBook.size - 1
        var c = Random.nextInt(10, 20)
        object : BukkitRunnable() {
            override fun run() {
                c--
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                gui.setItem(4, enchantBook[Random.nextInt(0, max)])
                if (c == 0) {
                    player.inventory.addItem(gui.getItem(4))
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                    player.closeInventory()
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 5L) // 1秒間隔 (20 ticks) でタスクを実行
    }
}
