package com.github.Ringoame196

import com.github.Ringoame196.Items.Item
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
            gui.setItem(i, Item().make(material = Material.PAPER, name = " ", customModelData = 6))
        }
        paper(gui, 3)
        paper(gui, 12)
        paper(gui, 21)
        return gui
    }
    private fun paper(gui: Inventory, c: Int) {
        for (i in 0..2) {
            gui.setItem(i + c, Item().make(material = Material.PAPER, name = "${ChatColor.RED}削る", customModelData = 7))
        }
    }
    fun click(itemList: MutableList<Material>): ItemStack {
        return ItemStack(itemList[Random.nextInt(0, itemList.size)])
    }
    fun check(gui: InventoryView, item: ItemStack): Int {
        if (item.type == Material.BARRIER) { return 0 }
        var c = 0
        if (gui.getItem(3) == item) {
            c += 1
        }
        if (gui.getItem(4) == item) {
            c += 1
        }
        if (gui.getItem(5) == item) {
            c += 1
        }
        if (gui.getItem(12) == item) {
            c += 1
        }
        if (gui.getItem(13) == item) {
            c += 1
        }
        if (gui.getItem(14) == item) {
            c += 1
        }
        if (gui.getItem(21) == item) {
            c += 1
        }
        if (gui.getItem(22) == item) {
            c += 1
        }
        if (gui.getItem(23) == item) {
            c += 1
        }
        return c
    }
    fun result(result: Boolean, player: Player, price: Int) {
        player.closeInventory()
        if (result) {
            player.sendMessage("${ChatColor.GREEN}[スクラッチ]当選！")
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1f, 1f)
            Money().add(player.uniqueId.toString(), price, true)
        } else {
            player.sendMessage("${ChatColor.GOLD}[スクラッチ]ハズレ")
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        }
    }
}
