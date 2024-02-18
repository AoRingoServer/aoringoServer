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
    val priceKey = NamespacedKey(plugin, "price")
    val accountKey = NamespacedKey(plugin, "account")
    val loreKey = NamespacedKey(plugin, "lore")
    fun make(sign: Sign, player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        val downBlock = sign.block.location.clone().add(0.0, -1.0, 0.0).block
        if (downBlock.type != Material.BARREL) { return }
        val itemFrame = sign.world.spawn(sign.location, org.bukkit.entity.ItemFrame::class.java)
        itemFrame.customName = "@Fshop"
        val price = sign.getLine(1)
        if (!checkInt(price)) {
            aoringoPlayer.sendErrorMessage("値段が不正です")
            return
        }
        additionalNbt(itemFrame, priceKey, price)
        additionalNbt(itemFrame, accountKey, aoringoPlayer.playerAccount.getAccountID())
    }
    fun checkInt(value: String): Boolean {
        return try {
            value.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
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
    fun additionalNbt(shop: ItemFrame, key: NamespacedKey, value: String) {
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
        val price = acquisitionPrice(shop) ?: 0
        val priceNotation = MoneyUseCase().formalCurrency(price)
        val itemManager = ItemManager()
        val lore = acquisitionLore(shop)
        gui.setItem(0, itemManager.make(Material.COMPASS, "ショップ", shop.uniqueId.toString()))
        gui.setItem(2, loreItem(lore))
        gui.setItem(3, goods)
        gui.setItem(4, itemManager.make(Material.EMERALD_BLOCK, "${ChatColor.GREEN}購入", "${priceNotation}円"))
        return gui
    }
    private fun loreItem(lore: String): ItemStack {
        val sign = ItemStack(Material.OAK_SIGN)
        val meta = sign.itemMeta
        meta?.setDisplayName("${ChatColor.GREEN}アイテム説明")
        meta?.lore = lore.split(" ")
        sign.setItemMeta(meta)
        return sign
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
    fun sendShopMessage(player: Player, message: String) {
        player.sendMessage("${ChatColor.YELLOW}[ショップ]$message")
    }
}
