package com.github.Ringoame196.GUIs

import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.AddMember
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.Delete
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.DeletionMember
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.GetWoodenAxe
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.Information
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.ProtectionCreationButton
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.ProtectionInformation
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.ProtectionListButton
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class WGGUI : GUI {
    override val guiName: String = "${ChatColor.YELLOW}WorldGuardGUI"
    override fun createGUI(player: Player?): Inventory {
        val itemManager = ItemManager()
        val gui = Bukkit.createInventory(null, 9, guiName)
        gui.setItem(
            2,
            itemManager.make(Material.GOLDEN_AXE, "${ChatColor.YELLOW}保護作成")
        )
        gui.setItem(4, itemManager.make(Material.MAP, "${ChatColor.GREEN}情報"))
        gui.setItem(6, itemManager.make(Material.CHEST, "${ChatColor.AQUA}保護一覧"))
        gui.setItem(8, itemManager.make(Material.WOODEN_AXE, "${ChatColor.GOLD}木の斧ゲット"))
        return gui
    }
    override fun whenClickedItem(player: Player, item: ItemStack, shift: Boolean) {
        val clickItems = mapOf<String, com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.LandProtectionApplicationButton>(
            "${ChatColor.GOLD}木の斧ゲット" to GetWoodenAxe(),
            "${ChatColor.AQUA}保護一覧" to ProtectionListButton(),
            "${ChatColor.YELLOW}保護作成" to ProtectionCreationButton(),
            "${ChatColor.GREEN}情報" to Information(),
            "${ChatColor.YELLOW}保護情報" to ProtectionInformation(),
            "${ChatColor.AQUA}メンバー追加" to AddMember(),
            "${ChatColor.RED}メンバー削除" to DeletionMember(),
            "${ChatColor.RED}削除" to Delete()
        )
        val name = item.itemMeta?.displayName
        clickItems[name]?.click(player, shift) ?: return
    }
}
