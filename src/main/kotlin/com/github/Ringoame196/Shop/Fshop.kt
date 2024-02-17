package com.github.Ringoame196.Shop

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.MoneyUseCase
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.block.Sign
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class Fshop(val plugin: Plugin) {
    private val moneyUseCase = MoneyUseCase()
    private val worldguard = WorldGuard()
    private val priceKey = NamespacedKey(plugin, "price")
    private val accountKey = NamespacedKey(plugin, "account")
    private val loreKey = NamespacedKey(plugin, "lore")
    fun make(sign: Sign, player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        val downBlock = sign.block.location.clone().add(0.0, -1.0, 0.0).block
        if (downBlock.type != Material.BARREL) { return }
        val itemFrame = sign.world.spawn(sign.location, org.bukkit.entity.ItemFrame::class.java)
        itemFrame.customName = "@Fshop"
        val price = sign.getLine(1)
        additionalNbt(itemFrame, priceKey, price)
        additionalNbt(itemFrame, accountKey, aoringoPlayer.playerAccount.getAccountID())
    }
    fun acquisitionPrice(shop: ItemFrame): Int? {
        val price = acquisitionNbt(shop, priceKey)
        return try {
            price?.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }
    fun acquisitionAccount(shop: ItemFrame): String? {
        return acquisitionNbt(shop, accountKey)
    }
    fun acquisitionLore(shop: ItemFrame): String {
        return acquisitionNbt(shop, loreKey) ?: "未設定"
    }
    private fun additionalNbt(shop: ItemFrame, key: NamespacedKey, value: String) {
        val persistentDataContainer = persistentDataContainer(shop)
        persistentDataContainer.set(key, PersistentDataType.STRING, value)
    }
    private fun acquisitionNbt(shop: ItemFrame, key: NamespacedKey): String? {
        val persistentDataContainer = persistentDataContainer(shop)
        return persistentDataContainer.get(key, PersistentDataType.STRING)
    }
    private fun persistentDataContainer(shop: ItemFrame): PersistentDataContainer {
        return shop.persistentDataContainer
    }
    fun isOwner(player: Player, entity: Entity?): Boolean {
        entity ?: return false
        return worldguard.getOwnerOfRegion(entity.location)?.contains(player.uniqueId) == true || WorldGuard().getMemberOfRegion(entity.location)?.contains(player.uniqueId) == true
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
        val price = acquisitionPrice(shop) ?: return
        if (shop.item != item) {
            aoringoPlayer.sendErrorMessage("売り物が更新されました")
            return
        }
        if (moneyUseCase.getMoney(aoringoPlayer.playerAccount) < price) {
            aoringoPlayer.sendErrorMessage("お金が足りません")
            return
        }
        sender.inventory.addItem(item)
        val account = ShopCoordinationAccount(shop, plugin)
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
