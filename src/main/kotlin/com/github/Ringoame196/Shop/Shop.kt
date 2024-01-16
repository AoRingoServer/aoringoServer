package com.github.Ringoame196.Shop

import com.github.Ringoame196.Entity.AoringoPlayer
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.plugin.Plugin

class Shop(private val plugin: Plugin) {
    val shopManager = FshopManager()
    fun createShop(aoringoPlayer: AoringoPlayer, sign: Sign, price: String) {
        val remittanceAccount = aoringoPlayer.playerAccount.getAccountID()
        val shopData = mapOf(
            "remittanceAccount" to remittanceAccount,
            "price" to price
        )
        sign.location.block.type = Material.AIR
        val itemFrame = sign.world.spawn(sign.location, org.bukkit.entity.ItemFrame::class.java)
        itemFrame.customName = "shop"
        shopManager.saveShopData(shopData, itemFrame.uniqueId.toString(), plugin)
    }
}
