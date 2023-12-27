package com.github.Ringoame196

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class Scratch {
    fun createGUI(title: String): Inventory {
        val gui = Bukkit.createInventory(null, 27, title)
        for (i in 0..26) {
            gui.setItem(i, ItemManager().make(Material.PAPER, customModelData = 6))
        }
        paper(gui, 3)
        paper(gui, 12)
        paper(gui, 21)
        return gui
    }
    private fun paper(gui: Inventory, c: Int) {
        for (i in 0..2) {
            gui.setItem(i + c, ItemManager().make(Material.PAPER, "${ChatColor.RED}削る", customModelData = 7))
        }
    }
    fun click(itemList: MutableList<Material>): ItemStack {
        return ItemStack(itemList[Random.nextInt(0, itemList.size)])
    }
    fun countItem(gui: InventoryView, item: ItemStack): Int {
        if (item.type == Material.BARRIER) {
            return 0
        }

        val slots = listOf(3, 4, 5, 12, 13, 14, 21, 22, 23)
        return slots.count { gui.getItem(it) == item }
    }
    fun result(result: Boolean, player: Player, price: Int) {
        val aoringoPlayer = AoringoPlayer(player)
        val moneyUseCase = aoringoPlayer.moneyUseCase
        player.closeInventory()
        if (result) {
            player.sendMessage("${ChatColor.GREEN}[スクラッチ]当選！")
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1f, 1f)
            moneyUseCase.getMoneyFromAdmin(aoringoPlayer, price)
        } else {
            player.sendMessage("${ChatColor.GOLD}[スクラッチ]ハズレ")
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        }
    }
}
