package com.github.Ringoame196.Accounts

class JointAccount(private val accountID: String) : Account {
    override fun getAccountID(): String {
        return accountID
    }

    override fun getRegisteredPerson(): String {
        return accountID
    }
}
