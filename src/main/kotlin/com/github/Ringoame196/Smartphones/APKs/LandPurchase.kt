package com.github.Ringoame196.Smartphone.APKs

import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Data.WorldGuard
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Items.Item
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

class LandPurchase() {
    private fun ownerGUI(player: Player, name: String, money: Int) {
        val item = Item()
        val memberAddSlot = 3
        val memberRemoveSlot = 5
        val prepaymentButtonSlot = 8
        val protectionScoreboadName = "protectionContract"
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}$name@土地設定")
        gui.setItem(memberAddSlot, item.make(Material.GREEN_WOOL, "${ChatColor.GREEN}メンバー追加"))
        gui.setItem(memberRemoveSlot, item.make(Material.RED_WOOL, "${ChatColor.RED}メンバー削除"))
        if (player.world.name == "shop" && Scoreboard().getValue(protectionScoreboadName, name) == 1) {
            val price = money * 1.5.toInt()
            gui.setItem(prepaymentButtonSlot, Item().make(Material.GOLD_INGOT, "${ChatColor.YELLOW}前払い", "${price}円"))
        }
        player.openInventory(gui)
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
            gui.addItem(Item().make(Material.PLAYER_HEAD, worldPlayer.toString(), Bukkit.getPlayer(worldPlayer as UUID)?.name ?: worldPlayer.toString()))
            i++
            if (i >= gui.size) { continue }
        }
        player.openInventory(gui)
    }
    fun advancePayment(player: Player, name: String, money: Int) {
        val aoringoPlayer = AoringoPlayer(player)
        if (money > Money().get(player.uniqueId.toString())) {
            aoringoPlayer.sendErrorMessage("お金が足りません")
        } else {
            Money().remove(player.name, money, true)
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
        val aoringoPlayer = AoringoPlayer(player)
        val money = sign.getLine(1).replace("${ChatColor.GREEN}", "").replace("円", "").toInt()
        val name = WorldGuard().getName(sign.location) ?: return
        if (WorldGuard().getOwnerOfRegion(sign.location)?.size() != 0) {
            if (WorldGuard().getOwnerOfRegion(sign.location)?.contains(player.uniqueId) == true) {
                ownerGUI(player, name, money)
            } else {
                aoringoPlayer.sendErrorMessage("この土地は既に買われています")
            }
            return
        }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}$name@土地購入")
        gui.setItem(4, Item().make(Material.EMERALD, "${ChatColor.GREEN}購入", "${money}円"))
        player.openInventory(gui)
    }
    fun buy(player: Player, item: ItemStack, guiName: String, plugin: Plugin) {
        val aoringoPlayer = AoringoPlayer(player)
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (item.itemMeta?.displayName) {
            "${ChatColor.GREEN}購入" -> {
                val money = item.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt() ?: return
                if (money > Money().get(player.uniqueId.toString())) {
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
                Money().remove(player.uniqueId.toString(), money, true)
                if (player.world.name != "shop") { return }
                Yml().addToList(plugin, "", "conservationLand", "protectedName", name)
                Scoreboard().set("protectionContract", name, 1)
            }
        }
    }
    fun price(player: Player): Int {
        val session = WorldEdit.getInstance().sessionManager[BukkitAdapter.adapt(player)]

        try {
            val region: Region = session.getSelection(BukkitAdapter.adapt(player.world))
            if (region is CuboidRegion) {
                val cuboidRegion = region

                // x軸およびz軸の長さを計算
                val xLength =
                    Math.abs(cuboidRegion.maximumPoint.blockX - cuboidRegion.minimumPoint.blockX) + 1
                val zLength =
                    Math.abs(cuboidRegion.maximumPoint.blockZ - cuboidRegion.minimumPoint.blockZ) + 1

                // x軸およびz軸上のブロック数を計算
                val blockCount = xLength * zLength
                return blockCount * if (blockCount <= 256) { 10 } else { 100 }
            }
        } catch (e: IncompleteRegionException) {
            e.printStackTrace()
        }
        return 0
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
