package com.github.Ringoame196.Entity

import com.github.Ringoame196.Anvil
import com.github.Ringoame196.Blocks.Block
import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Job
import com.github.Ringoame196.ResourcePack
import com.github.Ringoame196.Scoreboard
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class Player(val player: Player) {
    data class PlayerData(
        var titleMoneyBossbar: BossBar? = null,
        var speedMeasurement: Boolean = false
    )
    fun setPlayer(plugin: Plugin) {
        val scoreboardClass = Scoreboard()
        setJobName()
        setProtectionPermission(plugin)
        setTab()
        player.maxHealth = 20.0 + scoreboardClass.getValue("status_HP", player.uniqueId.toString()).toDouble()
        ResourcePack(plugin ?: return).adaptation(player)
        if (player.world.name == "Survival") {
            player.teleport(Bukkit.getWorld("world")?.spawnLocation ?: return)
        }
        scoreboardClass.set("blockCount", player.name, 0)
        permission("enderchest.size.${scoreboardClass.getValue("haveEnderChest", player.uniqueId.toString()) + 1}", true,plugin)
        Money().createBossbar(player)
        if (player.isOp) {
            player.setDisplayName("${ChatColor.YELLOW}[運営]" + player.displayName)
            player.setPlayerListName("${ChatColor.YELLOW}[運営]" + player.playerListName)
        }
    }
    fun setJobName() {
        val jobID = Scoreboard().getValue("job", player.uniqueId.toString())
        val jobColor = mutableListOf("", "${ChatColor.DARK_PURPLE}", "${ChatColor.DARK_RED}", "${ChatColor.GRAY}")
        val jobPrefix = jobColor[jobID]
        player.setDisplayName("$jobPrefix${player.displayName}@${Job().get(player)}")
        player.setPlayerListName("$jobPrefix${player.playerListName}")
    }
    private fun levelupMessage(player: Player, message: String) {
        player.sendMessage(message)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
    fun sendErrorMessage(message: String) {
        player.sendMessage("${ChatColor.RED}$message")
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
    }
    fun addPower() {
        Scoreboard().add("status_Power", player.uniqueId.toString(), 1)
        levelupMessage(player, "${ChatColor.YELLOW}パワーアップ！！")
    }
    fun addMaxHP() {
        Scoreboard().add("status_HP", player.uniqueId.toString(), 1)
        levelupMessage(player, "${ChatColor.RED}最大HPアップ！！")
        player.maxHealth = 20.0 + Scoreboard().getValue("status_HP", player.uniqueId.toString())
    }
    fun sendActionBar(message: String) {
        val actionBarMessage = ChatColor.translateAlternateColorCodes('&', message)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(actionBarMessage))
    }
    fun getPlayersInRadius(center: Location, radius: Double): List<Player>? {
        val playersInRadius = mutableListOf<Player>()

        for (player in center.world?.players ?: return null) {
            val playerLocation = player.location
            val distance = center.distance(playerLocation)

            if (distance <= radius) {
                // 半径内にいるプレイヤーをリストに追加
                playersInRadius.add(player)
            }
        }

        return playersInRadius
    }
    fun permission(permission: String, allow: Boolean,plugin: Plugin) {
        val permissions = player.addAttachment(plugin) // "plugin" はプラグインのインスタンスを指します
        permissions.setPermission(permission, allow)
        player.recalculatePermissions()
    }
    fun setTab() {
        player.playerListHeader = "${ChatColor.AQUA}青りんごサーバー"
        player.playerListFooter = "${ChatColor.YELLOW}" + when (player.world.name) {
            "world" -> "ロビーワールド"
            "Survival" -> "資源ワールド"
            "Nether" -> "ネザー"
            "shop" -> "ショップ"
            "event" -> "イベントワールド"
            "Home" -> "建築ワールド"
            else -> "${ChatColor.RED}未設定"
        }
    }
    fun setProtectionPermission(plugin: Plugin) {
        permission(
            "blocklocker.protect",
            when (player.world.name) {
                "Survival" -> true
                "Home" -> true
                else -> false
            }, plugin
        )
    }
    fun calculateDistance(pos1: Location, pos2: Location): Int {
        val deltaX = Math.abs(pos1.x.toInt() - pos2.x.toInt())
        val deltaZ = Math.abs(pos1.z.toInt() - pos2.z.toInt())

        return maxOf(deltaX, deltaZ)
    }
    fun fastJoin() {
        player.inventory.addItem(Item().make(material = Material.ENCHANTED_BOOK, name = "${ChatColor.YELLOW}スマートフォン", customModelData = 1))
        player.inventory.addItem(Item().make(material = Material.NETHER_STAR, name = "職業スター"))
        player.scoreboardTags.add("member")
        Scoreboard().set("money", player.uniqueId.toString(), 30000)
    }
    fun putItemInPost(post: Barrel) {
        val playerItem = player.inventory.itemInMainHand.clone()
        playerItem.amount = 1
        post.inventory.addItem(playerItem)
        player.inventory.removeItem(playerItem)
        player.sendMessage("${ChatColor.GOLD}[ポスト]アイテムをポストに入れました")
    }
    fun activationTeleporter(worldName: String) {
        player.teleport(
            Bukkit.getWorld(
                if (player.world.name == "world") { worldName } else { "world" }
            )?.spawnLocation ?: return
        )
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
    fun useEnchantingTable(){
        if (player.foodLevel < 10) {
            sendErrorMessage("満腹度が足りません")
            return
        }
        player.openInventory(Block().enchantGUI())
    }
    fun useAnvil(){
        if (Job().get(player) != "${ChatColor.GRAY}鍛冶屋") {
            sendErrorMessage("金床は鍛冶屋以外使用できません")
            return
        }
        player.openInventory(Anvil().makeGUI())
    }
}
