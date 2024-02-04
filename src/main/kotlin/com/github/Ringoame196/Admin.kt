package com.github.Ringoame196

class Admin : Account {
    override fun getAccountID(): String {
        return "admin"
    }

    override fun getRegisteredPerson(): String {
        return "admin"
    }
}
