package com.github.Ringoame196.Smartphones.APKs

import com.github.Ringoame196.APKs
import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class OPAPK : APKs {
    override val customModelData: Int = 7
    override fun openGUI(player: Player, plugin: Plugin) {
        val item = Item()
        if (!player.isOp) { return }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}OP用")
        gui.setItem(0, item.make(Material.COMMAND_BLOCK, "${ChatColor.YELLOW}リソパ更新"))
        gui.setItem(2, item.make(Material.WOODEN_AXE, "${ChatColor.RED}ショップ保護リセット"))
        gui.setItem(4, item.make(Material.DIAMOND, "${ChatColor.GREEN}運営ギフトリセット"))
        gui.setItem(6, item.make(Material.CRAFTING_TABLE, "${ChatColor.GREEN}テストワールド"))
        player.openInventory(gui)
    }
}
