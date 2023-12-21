package com.github.Ringoame196.Shop

import com.github.Ringoame196.Account

class ShopCoordinationAccount(private val fshop: Fshop) : Account {
    override fun getAccountID(): String {
        return fshop.getAccountID()
    }
}
