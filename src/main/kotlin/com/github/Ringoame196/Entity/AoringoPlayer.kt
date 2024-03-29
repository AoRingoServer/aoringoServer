package com.github.Ringoame196.Entity

import com.github.Ringoame196.Accounts.PlayerAccount
import com.github.Ringoame196.EnderChest
import com.github.Ringoame196.ExternalPlugins.LuckPerms
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Foods.FoodManager
import com.github.Ringoame196.GUIs.LandProtectiveGUI
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Job.JobManager
import com.github.Ringoame196.MoneyUseCase
import com.github.Ringoame196.PluginData
import com.github.Ringoame196.ResourcePack
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import com.github.Ringoame196.Worlds.WorldManager
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.block.Sign
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File

class AoringoPlayer(val player: Player) {
    val playerAccount = PlayerAccount(player)
    val moneyUseCase = MoneyUseCase()
    val jobManager = JobManager()
    val luckPerms = LuckPerms(this)
    data class PlayerData(
        var chatSettingItem: String? = null,
        var lastTouchShop: ItemFrame? = null,
        var entry: String? = null
    )
    fun setPlayer(plugin: Plugin) {
        val scoreboardClass = Scoreboard()
        setJobName()
        setProtectionPermission(plugin)
        setTab(plugin)
        player.maxHealth = 20.0 + scoreboardClass.getValue("status_HP", player.uniqueId.toString()).toDouble()
        ResourcePack(plugin).adaptation(player)
        if (player.world.name == "Survival") {
            teleporterWorld("world")
        }
        changePermission("enderchest.size.${scoreboardClass.getValue("haveEnderChest", player.uniqueId.toString()) + 1}", true, plugin)
        if (player.isOp) {
            setOperatorName()
        }
    }
    private fun setOperatorName() {
        val prefix = "${ChatColor.YELLOW}[運営]"
        player.setDisplayName("$prefix${player.displayName}")
        player.setPlayerListName("$prefix${player.playerListName}")
    }
    private fun setJobName() {
        val jobColors = mapOf<String, String>(
            "${ChatColor.YELLOW}料理人" to "${ChatColor.DARK_PURPLE}",
            "${ChatColor.GOLD}ハンター" to "${ChatColor.DARK_RED}",
            "${ChatColor.GRAY}鍛冶屋" to "${ChatColor.GRAY}"
        )
        val jobName = JobManager().get(player)
        val jobColor = jobColors[jobName] ?: ""
        player.setDisplayName("$jobColor${player.displayName}@$jobName")
        player.setPlayerListName("$jobColor${player.playerListName}")
    }
    private fun levelupMessage(player: Player, message: String) {
        player.sendMessage(message)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
    fun acquisitionJob(): String {
        return jobManager.get(player)
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
    fun sendActionBar(title: String) {
        val actionBarMessage = ChatColor.translateAlternateColorCodes('&', title)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(actionBarMessage))
    }
    fun changePermission(permission: String, allow: Boolean, plugin: Plugin) {
        val permissions = player.addAttachment(plugin)
        permissions.setPermission(permission, allow)
        player.recalculatePermissions()
    }
    fun setTab(plugin: Plugin) {
        player.playerListHeader = "${ChatColor.AQUA}青りんごサーバー"
        val worldManager = WorldManager(plugin)
        val worldName = player.location.world?.name ?: "world"
        player.playerListFooter = "${ChatColor.YELLOW}" + worldManager.getWorldName(worldName)
    }
    fun setProtectionPermission(plugin: Plugin) {
        if (player.isOp) { return }
        val worldName = player.world.name
        val judgement = when (worldName) {
            "Survival" -> true
            "Home" -> true
            else -> false
        }
        val protectiveAuthority = worldName == "Home"
        changePermission("blocklocker.protect", judgement, plugin)
        changePermission("worldguard.region.claim", protectiveAuthority, plugin)
        changePermission("worldedit.selection.*", protectiveAuthority, plugin)
    }
    fun fastJoin(plugin: Plugin) {
        player.scoreboardTags.add("member")
        Scoreboard().set("money", player.uniqueId.toString(), 30000)
        makePlayerDataFile(plugin)
    }
    private fun makePlayerDataFile(plugin: Plugin) {
        val templateFile = plugin.getResource("PlayerData.yml") ?: return
        val newDataFile = File("${plugin.dataFolder}/playerData/", "${player.uniqueId}.yml")

        // テンプレートファイルの内容を新しいファイルにコピーする
        templateFile.use { input ->
            newDataFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
    fun putItemInPost(post: Barrel) {
        val playerItem = player.inventory.itemInMainHand.clone()
        playerItem.amount = 1
        post.inventory.addItem(playerItem)
        player.inventory.removeItem(playerItem)
        player.sendMessage("${ChatColor.GOLD}[ポスト]アイテムをポストに入れました")
    }
    fun teleporterWorld(worldName: String) {
        player.teleport(
            Bukkit.getWorld(
                if (player.world.name == "world") { worldName } else { "world" }
            )?.spawnLocation ?: return
        )
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
    fun upDataEnderChestLevel(plugin: Plugin) {
        when (val level = EnderChest().investigateEnderChestSize(this)) {
            6 -> sendErrorMessage("これ以上拡張することはできません")
            else -> {
                luckPerms.revokePermission("enderchest.size.$level")
                luckPerms.addPermission("enderchest.size.${level + 1}")
                player.sendMessage("${ChatColor.AQUA}エンダーチェスト容量UP")
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                ItemManager().reduceMainItem(player)
            }
        }
    }

    fun createBossbar(title: String): BossBar {
        val bossbar = Bukkit.createBossBar(title, BarColor.BLUE, BarStyle.SOLID)
        bossbar.addPlayer(player)
        return bossbar
    }
    fun makeLandPurchase(sign: Sign) {
        sign.setLine(0, "${ChatColor.YELLOW}[土地販売]")
        sign.setLine(1, "${ChatColor.GREEN}${sign.getLine(1)}円")
        sign.update()
    }
    private fun makeConservationLand(name: String, aoringoPlayer: AoringoPlayer) {
        if (LandPurchase().doesRegionContainProtection(player)) {
            sendErrorMessage("保護範囲が含まれています")
        } else if (WorldGuard().getProtection(player.world, name)) {
            sendErrorMessage("同じ名前の保護を設定することは不可能です")
        } else {
            PluginData.DataManager.playerDataMap.getOrPut(aoringoPlayer.player.uniqueId) { AoringoPlayer.PlayerData() }.entry = name
            val gui = LandProtectiveGUI().createGUI(player)
            player.openInventory(gui)
        }
    }
    fun namingConservationLand(plugin: Plugin, name: String, aoringoPlayer: AoringoPlayer) {
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable
            {
                makeConservationLand(name, aoringoPlayer)
            }
        )
    }
    fun breakVegetables(block: org.bukkit.block.Block, plugin: Plugin) {
        val itemManager = ItemManager()
        val vegetablesNameMap = mapOf(
            Material.WHEAT to "${ChatColor.GREEN}小麦",
            Material.CARROT to "${ChatColor.GOLD}人参",
            Material.POTATO to "${ChatColor.GOLD}じゃがいも"
        )
        for (item in block.drops) {
            val vegetablesName = vegetablesNameMap[item.type] ?: continue
            block.world.dropItem(
                block.location,
                ItemManager().make(
                    item.type,
                    vegetablesName,
                    FoodManager().makeExpirationDate(14)
                )
            )
        }
        block.type = Material.AIR
    }
    fun reduceFoodLevel(plugin: Plugin) {
        Bukkit.getScheduler().runTaskLater(
            plugin,
            Runnable {
                player.foodLevel = 6
            },
            20L
        ) // 20Lは1秒を表す（1秒 = 20ticks）
    }
    fun isOperator(): Boolean {
        return player.isOp
    }
    fun sendNoOpMessage() {
        sendErrorMessage("このコマンドはOPのみ実行可能です")
    }
    fun hpEnemyShow(entity: Mob, damage: Double) {
        val health = maxOf(0.0, entity.health - damage).toInt()
        sendActionBar("${ChatColor.RED}${health}HP")
    }
    fun causeDamageAdditional(entity: Mob) {
        val power = Scoreboard().getValue("status_Power", player.uniqueId.toString())
        val addPowerPercentage = 0.1
        entity.damage(power * addPowerPercentage)
    }
    fun isFirstTimePlayer(participatedTags: String): Boolean {
        return player.scoreboardTags.contains(participatedTags)
    }
}
