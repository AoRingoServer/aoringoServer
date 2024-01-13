package com.github.Ringoame196.Smartphones

import com.github.Ringoame196.Admin
import com.github.Ringoame196.ApplicationManager
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.MoneyManager
import com.github.Ringoame196.MoneyUseCase
import com.github.Ringoame196.Smartphone.APKs.ItemProtectionApplication
import com.github.Ringoame196.Smartphones.Applications.Application
import com.github.Ringoame196.Smartphones.Applications.ConversionMoneyApplication
import com.github.Ringoame196.Smartphones.Applications.EnderChestApplication
import com.github.Ringoame196.Smartphones.Applications.HealthCcareApplication
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplication
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.AddMember
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.Delete
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.DeletionMember
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.GetWoodenAxe
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.Information
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.ProtectionCreationButton
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.ProtectionInformation
import com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.ProtectionListButton
import com.github.Ringoame196.Smartphones.Applications.PlayerRatingApplication
import com.github.Ringoame196.Smartphones.Applications.SortApplication
import com.github.Ringoame196.Smartphones.Applications.TeleportApplication
import com.github.Ringoame196.Worlds.WorldManager
import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.File

class Smartphone {
    val apkList = mapOf<String, Application>(
        "${ChatColor.YELLOW}エンダーチェスト" to EnderChestApplication(),
        "${ChatColor.GREEN}所持金変換" to ConversionMoneyApplication(),
        "${ChatColor.RED}アイテム保護" to ItemProtectionApplication(),
        "${ChatColor.GREEN}テレポート" to TeleportApplication(),
        "${ChatColor.GREEN}プレイヤー評価" to PlayerRatingApplication(),
        "${ChatColor.GREEN}土地保護" to LandProtectionApplication(),
        "${ChatColor.YELLOW}アプリ並べ替え" to SortApplication(),
        "${ChatColor.AQUA}ヘルスケア" to HealthCcareApplication(),
    )
    fun createGUI(plugin: Plugin, player: Player): Inventory {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        val smartphoneSlots = mutableListOf(1, 3, 5, 7, 10, 12, 14, 16, 19, 21, 23, 25)
        val playerHaveAPKList = Yml().getList(plugin, "playerData", player.uniqueId.toString(), "apkList")
        if (playerHaveAPKList.isNullOrEmpty()) {
            return gui
        }

        val apkCount = minOf(smartphoneSlots.size, playerHaveAPKList.size)
        for (i in 0 until apkCount) {
            val apkName = playerHaveAPKList[i]
            val applicationInfo = getApplicationInfo(apkName, plugin)
            val customModelData = getCustomModelData(applicationInfo)
            val lore = applicationInfo?.get("lore").toString()
            gui.setItem(
                smartphoneSlots[i],
                ItemManager().make(
                    Material.GREEN_CONCRETE, "${ChatColor.YELLOW}[アプリ]$apkName",
                    lore,
                    customModelData
                )
            )
        }
        return gui
    }
    private fun getCustomModelData(applicationInfo: Map<String, Any>?): Int {
        val customModelData = applicationInfo?.get("customModelData")
        return customModelData?.toString()?.toInt() ?: 0
    }
    private fun getApplicationInfo(applicationName: String, plugin: Plugin): Map<String, Any>? {
        val ymlFile = File(plugin.dataFolder, "Application.yml")

        try {
            java.io.FileReader(ymlFile).use { fileReader ->
                val yaml = Yaml()
                val yamlData = yaml.load(fileReader) as? Map<String, Any>

                // itemNameに対応する値があるか確認
                if (yamlData != null) {
                    return yamlData[applicationName] as? Map<String, Any>
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun startUpAKS(player: Player, item: ItemStack, plugin: Plugin, shift: Boolean) {
        val itemName = item.itemMeta?.displayName
        val apkName = itemName?.replace("${ChatColor.YELLOW}[アプリ]", "") ?: return
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        if (shift && item.type == Material.GREEN_CONCRETE) {
            ApplicationManager().uninstall(player, apkName, item.itemMeta?.customModelData ?: 0, plugin)
            player.openInventory(createGUI(plugin, player))
            return
        }
        apkList[apkName]?.bootApplication(player, plugin)
        teleportWorldFromPlayer(player, apkName, plugin)
        if (item.type == Material.EMERALD && (item.itemMeta?.customModelData ?: return) >= 1) {
            if ((item.itemMeta?.customModelData ?: return) > 4) { return }
            val money = itemName.replace("${ChatColor.GREEN}", "").replace("円", "").toInt()
            moneyItem(player, money, item)
            MoneyUseCase().reduceMoney(AoringoPlayer(player), money)
        }
    }
    private fun getWorldSpawnLocation(worldName: String): Location? {
        return Bukkit.getWorld(worldName)?.spawnLocation
    }
    private fun getWorldID(worldName: String, plugin: Plugin): String? {
        val worldManager = WorldManager(plugin)
        return worldManager.getWorldID(worldName)
    }
    private fun teleportWorldFromPlayer(player: Player, worldName: String, plugin: Plugin) {
        val worldID = getWorldID(worldName, plugin)
        val playerLocation = player.location
        val location = getWorldSpawnLocation(worldID ?: return)
        player.teleport(location ?: playerLocation)
    }
    fun wgClick(item: ItemStack, plugin: Plugin, player: Player, shift: Boolean) {
        val playerClass = AoringoPlayer(player)
        if (player.world.name != "Home" && !player.isOp) {
            playerClass.sendErrorMessage("保護は生活ワールドのみ使用可能です")
            player.closeInventory()
            return
        }
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        val clickItems = mapOf<String, com.github.Ringoame196.Smartphones.Applications.LandProtectionApplications.LandProtectionApplicationButton>(
            "${ChatColor.GOLD}木の斧ゲット" to GetWoodenAxe(),
            "${ChatColor.AQUA}保護一覧" to ProtectionListButton(),
            "${ChatColor.YELLOW}保護作成" to ProtectionCreationButton(),
            "${ChatColor.GREEN}情報" to Information(),
            "${ChatColor.YELLOW}保護情報" to ProtectionInformation(),
            "${ChatColor.AQUA}メンバー追加" to AddMember(),
            "${ChatColor.RED}メンバー削除" to DeletionMember(),
            "${ChatColor.RED}削除" to Delete()
        )
        val name = item.itemMeta?.displayName
        clickItems[name]?.click(player, shift) ?: return
    }
    fun createProtectionGUI(player: Player, name: String, price: Int): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}保護設定($name)")
        gui.setItem(4, ItemManager().make(Material.GREEN_WOOL, "${ChatColor.GREEN}作成", "${price}円"))
        return gui
    }
    fun protection(player: org.bukkit.entity.Player, item: ItemStack, name: String) {
        val aoringoPlayer = AoringoPlayer(player)
        val playerAccount = aoringoPlayer.playerAccount
        val price = item.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt() ?: return
        val world = player.world
        val playerMoney = aoringoPlayer.moneyUseCase.getMoney(playerAccount)
        if (playerMoney < price) {
            AoringoPlayer(player).sendErrorMessage("お金が足りません")
            return
        }
        player.performCommand("/expand vert")
        player.performCommand("rg claim $name")
        if (WorldGuard().getProtection(world, name)) {
            player.sendMessage("${ChatColor.GREEN}[WG]正常に保護をかけました")
            MoneyManager().tradeMoney(Admin(), playerAccount, price)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        }
        player.closeInventory()
    }
    private fun moneyItem(player: Player, money: Int, item: ItemStack) {
        val aoringoPlayer = AoringoPlayer(player)
        val playerAccount = aoringoPlayer.playerAccount
        val playerMoney = aoringoPlayer.moneyUseCase.getMoney(playerAccount)
        if (playerMoney < money) {
            com.github.Ringoame196.Entity.AoringoPlayer(player).sendErrorMessage("お金が足りません")
        } else {
            val giveItem = item.clone()
            giveItem.amount = 1
            player.inventory.addItem(giveItem)
            MoneyManager().tradeMoney(Admin(), playerAccount, money)
        }
        player.closeInventory()
    }
}
