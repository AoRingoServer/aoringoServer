package com.github.Ringoame196.Smartphones.Applications

import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class OPApplication : Application {
    override fun getCustomModelData(): Int {
        return 7
    }
    override fun bootApplication(player: Player, plugin: Plugin) {
        val itemManager = ItemManager()
        if (!player.isOp) { return }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}OP用")
        gui.setItem(0, itemManager.make(Material.COMMAND_BLOCK, "${ChatColor.YELLOW}リソパ更新"))
        gui.setItem(2, itemManager.make(Material.WOODEN_AXE, "${ChatColor.RED}ショップ保護リセット"))
        gui.setItem(4, itemManager.make(Material.DIAMOND, "${ChatColor.GREEN}運営ギフトリセット"))
        gui.setItem(6, itemManager.make(Material.CRAFTING_TABLE, "${ChatColor.GREEN}テストワールド"))
        player.openInventory(gui)
    }
}
