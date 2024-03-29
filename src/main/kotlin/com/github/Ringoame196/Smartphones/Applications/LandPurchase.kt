package com.github.Ringoame196.Smartphone.APKs

import com.github.Ringoame196.Admin
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.ExternalPlugins.WorldGuard
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.MoneyUseCase
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Yml
import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import java.util.UUID

class LandPurchase {
    private fun openOwnerGUI(player: Player, name: String, money: Int) {
        val itemManager = ItemManager()
        val memberAddSlot = 3
        val memberRemoveSlot = 5
        val prepaymentButtonSlot = 8
        val protectionScoreboadName = "protectionContract"
        val contractPeriod = Scoreboard().getValue(protectionScoreboadName, name)
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}$name@土地設定")
        player.openInventory(gui)
        gui.setItem(memberAddSlot, itemManager.make(Material.GREEN_WOOL, "${ChatColor.GREEN}メンバー追加"))
        gui.setItem(memberRemoveSlot, itemManager.make(Material.RED_WOOL, "${ChatColor.RED}メンバー削除"))
        if (player.world.name != "shop") { return }
        if (contractPeriod != 1) { return }
        val price = money * 1.5.toInt()
        gui.setItem(prepaymentButtonSlot, itemManager.make(Material.GOLD_INGOT, "${ChatColor.YELLOW}前払い", "${price}円"))
    }
    fun addMemberGUI(player: Player, name: String) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}$name@メンバー追加")
        var i = 0
        for (worldPlayer in player.world.players) {
            gui.addItem(playerHead(worldPlayer))
            i++
            if (i >= gui.size) { continue }
        }
        player.openInventory(gui)
    }
    fun removeMemberGUI(player: Player, name: String) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.RED}$name@メンバー削除")
        var i = 0
        val members = WorldGuard().getMember(player.world, name)?.playerDomain?.uniqueIds
        for (worldPlayer in members?.toList() ?: return) {
            gui.addItem(ItemManager().make(Material.PLAYER_HEAD, worldPlayer.toString(), Bukkit.getPlayer(worldPlayer as UUID)?.name ?: worldPlayer.toString()))
            i++
            if (i >= gui.size) { continue }
        }
        player.openInventory(gui)
    }
    fun advancePayment(player: Player, name: String, money: Int) {
        val aoringoPlayer = AoringoPlayer(player)
        val moneyUseCase = aoringoPlayer.moneyUseCase
        val playerAccount = aoringoPlayer.playerAccount
        val playerMoney = moneyUseCase.getMoney(playerAccount)
        if (money > playerMoney) {
            aoringoPlayer.sendErrorMessage("お金が足りません")
        } else {
            moneyUseCase.tradeMoney(aoringoPlayer, Admin(), money)
            Scoreboard().set("protectionContract", name, 2)
            player.sendMessage("${ChatColor.AQUA}前払いしました")
        }
        player.closeInventory()
    }
    private fun playerHead(target: Player): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.setDisplayName(target.name)
        meta.setOwningPlayer(target)
        item.setItemMeta(meta)
        return item
    }
    fun buyGUI(player: Player, sign: Sign) {
        val worldGuard = WorldGuard()
        val aoringoPlayer = AoringoPlayer(player)
        val signWritten = sign.getLine(1)
        val price = MoneyUseCase().convertingInt(signWritten, "${ChatColor.GREEN}")
        val name = worldGuard.getName(sign.location)
        if (worldGuard.getOwnerOfRegion(sign.location)?.size() != 0) {
            if (worldGuard.getOwnerOfRegion(sign.location)?.contains(player.uniqueId) == true) {
                openOwnerGUI(player, name, price)
            } else {
                aoringoPlayer.sendErrorMessage("この土地は既に買われています")
            }
            return
        }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}$name@土地購入")
        gui.setItem(4, ItemManager().make(Material.EMERALD, "${ChatColor.GREEN}購入", "${price}円"))
        player.openInventory(gui)
    }
    fun buy(player: Player, item: ItemStack, guiName: String, plugin: Plugin) {
        val aoringoPlayer = AoringoPlayer(player)
        val moneyUseCase = aoringoPlayer.moneyUseCase
        val playerAccount = aoringoPlayer.playerAccount
        val playerMoney = moneyUseCase.getMoney(playerAccount)
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (item.itemMeta?.displayName) {
            "${ChatColor.GREEN}購入" -> {
                val money = item.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt() ?: return
                if (money > playerMoney) {
                    aoringoPlayer.sendErrorMessage("お金が足りません")
                    return
                }
                val name = guiName.replace("${ChatColor.BLUE}", "").replace("@土地購入", "")
                if (WorldGuard().getOwner(player.world, name) != "") {
                    aoringoPlayer.sendErrorMessage("この土地は既に買われています")
                    return
                }
                WorldGuard().addOwnerToRegion(name, player)
                player.closeInventory()
                player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                moneyUseCase.tradeMoney(aoringoPlayer, Admin(), money)
                if (player.world.name != "shop") { return }
                Yml().addToList(plugin, "", "conservationLand", "protectedName", name)
                Scoreboard().set("protectionContract", name, 1)
            }
        }
    }
    fun calculatePrice(player: Player): Int {
        val blockCount = countSelectBlocks(player)
        return blockCount * if (blockCount <= 256) { 10 } else { 100 }
    }
    private fun countSelectBlocks(player: Player): Int {
        try {
            val session = WorldEdit.getInstance().sessionManager[BukkitAdapter.adapt(player)]
            val region: Region = session.getSelection(BukkitAdapter.adapt(player.world)) ?: return 0
            if (region !is CuboidRegion) {
                return 0
            }
            val maximumPoint = region.maximumPoint
            val minimumPoint = region.minimumPoint

            // x軸およびz軸の長さを計算
            val xLength =
                Math.abs(maximumPoint.blockX - minimumPoint.blockX) + 1
            val zLength =
                Math.abs(maximumPoint.blockZ - minimumPoint.blockZ) + 1

            // x軸およびz軸上のブロック数を計算
            return xLength * zLength
        } catch (e: IncompleteRegionException) {
            AoringoPlayer(player).sendErrorMessage("範囲設定が間違っています")
            return 0
        }
    }

    fun doesRegionContainProtection(player: Player): Boolean {
        val session = WorldEdit.getInstance().sessionManager[BukkitAdapter.adapt(player)]

        try {
            val region: Region = session.getSelection(BukkitAdapter.adapt(player.world))
            if (region is CuboidRegion) {
                val minPoint = region.minimumPoint
                val maxPoint = region.maximumPoint

                // 選択範囲内のx座標とz座標を取得するリスト
                val xzCoordinates = mutableListOf<Pair<Int, Int>>()

                // 選択範囲内のすべてのx座標とz座標を取得
                for (x in minPoint.blockX until maxPoint.blockX + 1) {
                    for (z in minPoint.blockZ until maxPoint.blockZ + 1) {
                        xzCoordinates.add(Pair(x, z))
                    }
                }

                // 取得したx座標とz座標のリストを使って処理を行います
                for ((x, z) in xzCoordinates) {
                    if (WorldGuard().getName(player.world.getBlockAt(x, 0, z).location) == "null") { continue }
                    return true
                }
                return false
            }
        } catch (e: IncompleteRegionException) {
            e.printStackTrace()
        }
        return false
    }
    fun listRegionsInWorld(player: Player) {
        val regionManager = com.sk89q.worldguard.WorldGuard.getInstance().platform.regionContainer.get(BukkitAdapter.adapt(player.world))
        val regions: MutableMap<String, ProtectedRegion>? = regionManager?.regions ?: return

        player.sendMessage("${ChatColor.YELLOW}---あなたの所持保護一覧---")
        var c = 1
        for ((regionName, region) in regions ?: return) {
            if (!region.owners.contains(player.uniqueId)) { continue }
            player.sendMessage("${ChatColor.AQUA}$c,$regionName")
            c++
        }
    }
}
