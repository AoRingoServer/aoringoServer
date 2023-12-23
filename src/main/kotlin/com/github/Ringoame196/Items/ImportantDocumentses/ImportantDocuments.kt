package com.github.Ringoame196.Items.ImportantDocumentses

import org.bukkit.entity.Player

interface ImportantDocuments {
    val status: MutableList<String>
    fun displayCommand(player: Player)
}
