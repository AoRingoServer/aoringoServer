package com.github.Ringoame196.Blocks

import com.github.Ringoame196.ActivityBlocks.ActivityBlock
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Foods.FoodManager
import com.github.Ringoame196.Items.ItemManager
import org.apache.commons.lang.ObjectUtils.Null
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Beehive
import org.bukkit.event.player.PlayerInteractEvent

class BeeNest(private val block:Block?):ActivityBlock {
    override fun clickBlock(e: PlayerInteractEvent, aoringoPlayer: AoringoPlayer) {
        e.isCancelled = true
        if (block == null) { return }
        val beeNestData = block.blockData as Beehive
        val player = aoringoPlayer.player
        val playerItem = player.inventory.itemInMainHand
        if (isMax(beeNestData)) { return }
        if (playerItem.type != Material.GLASS_BOTTLE) { return }
        emptyBeeNest(beeNestData)
        val expiryDate = 14
        val honeyBottle = ItemManager().make(Material.HONEY_BOTTLE, "${ChatColor.GOLD}ハチミツ", FoodManager().makeExpirationDate(expiryDate))
        player.inventory.addItem(honeyBottle)
        ItemManager().reduceMainItem(player)
    }
    fun isMax(beeNestData:Beehive): Boolean {
        return beeNestData.honeyLevel == beeNestData.maximumHoneyLevel
    }
    fun emptyBeeNest(beeNestData:Beehive) {
        if (block == null) { return }
        beeNestData.honeyLevel = 0
        block.blockData = beeNestData
    }
}
