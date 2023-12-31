package com.github.Ringoame196

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType

class ItemData {
    data class FoodType(val entityType: EntityType, val material: Material, val displayName: String, val customModelData: Int)
    fun getEntityDropItem(entityType: EntityType): FoodType? {
        return when (entityType) {
            EntityType.COW -> FoodType(entityType, Material.BEEF, "${ChatColor.RED}牛肉", 78)
            EntityType.SHEEP -> FoodType(entityType, Material.MUTTON, "${ChatColor.RED}羊肉", 79)
            EntityType.PIG -> FoodType(entityType, Material.PORKCHOP, "${ChatColor.RED}豚肉", 81)
            EntityType.CHICKEN -> FoodType(entityType, Material.CHICKEN, "${ChatColor.RED}鶏肉", 80)
            else -> null
        }
    }
}
