package com.github.Ringoame196.Cook

import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Data.CookData
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player

class Clothing {
    private val food = Food()
    private val cook = Cook()
    private val cookData = CookData()
    private val itemClass = Item()
    fun dressing(player: Player, entity: ItemFrame) {
        val item = player.inventory.itemInMainHand
        if (food.isExpirationDateHasExpired(player, entity.item)) { return }
        val dressingItem = cookData.dressing(item) ?: return
        if (!cook.isCookLevel(dressingItem.itemMeta?.displayName?:return, player)) {
            return
        }
        itemClass.reduceMainItem(player)
        player.inventory.addItem(dressingItem)
        val particleLocation = entity.location.add(0.0, 1.0, 0.0)
        entity.world.spawnParticle(Particle.EXPLOSION_HUGE, particleLocation, 1)
        player.world.playSound(player.location, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
    }
}
