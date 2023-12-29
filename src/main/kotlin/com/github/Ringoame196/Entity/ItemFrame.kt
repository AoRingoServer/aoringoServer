package com.github.Ringoame196.Entity

import org.bukkit.Location
import org.bukkit.entity.ItemFrame

class ItemFrame {
    fun summonItemFrame(location: Location): ItemFrame {
        val world = location.world
        return world?.spawn(location, ItemFrame::class.java)
            ?: throw RuntimeException("額縁が正常に生成されなかった")
    }
    fun changeTransparency(itemFrame: ItemFrame) {
        itemFrame.isVisible = false
    }
}
