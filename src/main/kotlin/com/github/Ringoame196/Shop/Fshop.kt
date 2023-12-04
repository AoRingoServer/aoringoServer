package com.github.Ringoame196.Shop

import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Data.WorldGuard
import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.block.Sign
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID

class Fshop {
    fun make(sign: Sign, player: Player) {
        val downBlock = sign.block.location.clone().add(0.0, -1.0, 0.0).block
        if (downBlock.type != Material.BARREL) { return }
        val itemFrame = sign.world.spawn(sign.location, org.bukkit.entity.ItemFrame::class.java)
        itemFrame.customName = "@Fshop,userID:${player.uniqueId},price:${sign.getLine(1)}"
        sign.type = Material.AIR
    }
    fun isOwner(player: Player, location: Location): Boolean {
        return WorldGuard().getOwnerOfRegion(location)?.contains(player.uniqueId) == true || WorldGuard().getMemberOfRegion(location)?.contains(player.uniqueId) == true
    }
    fun buyGUI(item: ItemStack, name: String, uuid: String): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}Fショップ")
        val index = name.indexOf("price:")
        val price = name.substring(index + 6, name.length)
        gui.setItem(0, Item().make(Material.COMPASS, "ショップ", uuid))
        gui.setItem(3, item)
        gui.setItem(4, Item().make(Material.EMERALD_BLOCK, "${ChatColor.GREEN}購入", "${price}円"))
        return gui
    }
    fun buy(player: Player, item: ItemStack, price: Int, uuid: String) {
        val itemFrame = Bukkit.getEntity(UUID.fromString(uuid)) ?: return
        val playerClass = com.github.Ringoame196.Entity.AoringoPlayer(player)
        if (itemFrame !is ItemFrame) {
            playerClass.sendErrorMessage("ショップが見つかりませんでした")
            return
        }
        if (itemFrame.item != item) {
            playerClass.sendErrorMessage("売り物が更新されました")
            return
        }
        if (Money().get(player.uniqueId.toString()) < price) {
            playerClass.sendErrorMessage("お金が足りません")
            return
        }
        val name = itemFrame.customName
        val owner = name?.substring(name.indexOf("userID:") + 7)
        player.inventory.addItem(item)
        Money().remove(player.uniqueId.toString(), price, false)
        Money().add(owner?.substring(0, owner.indexOf(",")) ?: return, price, false)
        player.sendMessage("${ChatColor.GREEN}購入しました")
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        replenishment(itemFrame)
        if (itemFrame.item.type == Material.AIR) {
            player.closeInventory()
        } else { player.openInventory(buyGUI(itemFrame.item, itemFrame.customName ?: return, itemFrame.uniqueId.toString())) }
    }
    private fun replenishment(itemFrame: ItemFrame) {
        val block = itemFrame.location.add(0.0, -1.0, 0.0).block
        if (block.type != Material.BARREL) { return }
        val barrel = block.state as Barrel
        itemFrame.setItem(ItemStack(Material.AIR))
        for (item in barrel.inventory) {
            item ?: continue
            itemFrame.setItem(item)
            item.amount = item.amount - 1
            return
        }
    }
}
