package com.github.Ringoame196.Items.Cookware

import com.github.Ringoame196.CookManager
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.entity.Player
import kotlin.random.Random

class Bowl(private val cookManager: CookManager = CookManager()) {
    private val foodManager = cookManager.foodManager
    private val cookData = cookManager.cookData
    fun mix(player: Player, barrel: Barrel) {
        if (Random.nextInt(0, 8) != 0) { return }
        val recipe = mutableListOf<String>()
        var expiration = false
        for (i in 0 until barrel.inventory.size) {
            val item = barrel.inventory.getItem(i) ?: continue
            recipe.add(item.itemMeta?.displayName ?: continue)
            if (foodManager.isExpirationDateHasExpired(player, item)) {
                expiration = true
            }
        }
        val finishFood = if (expiration) { cookData.fermentationMix(recipe) } else { cookData.mix(recipe) } ?: return
        val finishedProduct = cookManager.completionItem(finishFood, player)
        barrel.world.dropItem(barrel.location.clone().add(0.5, 1.5, 0.5), finishedProduct)
        for (i in 0 until barrel.inventory.size) {
            val item = barrel.inventory.getItem(i) ?: continue
            item.amount = barrel.inventory.getItem(i)!!.amount - 1
        }
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
