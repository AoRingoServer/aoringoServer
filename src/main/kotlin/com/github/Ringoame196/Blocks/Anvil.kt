package com.github.Ringoame196

import com.github.Ringoame196.GUIs.GUI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class Anvil:GUI {
    override fun close(gui: InventoryView,player: Player) {
        Anvil().returnItemFromPlayer(gui, player)
    }
    fun makeGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}カスタム金床")
        fillGUI(gui)
        val itemSlotNumber = 2
        val materialSlotNumber = 4
        val compositeButtonSlotNumber = 7
        InstallationAir(gui, itemSlotNumber)
        InstallationAir(gui, materialSlotNumber)
        InstallationCompositeButton(gui, compositeButtonSlotNumber)
        return gui
    }
    private fun fillGUI(gui: Inventory) {
        for (i in 0 until gui.size) {
            gui.setItem(i, com.github.Ringoame196.Items.ItemManager().make(Material.RED_STAINED_GLASS_PANE, " "))
        }
    }
    private fun InstallationAir(gui: Inventory, slot: Int) {
        gui.setItem(slot, com.github.Ringoame196.Items.ItemManager().make(Material.AIR, " "))
    }
    private fun InstallationCompositeButton(gui: Inventory, slot: Int) {
        gui.setItem(slot, com.github.Ringoame196.Items.ItemManager().make(Material.ANVIL, "${ChatColor.YELLOW}合成"))
    }
    fun click(player: Player, item: ItemStack, e: InventoryClickEvent) {
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (item.type) {
            Material.RED_STAINED_GLASS_PANE -> e.isCancelled = true
            Material.ANVIL -> {
                e.isCancelled = true
                synthesis(player, player.openInventory.topInventory)
            }
            else -> return
        }
    }
    fun returnItemFromPlayer(gui: InventoryView, player: Player) {
        val item = gui.getItem(2)
        val material = gui.getItem(4)
        if (item != null) {
            player.inventory.addItem(item)
        }
        if (material != null) {
            player.inventory.addItem(material)
        }
    }
    private fun synthesis(player: Player, gui: Inventory) {
        val playerClass = com.github.Ringoame196.Entity.AoringoPlayer(player)
        val item = gui.getItem(2) ?: return
        val material = gui.getItem(4) ?: return
        if (!isCanEnchantItem(item.type) || (!isCanEnchantItem(material.type) && material.itemMeta?.displayName != "${ChatColor.YELLOW}修理キット")) {
            return
        }
        if (material.itemMeta?.displayName != "" && material.itemMeta?.displayName != "${ChatColor.YELLOW}修理キット") {
            playerClass.sendErrorMessage("右側に名前付きのアイテムを設置することはできません")
            return
        }
        if ((material.itemMeta?.lore?.size ?: 0) >= 1) {
            for (lore in material.itemMeta!!.lore!!) {
                if (!lore.contains("所有者:")) {
                    continue
                }
                playerClass.sendErrorMessage("保護のアイテムを設置することはできません")
                return
            }
        }

        var completedItem = item.clone()
        if (item.type == material.type) {
            completedItem = enchantItem(material, completedItem)
            completedItem = durability(material, completedItem)
        } else if (material.type == Material.ENCHANTED_BOOK) {
            completedItem = enchantBook(completedItem, material.itemMeta as EnchantmentStorageMeta)
        } else if (material.itemMeta!!.displayName == "${ChatColor.YELLOW}修理キット") {
            completedItem.durability = (completedItem.durability - material.amount).toShort()
        }
        if (isOverEnchant(completedItem)) {
            playerClass.sendErrorMessage("オーバーエンチャント")
            return
        }
        givePlayerItem(player, completedItem, gui)
    }
    private fun isOverEnchant(item: ItemStack): Boolean {
        var allLevel = 0
        val overLevel = 10
        for ((enchant, level) in item.itemMeta?.enchants ?: return false) {
            allLevel += level
        }
        return allLevel > overLevel
    }
    private fun givePlayerItem(player: Player, completedItem: ItemStack, gui: Inventory) {
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        player.inventory.addItem(completedItem)
        val itemSlotNumber = 2
        val materialSlotNumber = 4
        InstallationAir(gui, itemSlotNumber)
        InstallationAir(gui, materialSlotNumber)
    }
    private fun enchantItem(beforeItem: ItemStack, afterItem: ItemStack): ItemStack {
        for ((enchant, level) in beforeItem.itemMeta?.enchants ?: return afterItem) {
            if (afterItem.getEnchantmentLevel(enchant) == level) {
                afterItem.addUnsafeEnchantment(enchant, level + 1)
                continue
            }
            afterItem.addUnsafeEnchantment(enchant, level)
        }
        return afterItem
    }
    private fun enchantBook(afterItem: ItemStack, meta: EnchantmentStorageMeta): ItemStack {
        val maxLevel = 5
        for ((enchant, level) in meta.storedEnchants) {
            if (afterItem.getEnchantmentLevel(enchant) > level) { continue }
            if (afterItem.getEnchantmentLevel(enchant) == level) {
                if (level >= maxLevel) {
                    afterItem.addUnsafeEnchantment(enchant, level)
                } else {
                    afterItem.addUnsafeEnchantment(enchant, level + 1)
                }
                continue
            }
            afterItem.addUnsafeEnchantment(enchant, level)
        }
        return afterItem
    }
    private fun durability(beforeItem: ItemStack, afterItem: ItemStack): ItemStack {
        val maxdurability = beforeItem.type.maxDurability
        val durability = (afterItem.durability - (maxdurability - beforeItem.durability)).toShort()
        afterItem.durability = if (durability >= 0) { durability } else { 0 }
        return afterItem
    }
    private fun isCanEnchantItem(material: Material): Boolean {
        val allowedItems = mutableListOf(
            "_SWORD",
            "_AXE",
            "_PICKAXE",
            "_SHOVEL",
            "_HOE",
            "SHIELD",
            "ENCHANTED_BOOK",
            "_HELMET",
            "_CHESTPLATE",
            "_LEGGINGS",
            "_BOOTS",
        )
        for (id in allowedItems) {
            if (material.toString().contains(id)) { return true }
        }
        return false
    }
}
