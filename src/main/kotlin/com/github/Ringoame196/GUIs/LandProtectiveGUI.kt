package com.github.Ringoame196.GUIs

import com.github.Ringoame196.Admin
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.MoneyManager
import com.github.Ringoame196.MoneyUseCase
import com.github.Ringoame196.PluginData
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class LandProtectiveGUI() : GUI {
    override val guiName: String = "${ChatColor.BLUE}保護設定"
    override fun createGUI(player: Player?): Inventory {
        val gui = Bukkit.createInventory(null, 9, guiName)
        val slot = 4
        val price = LandPurchase().calculatePrice(player ?: return gui)
        val item = ItemManager().make(Material.GREEN_WOOL, "${ChatColor.GREEN}作成", "${price}円")
        gui.setItem(slot, item)
        return gui
    }

    override fun whenClickedItem(player: Player, item: ItemStack, shift: Boolean) {
        val itemName = item.itemMeta?.displayName
        if (itemName != "${ChatColor.GREEN}作成") { return }
        val playerClass = AoringoPlayer(player)
        if (player.world.name != "Home" && !player.isOp) {
            playerClass.sendErrorMessage("保護は生活ワールドのみ使用可能です")
            player.closeInventory()
            return
        }
        val aoringoPlayer = AoringoPlayer(player)
        val name = PluginData.DataManager.playerDataMap.getOrPut(aoringoPlayer.player.uniqueId) { AoringoPlayer.PlayerData() }.entry ?: return
        val playerAccount = aoringoPlayer.playerAccount
        val lore = item.itemMeta?.lore?.get(0) ?: ""
        val price = MoneyUseCase().convertingInt(lore)
        val world = player.world
        val playerMoney = aoringoPlayer.moneyUseCase.getMoney(playerAccount)
        if (playerMoney < price) {
            AoringoPlayer(player).sendErrorMessage("お金が足りません")
            return
        }
        player.performCommand("/expand vert")
        player.performCommand("rg claim $name")
        if (WorldGuard().getProtection(world, name)) {
            player.sendMessage("${ChatColor.GREEN}[WG]正常に保護をかけました")
            MoneyManager().tradeMoney(Admin(), playerAccount, price)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        }
        player.closeInventory()
        PluginData.DataManager.playerDataMap.getOrPut(aoringoPlayer.player.uniqueId) { AoringoPlayer.PlayerData() }.entry = null
    }
}
