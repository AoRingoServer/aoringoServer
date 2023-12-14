package com.github.Ringoame196.Job

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class Mission {
    fun set(player: Player, barrel: Barrel) {
        val r = Random.nextInt(1, barrel.inventory.size - 1)
        val item = barrel.inventory.getItem(r - 1)
        val name = if (item?.itemMeta?.displayName != "") { item?.itemMeta?.displayName } else { item.type }
        Scoreboard().set("mission", player.name, r)
        player.sendMessage("${ChatColor.GOLD}[クエスト] ${name}を${item?.amount}個集める")
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
    fun check(player: Player, barrel: Barrel) {
        val item = barrel.inventory.getItem(Scoreboard().getValue("mission", player.name) - 1) ?: return
        val playerItem = player.inventory.itemInMainHand
        val c = item.clone().amount - 1
        if (comparison(item, playerItem)) {
            playerItem.amount = playerItem.amount - c
            Scoreboard().add("missionCount", player.name, 1)
            Scoreboard().set("mission", player.name, 0)
            player.sendMessage("${ChatColor.AQUA}クエストクリア！！！")
            playerItem.amount = playerItem.amount - 1
            player.inventory.setItemInMainHand(playerItem)
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f)
            if (Scoreboard().getValue("missionCount", player.name) != 10) { return }
            reward(player)
        } else {
            val gui = Bukkit.createInventory(null, 9, "${ChatColor.GOLD}クエスト")
            gui.setItem(0, Item().make(Material.PLAYER_HEAD, "${ChatColor.YELLOW}${Scoreboard().getValue("missionCount",player.name)}連続"))
            gui.setItem(4, item)
            gui.setItem(6, Item().make(Material.REDSTONE, "${ChatColor.RED}辞退"))
            player.openInventory(gui)
        }
    }
    fun reset(player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        aoringoPlayer.sendErrorMessage("クエストを辞退しました")
        Scoreboard().set("mission", player.name, 0)
        Scoreboard().set("missionCount", player.name, 0)
        player.closeInventory()
        if (player.foodLevel >= 4) {
            player.foodLevel = 3
        }
    }
    private fun reward(player: Player) {
        Scoreboard().set("missionCount", player.name, 0)
        player.sendMessage("${ChatColor.YELLOW}[クエスト]10連クリア！！")
        when (JobManager().get(player)) {
            "${ChatColor.YELLOW}料理人" -> player.inventory.addItem(Item().make(Material.GOLD_INGOT, "${ChatColor.YELLOW}クエストコイン", customModelData = 1))
            "${ChatColor.GRAY}鍛冶屋" -> player.inventory.addItem(Item().make(Material.GOLD_INGOT, "${ChatColor.YELLOW}クエストコイン", customModelData = 1))
            "${ChatColor.GOLD}ハンター" -> {
                when (Random.nextInt(1, 101)) {
                    in 1..90 -> player.inventory.addItem(Item().make(Material.GOLD_INGOT, "${ChatColor.YELLOW}クエストコイン", customModelData = 1))
                    in 91..95 -> player.inventory.addItem(Item().make(Material.MELON_SLICE, "${ChatColor.YELLOW}力のプロテイン", customModelData = 91))
                    in 96..100 -> player.inventory.addItem(Item().make(Material.MELON_SLICE, "${ChatColor.RED}ハートのハーブ", customModelData = 92))
                }
            }
        }
    }
    private fun comparison(item1: ItemStack, item2: ItemStack): Boolean {
        val c1 = item1.clone()
        val c2 = item2.clone()
        c1.amount = 1
        c2.amount = 1
        return c1 == c2
    }
}
