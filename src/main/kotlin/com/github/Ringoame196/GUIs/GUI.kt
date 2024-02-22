package com.github.Ringoame196.GUIs

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

interface GUI {
    val guiName: String
    fun createGUI(player: Player?): Inventory // val gui = Bukkit.createInventory(null, 9, guiName)
    fun whenClickedItem(player: Player, item: ItemStack, shift:Boolean)
}
