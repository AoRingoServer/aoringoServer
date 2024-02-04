package com.github.Ringoame196.Shop

import com.github.Ringoame196.Account
import org.bukkit.entity.ItemFrame

class ShopCoordinationAccount(private val shop: ItemFrame) : Account {
    override fun getAccountID(): String {
        return Fshop().getAccountID(shop)
    }

    override fun getRegisteredPerson(): String {
        return Fshop().getAccountID(shop)
    }
}
