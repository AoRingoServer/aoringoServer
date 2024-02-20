package com.github.Ringoame196.Commands

import com.github.Ringoame196.Admin
import com.github.Ringoame196.ResourcePack
import com.github.Ringoame196.Shop.ShopLand
import com.github.Ringoame196.Worlds.HardcoreWorld
import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.plugin.Plugin

class AoringoopCommand(val plugin: Plugin) : CommandExecutor, TabExecutor {
    private var player: Player? = null
    private val subCommand = mapOf(
        "updateResourcePack" to { ResourcePack(plugin).update() },
        "tpTestWorld" to { player?.teleport(Bukkit.getWorld("testworld")?.spawnLocation ?: player?.location!!) },
        "resetShopLand" to { ShopLand().resetShopLand(plugin) },
        "fullRecovery" to {
            player?.health = 20.0
            player?.foodLevel = 20
            player?.saturation = 20.0f
            player?.sendMessage("${ChatColor.AQUA}全回復しました")
        },
        "loadYML" to {
            Yml().callData(plugin)
            player?.sendMessage("${ChatColor.YELLOW}[青リンゴサーバー] ymlファイルを再読込しました")
        },
        "resetHardcore" to { HardcoreWorld().resetHardCoreWorld(plugin) },
        "bookAuthorChange" to { bookAuthorChange(player) },
        "updateYml" to {  }
    )
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        player = sender as Player
        subCommand[args[0]]?.invoke()
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return subCommand.keys.toMutableList()
    }
    private fun bookAuthorChange(player: Player?) {
        val book = player?.inventory?.itemInMainHand ?: return
        if (book.type != Material.WRITTEN_BOOK && book.type != Material.WRITABLE_BOOK) {
            return
        }
        val meta = book.itemMeta as BookMeta
        meta.author = Admin().writeBookAuthor
        book.setItemMeta(meta)
        player.sendMessage("${ChatColor.GREEN}著者を運営に変更しました")
    }
    private fun updateYml(){

    }
}
