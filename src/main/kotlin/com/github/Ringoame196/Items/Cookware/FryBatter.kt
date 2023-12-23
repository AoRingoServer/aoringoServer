package com.github.Ringoame196.Items.Cookware

import com.github.Ringoame196.CookManager
import com.github.Ringoame196.Items.ItemManager
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player

class FryBatter(private val cookManager: CookManager = CookManager()) {
    private val itemManagerClass = ItemManager()
    fun dressing(player: Player, entity: ItemFrame) {
        val item = player.inventory.itemInMainHand
        if (cookManager.foodManager.isExpirationDateHasExpired(player, entity.item)) { return }
        val dressingItem = cookManager.cookData.dressing(item) ?: return
        if (!cookManager.isCookLevel(dressingItem.itemMeta?.displayName?:return, player)) {
            return
        }
        itemManagerClass.reduceMainItem(player)
        player.inventory.addItem(dressingItem)
        val particleLocation = entity.location.add(0.0, 1.0, 0.0)
        entity.world.spawnParticle(Particle.EXPLOSION_HUGE, particleLocation, 1)
        player.world.playSound(player.location, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
    }
}
