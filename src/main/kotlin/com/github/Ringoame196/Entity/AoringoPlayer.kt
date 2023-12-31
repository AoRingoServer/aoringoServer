package com.github.Ringoame196.Entity

import com.github.Ringoame196.Contract
import com.github.Ringoame196.Data.WorldGuard
import com.github.Ringoame196.EnderChest
import com.github.Ringoame196.Foods.FoodManager
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Job.JobManager
import com.github.Ringoame196.MoneyUseCase
import com.github.Ringoame196.PlayerAccount
import com.github.Ringoame196.PluginData
import com.github.Ringoame196.ResourcePack
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import com.github.Ringoame196.Smartphones.Smartphone
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
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.plugin.Plugin

class AoringoPlayer(val player: Player) {
    val playerAccount = PlayerAccount(player)
    val moneyUseCase = MoneyUseCase()
    val jobManager = JobManager()
    data class PlayerData(
        var titleMoneyBossbar: BossBar? = null,
        var chatSettingItem: String? = null
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
        moneyUseCase.displayMoney(this)
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
        val judgement = when (player.world.name) {
            "Survival" -> true
            "Home" -> true
            else -> false
        }
        changePermission("blocklocker.protect", judgement, plugin)
        changePermission("worldguard.region.claim", judgement, plugin)
    }
    fun fastJoin() {
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
    fun teleporterWorld(worldName: String) {
        player.teleport(
            Bukkit.getWorld(
                if (player.world.name == "world") { worldName } else { "world" }
            )?.spawnLocation ?: return
        )
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
    fun upDataEnderChestLevel(plugin: Plugin) {
        when (val level = EnderChest().investigateEnderChestSize(player)) {
            6 -> sendErrorMessage("これ以上拡張することはできません")
            else -> {
                Scoreboard().set("haveEnderChest", player.uniqueId.toString(), level + 1)
                changePermission("enderchest.size.$level", true, plugin)
                player.sendMessage("${ChatColor.AQUA}エンダーチェスト容量UP")
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                ItemManager().reduceMainItem(player)
            }
        }
    }

    fun createBossbar(title: String): BossBar {
        val bossbar = Bukkit.createBossBar(title, BarColor.BLUE, BarStyle.SOLID)
        bossbar.addPlayer(player)
        PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.titleMoneyBossbar = bossbar
        return bossbar
    }
    fun makeShop(sign: Sign) {
        val downBlock = sign.block.location.clone().add(0.0, -1.0, 0.0).block
        if (downBlock.type != Material.BARREL) { return }
        val itemFrame = sign.world.spawn(sign.location, org.bukkit.entity.ItemFrame::class.java)
        itemFrame.customName = "@Fshop,userID:${player.uniqueId},price:${sign.getLine(1)}"
    }
    fun makeLandPurchase(sign: Sign) {
        sign.setLine(0, "${ChatColor.YELLOW}[土地販売]")
        sign.setLine(1, "${ChatColor.GREEN}${sign.getLine(1)}円")
        sign.update()
    }
    fun makeConservationLand(name: String) {
        val price = LandPurchase().calculatePrice(player)
        if (price == 0) { return }
        if (LandPurchase().doesRegionContainProtection(player)) {
            sendErrorMessage("保護範囲が含まれています")
        } else if (WorldGuard().getProtection(player.world, name)) {
            sendErrorMessage("同じ名前の保護を設定することは不可能です")
        } else {
            player.openInventory(Smartphone().createProtectionGUI(player, name, price))
        }
    }
    fun namingConservationLand(plugin: Plugin, name: String) {
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable
            {
                makeConservationLand(name)
            }
        )
    }
    fun writeContractRequest(money: Int) {
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        meta.setDisplayName("${ChatColor.YELLOW}契約書[契約待ち]")
        val bookMessage = meta.getPage(1)
            .replace("甲方：[プレイヤー名]\nUUID：[UUID]", "甲方：${player.name}\nUUID：${player.uniqueId}")
            .replace("取引金額：[値段]", "取引金額：${money}円")
        meta.setPage(1, bookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
    fun createContractBook(money: Int) {
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        val bookMessage = meta.getPage(1)
        val priceIndex = bookMessage.indexOf("取引金額：")
        val priceMessage = bookMessage.substring(priceIndex + "取引金額：".length).replace("円", "").toInt()
        if (money != priceMessage) {
            sendErrorMessage("金額が違います")
            return
        }
        if (moneyUseCase.getMoney(playerAccount) < money) {
            sendErrorMessage("お金が足りません")
            return
        }
        moneyUseCase.reduceMoney(this, money)
        val setBookMessage = Contract().writeContractDate(meta, player, money)
        meta.setPage(1, setBookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
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
    fun sendTeleportDungeonMessage() {
        ItemManager().reduceMainItem(player)
        player.sendTitle("${ChatColor.YELLOW}ダンジョン", "")
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
        player.sendMessage("${ChatColor.GOLD}ダンジョンへ移動しました")
    }
    fun isOperator(): Boolean {
        return player.isOp
    }
    fun sendNoOpMessage() {
        sendErrorMessage("このコマンドはOPのみ実行可能です")
    }
}
