package com.github.Ringoame196.Blocks

import org.bukkit.block.Block
import org.bukkit.block.data.type.Beehive

class BeeNest(private val beeNestData: Beehive) {
    fun isMax(): Boolean {
        return beeNestData.honeyLevel == beeNestData.maximumHoneyLevel
    }
    fun emptyBeeNest(beeNest: Block) {
        beeNestData.honeyLevel = 0
        beeNest.blockData = beeNestData
    }
}
