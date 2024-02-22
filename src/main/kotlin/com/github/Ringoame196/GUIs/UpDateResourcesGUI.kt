package com.github.Ringoame196.GUIs

import com.github.Ringoame196.ResourcesManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class UpDateResourcesGUI(val plugin: Plugin) : GUI {
    override val guiName: String = "${ChatColor.BLUE}アップデートするファイル"
    override fun createGUI(player: Player?): Inventory {
        return ResourcesManager().makeSelectFileGUI(guiName)
    }

    override fun whenClickedItem(player: Player, item: ItemStack,shift:Boolean) {
        ResourcesManager().update(plugin, item.itemMeta?.displayName ?: "", player)
    }
}
