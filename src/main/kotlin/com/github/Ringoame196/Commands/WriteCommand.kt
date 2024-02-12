package com.github.Ringoame196.Commands

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.ImportantDocuments.ContractBook
import com.github.Ringoame196.Items.ImportantDocuments.ImportantDocument
import com.github.Ringoame196.Items.ImportantDocuments.RemittanceBook
import com.github.Ringoame196.Items.ImportantDocuments.WaitingListBook
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class WriteCommand : CommandExecutor, TabExecutor {
    private val processingPerItem = mapOf<String, ImportantDocument> (
        "${ChatColor.YELLOW}契約書[未記入]" to ContractBook(),
        "${ChatColor.YELLOW}契約書[契約待ち]" to WaitingListBook(),
        "${ChatColor.YELLOW}送金申込書[未記入]" to RemittanceBook()
    )
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as Player
        val item = player.inventory.itemInMainHand
        if (item.amount != 1) {
            AoringoPlayer(player).sendErrorMessage("アイテムを一つだけ持ってください")
            return true
        }
        processingPerItem[item.itemMeta?.displayName ?: ""]?.write(player, args)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        val player = sender as Player
        val item = player.inventory.itemInMainHand
        val subCommand = processingPerItem[item.itemMeta?.displayName ?: ""]?.subCommand(args.size) ?: mutableListOf("[このアイテムは非対応アイテム]")
        return subCommand ?: mutableListOf()
    }
}
