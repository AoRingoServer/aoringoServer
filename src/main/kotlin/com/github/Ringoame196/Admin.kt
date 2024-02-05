package com.github.Ringoame196

import com.github.Ringoame196.Accounts.Account

class Admin : Account {
    override fun getAccountID(): String {
        return "admin"
    }

    override fun getRegisteredPerson(): String {
        return "admin"
    }
}
