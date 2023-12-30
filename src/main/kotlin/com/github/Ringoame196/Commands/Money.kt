package com.github.Ringoame196.Commands

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.PlayerManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Money : CommandExecutor, TabCompleter {
    private val playerManager = PlayerManager()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return false }
        val aoringoPlayer = AoringoPlayer(sender)
        val size = args.size
        if (args.isEmpty()) {
            val possessionMoney = aoringoPlayer.moneyUseCase.getMoney(aoringoPlayer.playerAccount)
            sender.sendMessage("${ChatColor.GREEN}[所持金] $possessionMoney 円")
        }
        if (size == 1) { return false }
        val targetPlayer = playerManager.acquisitionPlayer(args[2])
        if (size == 2) { return false }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf("pay", "show", "add", "set")
            2 -> playerManager.acquisitionPlayerNameList()
            3 -> mutableListOf("[値段]")
            else -> mutableListOf("")
        }
    }
}
