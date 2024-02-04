package com.github.Ringoame196

class JointAccount(private val accountID: String) : Account {
    override fun getAccountID(): String {
        return accountID
    }

    override fun getRegisteredPerson(): String {
        return accountID
    }
}
