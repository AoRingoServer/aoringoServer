package com.github.Ringoame196

interface MessageSender {
    fun createBossbar()
    fun sendMessage(message: String)
    fun sendErrorMessage(message: String)
    fun sendActionBar(title: String)
}
