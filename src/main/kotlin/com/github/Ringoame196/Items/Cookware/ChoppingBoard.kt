package com.github.Ringoame196.Items.Cookware

import com.github.Ringoame196.CookManager
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class ChoppingBoard(private val cookManager: CookManager = CookManager()) {
    fun cutFoods(item: ItemStack, player: Player, entity: ItemFrame) {
        val playerItem = player.inventory.itemInMainHand
        if (playerItem.itemMeta?.customModelData != 1) { return }
        if (!checkCut(playerItem)) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
            return
        }
        if (cookManager.foodManager.isExpirationDateHasExpired(player, entity.item)) {
            return
        }
        val cutItem = cookManager.cookData.cut(item) ?: return
        val ingredientName = cutItem.itemMeta?.displayName
        if (!cookManager.isCookLevel(ingredientName?:return, player)) {
            return
        }
        val usedKnife = reduceDurability(playerItem)
        player.inventory.addItem(cutItem)
        entity.setItem(ItemStack(Material.AIR))
        player.world.playSound(player.location, Sound.ENTITY_SHEEP_SHEAR, 1f, 1f)
        player.inventory.setItemInMainHand(usedKnife)
        if (usedKnife.durability >= playerItem.type.maxDurability) { breakKnife(player) }
    }
    private fun reduceDurability(knife: ItemStack): ItemStack {
        val durability = knife.durability
        val decreasingNumber = 4
        val newDurability = (durability + decreasingNumber).toShort()
        knife.durability = newDurability
        return knife
    }
    private fun breakKnife(player: Player) {
        player.inventory.setItemInMainHand(ItemStack(Material.AIR))
        player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
    }
    private fun checkCut(knife: ItemStack): Boolean {
        val lore = knife.itemMeta?.lore?.get(0) ?: return false
        val sharpness = lore.replace("切れ味:", "").toInt()
        if (sharpness > 10) { return true }
        return Random.nextInt(0, (10 - sharpness)) <= 0
    }
    fun summonChoppingBoard(block: Block) {
        val itemFrameClass = com.github.Ringoame196.Entity.ItemFrame()
        val summonLocation = block.location.clone().add(0.0, 1.0, 0.0)
        val itemFrame = itemFrameClass.summonItemFrame(summonLocation)
        itemFrame.customName = "まな板"
    }
}
