package com.github.Ringoame196.Items.ImportantDocuments

import org.bukkit.entity.Player

interface ImportantDocument {
    fun subCommand(subCommandCount: Int): MutableList<String>
    fun write(player: Player, subCommand: Array<out String>)
}
