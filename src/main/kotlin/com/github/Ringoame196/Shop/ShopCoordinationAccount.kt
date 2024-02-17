package com.github.Ringoame196.Shop

import com.github.Ringoame196.Accounts.Account
import org.bukkit.entity.ItemFrame
import org.bukkit.plugin.Plugin

class ShopCoordinationAccount(private val shop: ItemFrame, private val plugin: Plugin) : Account {
    private val fshop = Fshop(plugin)
    override fun getAccountID(): String {
        return fshop.acquisitionAccount(shop) ?: "取得不能の口座"
    }

    override fun getRegisteredPerson(): String {
        return fshop.acquisitionAccount(shop) ?: "取得不能の口座"
    }
}
