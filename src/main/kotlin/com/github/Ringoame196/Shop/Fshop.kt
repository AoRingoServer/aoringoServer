package com.github.Ringoame196.Shop

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.MoneyUseCase
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.block.Sign
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Fshop {
    private val moneyUseCase = MoneyUseCase()
    private val worldguard = WorldGuard()
    private fun shopInfo(shop: ItemFrame): String {
        return shop.customName ?: throw RuntimeException("ショップ情報を取得できませんでした")
    }
    fun make(sign: Sign, player: Player) {
        val downBlock = sign.block.location.clone().add(0.0, -1.0, 0.0).block
        if (downBlock.type != Material.BARREL) { return }
        val itemFrame = sign.world.spawn(sign.location, org.bukkit.entity.ItemFrame::class.java)
        itemFrame.customName = "@Fshop,userID:${player.uniqueId},price:${sign.getLine(1)}"
    }
    fun getAccountID(shop: ItemFrame): String {
        return acquisitionAccountName(shop)
    }
    fun isOwner(player: Player, entity: Entity?): Boolean {
        entity ?: return false
        return worldguard.getOwnerOfRegion(entity.location)?.contains(player.uniqueId) == true || WorldGuard().getMemberOfRegion(entity.location)?.contains(player.uniqueId) == true
    }
    private fun acquisitionAccountName(shop: ItemFrame): String {
        val shopInfo = shopInfo(shop)
        val userIDStartIndex = shopInfo.indexOf("userID:") + 7
        val userIDEndIndex = shopInfo.indexOf(",", userIDStartIndex)

        if (userIDStartIndex < 0 || userIDEndIndex < 0) {
            throw RuntimeException("口座IDの取得に失敗しました")
        }

        return shopInfo.substring(userIDStartIndex, userIDEndIndex)
    }
    private fun acquisitionPrice(shop: ItemFrame): Int {
        val shopInfo = shopInfo(shop)
        val index = shopInfo.indexOf("price:")

        if (index < 0) {
            throw RuntimeException("価格の取得に失敗しました")
        }

        val priceSubstring = shopInfo.substring(index + 6)
        return priceSubstring.toIntOrNull() ?: throw RuntimeException("価格の変換に失敗しました")
    }
    fun makeBuyGUI(goods: ItemStack, shop: ItemFrame): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}Fショップ")
        val price = acquisitionPrice(shop)
        val itemManager = ItemManager()
        gui.setItem(0, itemManager.make(Material.COMPASS, "ショップ", shop.uniqueId.toString()))
        gui.setItem(3, goods)
        gui.setItem(4, itemManager.make(Material.EMERALD_BLOCK, "${ChatColor.GREEN}購入", "${price}円"))
        return gui
    }
    fun buy(aoringoPlayer: AoringoPlayer, item: ItemStack, shop: ItemFrame) {
        val sender = aoringoPlayer.player
        val price = acquisitionPrice(shop)
        if (shop.item != item) {
            aoringoPlayer.sendErrorMessage("売り物が更新されました")
            return
        }
        if (moneyUseCase.getMoney(aoringoPlayer.playerAccount) < price) {
            aoringoPlayer.sendErrorMessage("お金が足りません")
            return
        }
        sender.inventory.addItem(item)
        val account = ShopCoordinationAccount(shop)
        moneyUseCase.tradeMoney(aoringoPlayer, account, price)
        sender.sendMessage("${ChatColor.GREEN}購入しました")
        sender.playSound(sender, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        replenishment(shop)
        if (shop.item.type == Material.AIR) {
            sender.closeInventory()
        } else { sender.openInventory(makeBuyGUI(shop.item, shop)) }
    }
    private fun replenishment(shop: ItemFrame) {
        val block = shop.location.add(0.0, -1.0, 0.0).block
        if (block.type != Material.BARREL) {
            return
        }
        val barrel = block.state as Barrel
        shop.setItem(ItemStack(Material.AIR))
        for (item in barrel.inventory) {
            item ?: continue
            shop.setItem(item)
            item.amount = item.amount - 1
            return
        }
    }
}
