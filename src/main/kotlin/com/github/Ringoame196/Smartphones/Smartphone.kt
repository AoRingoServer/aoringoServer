package com.github.Ringoame196.Smartphones

import com.github.Ringoame196.APK
import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Data.WorldGuard
import com.github.Ringoame196.EnderChest
import com.github.Ringoame196.Evaluation
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.ResourcePack
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Smartphone.APKs.ItemProtection
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import com.github.Ringoame196.Yml
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Smartphone {
    fun createGUI(plugin: Plugin, player: Player): Inventory {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        val smartphone = mutableListOf(1, 3, 5, 7, 10, 12, 14, 16, 19, 21, 23, 25)
        val apks = Yml().getList(plugin, "playerData", player.uniqueId.toString(), "apkList")
        if (apks.isNullOrEmpty()) {
            return gui
        }

        val minSize = minOf(smartphone.size, apks.size)
        for (i in 0 until minSize) {
            val apkName = apks[i]
            gui.setItem(smartphone[i], Item().make(Material.GREEN_CONCRETE, "${ChatColor.YELLOW}[アプリ]$apkName", null, giveCustomModel(apkName), 1))
        }
        return gui
    }
    fun giveCustomModel(itemName: String): Int {
        return when (itemName) {
            "${ChatColor.YELLOW}エンダーチェスト" -> 1
            "${ChatColor.GREEN}所持金変換" -> 2
            "${ChatColor.RED}アイテム保護" -> 3
            "${ChatColor.GREEN}テレポート" -> 4
            "${ChatColor.GREEN}プレイヤー評価" -> 5
            "${ChatColor.GREEN}土地保護" -> 6
            "${ChatColor.YELLOW}OP用" -> 7
            "${ChatColor.YELLOW}アプリ並べ替え" -> 8
            "${ChatColor.AQUA}ヘルスケア" -> 9
            else -> 0
        }
    }
    fun clickItem(player: org.bukkit.entity.Player, item: ItemStack, plugin: Plugin, shift: Boolean) {
        val itemName = item.itemMeta?.displayName?.replace("${ChatColor.YELLOW}[アプリ]", "") ?: return
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        if (shift && item.type == Material.GREEN_CONCRETE) {
            APK().remove(player, itemName, item.itemMeta?.customModelData ?: 0, plugin)
            player.openInventory(createGUI(plugin, player))
            return
        }
        if (shift) { return }
        when (itemName) {
            "${ChatColor.YELLOW}エンダーチェスト" -> EnderChest().open(player, plugin)
            "${ChatColor.GREEN}所持金変換" -> conversion(player)
            "${ChatColor.RED}アイテム保護" -> ItemProtection().open(player)
            "${ChatColor.GREEN}テレポート" -> tpGUI(player)
            "${ChatColor.GOLD}ロビー" -> player.teleport(Bukkit.getWorld("world")?.spawnLocation ?: return)
            "${ChatColor.GREEN}生活ワールド" -> player.teleport(Bukkit.getWorld("Home")?.spawnLocation ?: return)
            "${ChatColor.AQUA}資源ワールド" -> player.teleport(Bukkit.getWorld("Survival")?.spawnLocation ?: return)
            "${ChatColor.YELLOW}ショップ" -> player.teleport(Bukkit.getWorld("shop")?.spawnLocation ?: return)
            "${ChatColor.RED}イベント" -> player.teleport(Bukkit.getWorld("event")?.spawnLocation ?: return)
            "${ChatColor.YELLOW}OP用" -> op(player)
            "${ChatColor.GREEN}プレイヤー評価" -> Evaluation().display(player)
            "${ChatColor.GREEN}土地保護" -> wgGUI(player)
            "${ChatColor.YELLOW}アプリ並べ替え" -> APK().sortGUIOpen(player, plugin)
            "${ChatColor.AQUA}ヘルスケア" -> healthcare(player)
        }
        if (item.type == Material.EMERALD && (item.itemMeta?.customModelData ?: return) >= 1) {
            if ((item.itemMeta?.customModelData ?: return) > 4) { return }
            val money = itemName.replace("${ChatColor.GREEN}", "").replace("円", "")?.toInt()
            moneyItem(player, money ?: return, item)
        }
    }
    fun opClick(item: ItemStack, plugin: Plugin, shift: Boolean, player: org.bukkit.entity.Player) {
        when (item.itemMeta?.displayName) {
            "${ChatColor.RED}ショップ保護リセット" -> {
                if (!shift) { return }
                val list = Yml().getList(plugin, "conservationLand", "", "protectedName") ?: return
                for (name in list) {
                    if (Scoreboard().getValue("protectionContract", name) == 2) {
                        Scoreboard().remove("protectionContract", name, 1)
                        continue
                    }
                    WorldGuard().reset(name, Bukkit.getWorld("shop") ?: return)
                    Yml().removeToList(plugin, "", "conservationLand", "protectedName", name)
                }
                Bukkit.broadcastMessage("${ChatColor.RED}[ショップ] ショップの購入土地がリセットされました")
            }
            "${ChatColor.YELLOW}リソパ更新" -> ResourcePack(plugin).update()
            "${ChatColor.GREEN}運営ギフトリセット" -> {
                if (!Scoreboard().existence("admingift")) { return }
                Scoreboard().delete("admingift")
                Scoreboard().make("admingift", "admingift")
                Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー] 運営ギフトがリセットされました")
            }
            "${ChatColor.GREEN}テストワールド" -> player.teleport(Bukkit.getWorld("testworld")?.spawnLocation ?: return)
        }
    }
    fun wgClick(item: ItemStack, plugin: Plugin, player: org.bukkit.entity.Player, shift: Boolean) {
        val playerClass = com.github.Ringoame196.Entity.AoringoPlayer(player, plugin)
        if (player.world.name != "Home" && !player.isOp) {
            playerClass.sendErrorMessage("保護は生活ワールドのみ使用可能です")
            player.closeInventory()
            return
        }
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (item.itemMeta?.displayName) {
            "${ChatColor.GOLD}木の斧ゲット" -> player.inventory.addItem(ItemStack(Material.WOODEN_AXE))
            "${ChatColor.AQUA}保護一覧" -> {
                player.closeInventory()
                LandPurchase().listRegionsInWorld(player)
            }
            "${ChatColor.YELLOW}保護作成" -> {
                player.closeInventory()
                player.addScoreboardTag("rg")
                player.sendMessage("${ChatColor.AQUA}[土地保護]保護名を入力してください")
            }
            "${ChatColor.GREEN}情報" -> {
                val gui = player.openInventory.topInventory
                gui.setItem(2, Item().make(Material.MAP, "${ChatColor.YELLOW}保護情報", null, null, 1))
                gui.setItem(4, Item().make(Material.PLAYER_HEAD, "${ChatColor.AQUA}メンバー追加", null, null, 1))
                gui.setItem(6, Item().make(Material.PLAYER_HEAD, "${ChatColor.RED}メンバー削除", null, null, 1))
                gui.setItem(8, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}削除", "${ChatColor.DARK_RED}シフトで実行", null, 1))
            }
            "${ChatColor.YELLOW}保護情報" -> {
                player.closeInventory()
                player.sendMessage("${ChatColor.YELLOW}-----保護情報-----")
                player.sendMessage("${ChatColor.GOLD}保護名:${WorldGuard().getName(player.location)}")
                player.sendMessage("${ChatColor.YELLOW}オーナー:" + if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) == true) { "${ChatColor.GOLD}あなたはオーナーです" } else { "${ChatColor.RED}あなたはオーナーではありません" })
                player.sendMessage("${ChatColor.AQUA}メンバー:" + if (WorldGuard().getMemberOfRegion(player.location)?.contains(player.uniqueId) == true) { "${ChatColor.GOLD}あなたはメンバーです" } else { "${ChatColor.RED}あなたはメンバーではありません" })
            }
            "${ChatColor.AQUA}メンバー追加" -> {
                if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
                    playerClass.sendErrorMessage("自分の保護土地内で実行してください")
                    return
                }
                LandPurchase().addMemberGUI(player, WorldGuard().getName(player.location))
            }
            "${ChatColor.RED}メンバー削除" -> {
                if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
                    playerClass.sendErrorMessage("自分の保護土地内で実行してください")
                    return
                }
                LandPurchase().removeMemberGUI(player, WorldGuard().getName(player.location) ?: return)
            }
            "${ChatColor.RED}削除" -> {
                if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
                    playerClass.sendErrorMessage("自分の保護土地内で実行してください")
                    return
                }
                if (!shift) { return }
                WorldGuard().delete(player, WorldGuard().getName(player.location) ?: return)
                player.sendMessage("${ChatColor.RED}保護を削除しました")
            }
        }
    }
    fun protectionGUI(player: Player, name: String) {
        val playerClass = com.github.Ringoame196.Entity.AoringoPlayer(player, null)
        if (LandPurchase().doesRegionContainProtection(player)) {
            playerClass.sendErrorMessage("保護範囲が含まれています")
            return
        }
        if (WorldGuard().getProtection(player, name)) {
            playerClass.sendErrorMessage("同じ名前の保護を設定することは不可能です")
            return
        }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}保護設定($name)")
        gui.setItem(4, Item().make(Material.GREEN_WOOL, "${ChatColor.GREEN}作成", "${LandPurchase().price(player)}円", null, 1))
        player.openInventory(gui)
    }
    fun protection(player: org.bukkit.entity.Player, item: ItemStack, name: String) {
        val price = item.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt() ?: return
        if ((Money().get(player.uniqueId.toString())) < price) {
            com.github.Ringoame196.Entity.AoringoPlayer(player, null).sendErrorMessage("お金が足りません")
            return
        }
        player.performCommand("/expand vert")
        player.performCommand("rg claim $name")
        if (WorldGuard().getProtection(player, name)) {
            player.sendMessage("${ChatColor.GREEN}[WG]正常に保護をかけました")
            Money().remove(player.uniqueId.toString(), price, true)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        }
        player.closeInventory()
    }
    private fun tpGUI(player: org.bukkit.entity.Player) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.CHEST, "${ChatColor.GOLD}ロビー", null, null, 1))
        gui.setItem(3, Item().make(Material.GRASS_BLOCK, "${ChatColor.GREEN}生活ワールド", null, null, 1))
        gui.setItem(5, Item().make(Material.DIAMOND_PICKAXE, "${ChatColor.AQUA}資源ワールド", null, null, 1))
        gui.setItem(7, Item().make(Material.QUARTZ_BLOCK, "${ChatColor.YELLOW}ショップ", null, null, 1))
        gui.setItem(19, Item().make(Material.BEDROCK, "${ChatColor.RED}イベント", null, null, 1))
        player.openInventory(gui)
    }
    private fun op(player: org.bukkit.entity.Player) {
        if (!player.isOp) { return }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}OP用")
        gui.setItem(0, Item().make(Material.COMMAND_BLOCK, "${ChatColor.YELLOW}リソパ更新", null, null, 1))
        gui.setItem(2, Item().make(Material.WOODEN_AXE, "${ChatColor.RED}ショップ保護リセット", null, null, 1))
        gui.setItem(4, Item().make(Material.DIAMOND, "${ChatColor.GREEN}運営ギフトリセット", null, null, 1))
        gui.setItem(6, Item().make(Material.CRAFTING_TABLE, "${ChatColor.GREEN}テストワールド", null, null, 1))
        player.openInventory(gui)
    }
    private fun wgGUI(player: org.bukkit.entity.Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}WorldGuardGUI")
        gui.setItem(2, Item().make(Material.GOLDEN_AXE, "${ChatColor.YELLOW}保護作成", "${LandPurchase().price(player)}円", null, 1))
        gui.setItem(4, Item().make(Material.MAP, "${ChatColor.GREEN}情報", null, null, 1))
        gui.setItem(6, Item().make(Material.CHEST, "${ChatColor.AQUA}保護一覧", null, null, 1))
        gui.setItem(8, Item().make(Material.WOODEN_AXE, "${ChatColor.GOLD}木の斧ゲット", null, null, 1))
        player.openInventory(gui)
    }
    private fun conversion(player: org.bukkit.entity.Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.EMERALD, "${ChatColor.GREEN}100円", null, 1, 1))
        gui.setItem(3, Item().make(Material.EMERALD, "${ChatColor.GREEN}1000円", null, 2, 1))
        gui.setItem(5, Item().make(Material.EMERALD, "${ChatColor.GREEN}10000円", null, 3, 1))
        gui.setItem(7, Item().make(Material.EMERALD, "${ChatColor.GREEN}100000円", null, 4, 1))
        player.openInventory(gui)
    }
    private fun moneyItem(player: Player, money: Int, item: ItemStack) {
        if ((Money().get(player.uniqueId.toString())) < money) {
            com.github.Ringoame196.Entity.AoringoPlayer(player, null).sendErrorMessage("お金が足りません")
        } else {
            val giveItem = item.clone()
            giveItem.amount = 1
            player.inventory.addItem(giveItem)
            Money().remove(player.uniqueId.toString(), money, true)
        }
        player.closeInventory()
    }
    private fun healthcare(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}ヘルスケア")
        gui.setItem(3, Item().make(Material.MELON_SLICE, "${ChatColor.RED}マックスHP", "${player.maxHealth.toInt()}HP", 92, 1))
        gui.setItem(5, Item().make(Material.MELON_SLICE, "${ChatColor.GREEN}Power", "${Scoreboard().getValue("status_Power",player.uniqueId.toString())}パワー", 91, 1))
        player.openInventory(gui)
    }
}
