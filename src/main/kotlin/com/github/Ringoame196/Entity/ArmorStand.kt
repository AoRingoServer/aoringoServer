package com.github.Ringoame196.Entity

import org.bukkit.Location
import org.bukkit.entity.ArmorStand

class ArmorStand {
    fun summonMarker(location: Location, name: String, tag: String = ""): ArmorStand {
        val world = location.world
        val armorStand: ArmorStand = world!!.spawn(location, org.bukkit.entity.ArmorStand::class.java)

        // アーマースタンドの設定
        armorStand.isVisible = false // 可視化するかどうか
        armorStand.isSmall = true // サイズを小さくするかどうか
        armorStand.isInvulnerable = true
        armorStand.customName = name
        armorStand.isCustomNameVisible = true
        armorStand.setGravity(false)
        armorStand.isMarker = true
        armorStand.addScoreboardTag(tag)
        return armorStand
    }
}
