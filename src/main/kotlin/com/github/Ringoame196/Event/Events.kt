package com.github.Ringoame196.Event

import com.github.Ringoame196.APK
import com.github.Ringoame196.Anvil
import com.github.Ringoame196.Blocks.Block
import com.github.Ringoame196.Cage
import com.github.Ringoame196.Contract
import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Discord
import com.github.Ringoame196.EnderChest
import com.github.Ringoame196.Entity.Player
import com.github.Ringoame196.Evaluation
import com.github.Ringoame196.Handover
import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Cook
import com.github.Ringoame196.Job.Data.CookData
import com.github.Ringoame196.Job.Job
import com.github.Ringoame196.Job.Mission
import com.github.Ringoame196.Resource
import com.github.Ringoame196.ResourcePack
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Scratch
import com.github.Ringoame196.Shop.BarrelShop
import com.github.Ringoame196.Shop.Fshop
import com.github.Ringoame196.Smartphone.APKs.ItemProtection
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import com.github.Ringoame196.Smartphones.Smartphone
import com.github.Ringoame196.WorldGuard
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Barrel
import org.bukkit.block.Sign
import org.bukkit.block.Smoker
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Mob
import org.bukkit.entity.Villager
import org.bukkit.entity.Wither
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import kotlin.random.Random

class Events(private val plugin: Plugin) : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (!player.scoreboardTags.contains("member")) {
            AoringoEvents().onFastJoinEvent(e)
        }
        Player().setName(player)
        player.maxHealth = 20.0 + Scoreboard().getValue("status_HP", player.uniqueId.toString()).toDouble()
        ResourcePack().adaptation(e.player, plugin)
        if (player.world.name == "Survival") {
            player.teleport(Bukkit.getWorld("world")?.spawnLocation ?: return)
        }
        Scoreboard().set("blockCount", player.name, 0)
        Player().setTab(player)
        Player().addPermission(player, plugin, "enderchest.size.${Scoreboard().getValue("haveEnderChest", player.uniqueId.toString()) + 1}")
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val item = e.item
        val itemName = item?.itemMeta?.displayName
        val block = e.clickedBlock
        val upBlock = block?.location?.clone()?.add(0.0, 1.0, 0.0)?.block
        val downBlock = block?.location?.clone()?.add(0.0, -1.0, 0.0)?.block
        if (item != player.inventory.itemInMainHand && item != null) { return }
        if (block?.type == Material.BARREL) {
            val shop = block.state as Barrel
            if (player.isOp && player.gameMode == GameMode.CREATIVE && item?.type == Material.NAME_TAG) {
                e.isCancelled = true
                BarrelShop().changeOwner(shop, item.itemMeta?.displayName ?: return, player)
            }
        }
        if (block?.type == Material.OAK_SIGN && downBlock?.type == Material.BARREL) {
            val sign = block.state as Sign
            val barrel = downBlock.state as Barrel
            when (sign.getLine(0)) {
                "shop" -> {
                    if (sign.getLine(2) == "") {
                        BarrelShop().make(barrel, player, sign)
                        return
                    }
                }

                "${ChatColor.GREEN}shop" -> {
                    val price: Int = sign.getLine(1).replace("円", "").replace("${ChatColor.YELLOW}", "").toInt()
                    BarrelShop().purchase(player, barrel, price)
                }

                "Fshop" -> {
                    e.isCancelled = true
                    val itemFrame = block.world.spawn(block.location, org.bukkit.entity.ItemFrame::class.java)
                    itemFrame.customName = "@Fshop,userID:${player.uniqueId},price:${sign.getLine(1)}"
                    block.type = Material.AIR
                }
            }
        }
        if (block?.type == Material.ANVIL || block?.type == Material.CHIPPED_ANVIL || block?.type == Material.DAMAGED_ANVIL) {
            if (player.gameMode == GameMode.CREATIVE) {
                return
            }
            e.isCancelled = true
            Anvil().open(player)
        } else if (block?.type == Material.SMITHING_TABLE) {
            if (Job().get(player) != "${ChatColor.GRAY}鍛冶屋") {
                AoringoEvents().onErrorEvent(player, "${ChatColor.RED}鍛冶屋以外は使用することができません")
                e.isCancelled = true
            }
        } else if (block?.type == Material.SMOKER) {
            if (e.action == Action.LEFT_CLICK_BLOCK) {
                return
            }
            e.isCancelled = true
        } else if (itemName == "職業スター") {
            if (player.inventory.itemInMainHand != item) {
                return
            }
            Job().selectGUI(player)
        } else if (itemName == "まな板") {
            e.isCancelled = true
            if (block?.location?.clone()?.add(0.0, 1.0, 0.0)?.block?.type != Material.AIR) {
                return
            }
            Cook().cuttingBoard(block)
            val playerItem = player.inventory.itemInMainHand
            playerItem.amount = playerItem.amount - 1
            player.inventory.setItemInMainHand(playerItem)
        } else if (downBlock?.type == Material.CAMPFIRE && (block.type == Material.WATER_CAULDRON || block.type == Material.CAULDRON)) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) {
                return
            }
            upBlock ?: return
            if (upBlock.type == Material.IRON_TRAPDOOR) {
                return
            }
            Cook().pot(block, player, plugin)
        } else if (block?.type == Material.ENCHANTED_BOOK) {
            e.isCancelled = true
        } else if (block?.type == Material.LAVA_CAULDRON) {
            item ?: return
            val fryItem = CookData().fly(item) ?: return
            if (!Cook().isCookLevel(fryItem.itemMeta?.displayName ?: return, player)) {
                return
            }
            player.world.playSound(player.location, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
            Item().remove(player)
            Cook().fry(player, block, item, plugin)
        } else if (block?.type == Material.BARREL && downBlock?.type == Material.CAMPFIRE && item?.itemMeta?.displayName == "${ChatColor.YELLOW}おたま") {
            e.isCancelled = true
            Cook().pot(block, player, plugin)
            if (Random.nextInt(0, 100) != 0) {
                return
            }
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            AoringoEvents().onErrorEvent(player, "おたまがぶっ壊れた")
        } else if (block?.type == Material.ENCHANTING_TABLE) {
            e.isCancelled = true
            if (player.foodLevel < 10) {
                AoringoEvents().onErrorEvent(player, "満腹度が足りません")
                return
            }
            Block().enchantGUI(player)
        } else if (itemName == "${ChatColor.YELLOW}カゴ") {
            if (player.inventory.itemInMainHand != item) {
                return
            }
            e.isCancelled = true
            Cage().open(player)
        } else if (block?.type == Material.BARREL) {
            if (player.gameMode == GameMode.CREATIVE) {
                return
            }
            val barrel = block.state as Barrel
            if (barrel.customName == "クエスト") {
                e.isCancelled = true
                if (Scoreboard().getValue("mission", player.name) == 0) {
                    Mission().set(player, barrel)
                } else {
                    Mission().check(player, barrel)
                }
            }
        } else if ((block?.type == Material.BEE_NEST || block?.type == Material.BEEHIVE) && item?.type == Material.GLASS_BOTTLE) {
            e.isCancelled = true
            val beeNest = block.blockData as org.bukkit.block.data.type.Beehive
            if (beeNest.honeyLevel != 5 || player.inventory.itemInMainHand.type != Material.GLASS_BOTTLE) {
                return
            }
            beeNest.honeyLevel = 0
            e.clickedBlock!!.blockData = beeNest
            player.inventory.addItem(
                Item().make(
                    Material.HONEY_BOTTLE,
                    "${ChatColor.GOLD}ハチミツ",
                    Food().giveExpirationDate(14),
                    null,
                    1
                )
            )
            val playerItem = player.inventory.itemInMainHand.clone()
            playerItem.amount = playerItem.amount - 1
            player.inventory.setItemInMainHand(playerItem)
        }
        if (player.world.name == "Survival") {
            val ngItem = mutableListOf(Material.LAVA_BUCKET, Material.FLINT_AND_STEEL, Material.FIRE_CHARGE)
            if (ngItem.contains(item?.type)) {
                e.isCancelled = true
                AoringoEvents().onErrorEvent(player, "${item?.type}は使用禁止です")
                return
            }
        }
        if (item == Item().smartphone()) {
            Smartphone().open(plugin, player)
        } else if (item?.type == Material.EMERALD && (item.itemMeta?.customModelData ?: return) >= 1) {
            val money = itemName?.replace("${ChatColor.GREEN}", "")?.replace("円", "")?.toInt()
            if (money == 0) {
                return
            }
            Money().add(player.uniqueId.toString(), (money?.times(item.amount) ?: return), true)
            player.inventory.remove(item)
        } else if (item?.itemMeta?.displayName == "${ChatColor.YELLOW}契約書[未記入]") {
            player.sendMessage("${ChatColor.YELLOW}契約書を発行するには [!契約 (値段)]")
        } else if (item?.itemMeta?.displayName == "${ChatColor.YELLOW}契約書[契約待ち]") {
            player.sendMessage("${ChatColor.YELLOW}契約書を完了するには [!契約 (契約書に書かれているお金)]")
        } else if (item?.itemMeta?.displayName?.contains("${ChatColor.RED}契約本") == true) {
            if (player.isSneaking) {
                Contract().returnMoney(player)
            } else {
                Player().sendActionBar(player, "お金を受け取るにはシフトをしてください")
            }
        } else if (block?.type == Material.OAK_SIGN) {
            val sign = block.state as Sign
            when (sign.getLine(0)) {
                "[土地販売]" -> LandPurchase().make(player, sign)
                "${ChatColor.YELLOW}[土地販売]" -> LandPurchase().buyGUI(player, sign)
            }
        } else if (item?.itemMeta?.displayName == "${ChatColor.YELLOW}エンダーチェスト容量UP") {
            e.isCancelled = true
            if (player.inventory.itemInMainHand != item) {
                return
            }
            EnderChest().update(player, plugin)
        } else if (block?.type == Material.BARREL && player.gameMode != GameMode.CREATIVE) {
            val barrel = block.state as Barrel
            if (barrel.customName != "admingift") {
                return
            }
            e.isCancelled = true
            if (Scoreboard().getValue("admingift", player.uniqueId.toString()) != 0) {
                AoringoEvents().onErrorEvent(player, "既にギフトを受け取っています")
                return
            }
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1f, 1f)
            Scoreboard().set("admingift", player.uniqueId.toString(), 1)
            for (barrelItem in barrel.inventory) {
                barrelItem ?: continue
                player.inventory.addItem(barrelItem)
            }
            player.sendMessage("${ChatColor.AQUA}Great gift for you!")
        } else if (itemName == "${ChatColor.RED}リンゴスクラッチ") {
            val clearItem = item.clone()
            clearItem.amount = 1
            player.inventory.removeItem(clearItem)
            Scratch().open(player, "${ChatColor.RED}リンゴスクラッチ")
        } else if (itemName == "${ChatColor.YELLOW}金リンゴスクラッチ") {
            val clearItem = item.clone()
            clearItem.amount = 1
            player.inventory.removeItem(clearItem)
            Scratch().open(player, "${ChatColor.YELLOW}金リンゴスクラッチ")
        } else if (itemName?.contains("[アプリケーション]") == true) {
            APK().add(player, itemName, plugin)
            e.isCancelled = true
        } else if (block?.type.toString().contains("WALL_SIGN") && itemName == "${ChatColor.GREEN}引き継ぎ書") {
            val sign = block?.state as Sign
            val lore = item.itemMeta?.lore
            if (sign.getLine(0) == "[Private]" && lore?.get(1) == player.uniqueId.toString()) {
                Handover().updateSign(sign, player, lore[0].replace("名前:", ""))
            }
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
        val player = e.player
        val entity = e.rightClicked
        if (entity.type != EntityType.ITEM_FRAME) {
            return
        }
        entity as ItemFrame
        val name = entity.customName
        val item = entity.item
        val block = entity.location.clone().add(0.0, -1.0, 0.0).block
        if (name?.contains("@Fshop") == true) {
            val owner = name.contains("userID:${player.uniqueId}")
            if (item.type == Material.AIR && owner) {
                player.sendMessage("${ChatColor.GREEN}販売開始")
            } else {
                e.isCancelled = true
                val index = name.indexOf("price:")
                val result = name.substring(index + 6, name.length)
                Fshop().buyGUI(player, item, result, entity.uniqueId.toString())
            }
        } else if (entity.customName == "まな板") {
            if (player.world.name != "event" && Job().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                AoringoEvents().onErrorEvent(player, "料理人のみ包丁を使用することができます")
                return
            }
            val mainItem = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                return
            }
            if (mainItem.type.toString().contains("SWORD")) {
                e.isCancelled = true
            }
            if (item.type == Material.AIR) {
                return
            }
            Cook().cut(item, player, entity)
            return
        } else if (item.itemMeta?.displayName == "衣") {
            e.isCancelled = true
            if (player.world.name != "event" && Job().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                AoringoEvents().onErrorEvent(player, "料理人のみ衣をつけることができます")
                return
            }
            entity.setItem(ItemStack(Material.AIR))
            Cook().dressing(player, entity)
        } else if (block.type == Material.SMOKER) {
            if (player.world.name != "event" && Job().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                AoringoEvents().onErrorEvent(player, "料理人のみコンロを使用することができます")
                return
            }
            val smoker = block.state as Smoker
            if (entity.item.type != Material.AIR) {
                return
            }
            if (smoker.burnTime.toInt() != 0) {
                e.isCancelled = true
                player.sendMessage("${ChatColor.RED}クールタイム中")
                return
            }
            Cook().bake(plugin, player, entity, smoker)
        } else if (item.itemMeta?.displayName == "${ChatColor.YELLOW}混ぜハンドル" && block.type == Material.BARREL) {
            if (player.world.name != "event" && Job().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                AoringoEvents().onErrorEvent(player, "料理人のみ混ぜることができます")
                return
            }
            player.world.playSound(player.location, Sound.BLOCK_BREWING_STAND_BREW, 1f, 1f)
            Cook().mix(player, block.state as Barrel)
            if (Random.nextInt(0, 100) != 0) {
                return
            }
            entity.setItem(ItemStack(Material.AIR))
            AoringoEvents().onErrorEvent(player, "ハンドルがぶっ壊れた")
        } else if (item.itemMeta?.displayName == "${ChatColor.RED}ポスト") {
            e.isCancelled = true
            if (!player.isSneaking) {
                Player().sendActionBar(player, "${ChatColor.RED}スニークでアイテム投下")
                return
            }
            if (player.inventory.itemInMainHand.type == Material.AIR) { return }
            val playerItem = player.inventory.itemInMainHand.clone()
            playerItem.amount = 1
            val direction: Vector = entity.location.direction.normalize()
            val blockBehindLocation: Location = entity.location.add(direction.multiply(-1))
            val blockBehind: org.bukkit.block.Block = blockBehindLocation.block
            if (blockBehind.type != Material.BARREL) { return }
            val barrel = blockBehind.state as Barrel
            barrel.inventory.addItem(playerItem)
            player.inventory.removeItem(playerItem)
            player.sendMessage("${ChatColor.GOLD}[ポスト]アイテムをポストに入れました")
        }
    }

    @EventHandler
    fun onHangingBreak(e: HangingBreakEvent) {
        val entity = e.entity
        val name = entity.customName
        if (entity is ItemFrame) {
            if (name?.contains("@Fshop") == true) {
                e.isCancelled = true
                entity.remove()
                entity.world.dropItem(entity.location, ItemStack(Material.OAK_SIGN))
            }
        }
        if (entity is ItemFrame && entity.location.clone().add(0.0, -1.0, 0.0).block.type == Material.SMOKER) {
            e.isCancelled = true
        } else if (entity.customName == "まな板") {
            e.isCancelled = true
            entity.remove()
            entity.world.dropItem(
                entity.location,
                Item().make(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "まな板", null, null, 1)
            )
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked
        if (player !is org.bukkit.entity.Player) {
            return
        }
        val gui = e.view
        val item = e.currentItem ?: return
        val title = gui.title
        if (gui.title.contains("${ChatColor.RED}ショップ購入:")) {
            e.isCancelled = true
            if (item.itemMeta?.displayName == "${ChatColor.GREEN}購入") {
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                val parts = gui.title.replace("${ChatColor.RED}ショップ購入:", "").split(",")
                val location = Location(player.world, parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
                val block = location.block
                val barrel = block.state as Barrel
                val count = item.amount
                BarrelShop().buy(
                    player,
                    (item.itemMeta!!.lore?.get(0)?.replace("円", "")?.toInt()?.times(count) ?: return),
                    barrel,
                    count
                )
            }
        } else if (gui.title.contains("@shop")) {
            if (player.isOp && player.gameMode == GameMode.CREATIVE) {
                return
            }
            if (gui.title != "${player.name}@shop") {
                e.isCancelled = true
                return
            }
            val shopItem = gui.getItem(0)?.clone()
            shopItem?.amount = 1
            val playerItem = item.clone()
            playerItem.amount = 1
            if (playerItem != shopItem && item.type != Material.AIR) {
                e.isCancelled = true
            }
            if (e.clickedInventory != player.inventory && e.slot == 0) {
                e.isCancelled = true
                val giveItem = item.clone()
                giveItem.amount = giveItem.amount - 1
                player.inventory.addItem(giveItem)
                item.amount = 1
            }
        } else if (gui.title.contains("${ChatColor.BLUE}Fショップ")) {
            e.isCancelled = true
            if (item.itemMeta?.displayName != "${ChatColor.GREEN}購入") {
                return
            }
            val meta = item.itemMeta ?: return
            val price = meta.lore?.get(0)?.replace("円", "")?.toInt()
            Fshop().buy(player, gui.getItem(3) ?: return, price ?: return, gui.title)
        }
        if (player.openInventory.topInventory != e.clickedInventory && player.openInventory.topInventory.type == InventoryType.WORKBENCH) {
            if (item?.hasItemMeta() == false) {
                return
            }
            e.isCancelled = true
            return
        }
        when (gui.title) {
            "${ChatColor.YELLOW}カスタム金床" -> Anvil().click(player, item ?: return, e)
            "${ChatColor.BLUE}職業選択" -> {
                e.isCancelled = true
                Job().change(player, item?.itemMeta?.displayName ?: return)
            }

            "${ChatColor.RED}エンチャント" -> {
                val book = gui.getItem(4)
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                if (item?.type != Material.BOOK && item?.type != null) {
                    e.isCancelled = true
                }
                if (item?.type == Material.ENCHANTING_TABLE && book?.type == Material.BOOK && !book.hasItemMeta() && book.amount == 1) {
                    Block().enchant(player, gui, plugin)
                }
            }

            "${ChatColor.BLUE}カゴ" -> {
                if (item?.itemMeta?.displayName == "${ChatColor.YELLOW}カゴ") {
                    e.isCancelled = true
                }
                val lore = item?.itemMeta?.lore?.get(0)
                if (lore?.contains("消費期限:") == true || item?.type == null) {
                    return
                }
                e.isCancelled = true
            }

            "${ChatColor.GOLD}クエスト" -> {
                e.isCancelled = true
                if (item?.itemMeta?.displayName != "${ChatColor.RED}辞退") {
                    return
                }
                Mission().reset(player)
            }
        }
        if (title.contains("@土地購入")) {
            e.isCancelled = true
            LandPurchase().buy(player, item ?: return, gui.title, plugin)
        } else if (title.contains("@土地設定")) {
            e.isCancelled = true
            val name = gui.title.replace("${ChatColor.BLUE}", "").replace("@土地設定", "")
            val money = item?.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt()
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            when (item?.itemMeta?.displayName) {
                "${ChatColor.GREEN}メンバー追加" -> LandPurchase().addMemberGUI(player, name)
                "${ChatColor.RED}メンバー削除" -> LandPurchase().removeMemberGUI(player, name)
                "${ChatColor.YELLOW}前払い" -> LandPurchase().advancePayment(player, name, money ?: return)
            }
        } else if (title.contains("@メンバー追加")) {
            e.isCancelled = true
            val name = gui.title.replace("${ChatColor.BLUE}", "").replace("@メンバー追加", "")
            WorldGuard().addMemberToRegion(name, Bukkit.getPlayer(item?.itemMeta?.displayName ?: return) ?: return)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            player.closeInventory()
        } else if (title.contains("@メンバー削除")) {
            e.isCancelled = true
            val name = gui.title.replace("${ChatColor.RED}", "").replace("@メンバー削除", "")
            WorldGuard().removeMember(name, item.itemMeta?.displayName ?: return, player.world)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            player.closeInventory()
        } else if (title.contains("${ChatColor.BLUE}保護設定") && item.itemMeta?.displayName == "${ChatColor.GREEN}作成") {
            e.isCancelled = true
            Smartphone().protection(player, item, title.replace("${ChatColor.BLUE}保護設定(", "").replace(")", ""))
        } else if (title == "${ChatColor.RED}リンゴスクラッチ" && e.clickedInventory != player.inventory) {
            e.isCancelled = true
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            val itemList = mutableListOf(
                Material.APPLE,
                Material.BARRIER,
                Material.BARRIER,
                Material.BARRIER,
                Material.BARRIER
            )
            val scratchItem = Scratch().click(itemList)
            if (item?.itemMeta?.displayName == "${ChatColor.RED}削る") {
                e.currentItem = scratchItem
            }
            if (Scratch().check(gui, Item().make(Material.PAPER, "${ChatColor.RED}削る", null, 7, 1)) <= 6) {
                Scratch().result(Scratch().check(gui, scratchItem) == 3, player, 10000)
            }
        } else if (title == "${ChatColor.YELLOW}金リンゴスクラッチ" && e.clickedInventory != player.inventory) {
            e.isCancelled = true
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            val itemList = mutableListOf(
                Material.GOLDEN_APPLE,
                Material.BARRIER
            )
            val scratchItem = Scratch().click(itemList)
            if (item?.itemMeta?.displayName == "${ChatColor.RED}削る") {
                e.currentItem = scratchItem
            }
            if (Scratch().check(gui, Item().make(Material.PAPER, "${ChatColor.RED}削る", null, 7, 1)) == 0) {
                Scratch().result(Scratch().check(gui, scratchItem) == 9, player, 1000000)
            }
        }
        when (gui.title) {
            "${ChatColor.BLUE}スマートフォン" -> {
                e.isCancelled = true
                Smartphone().clickItem(player, item ?: return, plugin, e.isShiftClick)
            }

            "${ChatColor.GREEN}資源テレポート" -> {
                e.isCancelled = true
                Resource().guiClick(player, item?.itemMeta?.displayName ?: return)
            }

            "${ChatColor.YELLOW}アイテム保護" -> {
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                when (item?.type) {
                    Material.RED_STAINED_GLASS_PANE -> e.isCancelled = true
                    Material.ANVIL -> {
                        e.isCancelled = true
                        gui.setItem(3, ItemProtection().chekcProtection(gui.getItem(3) ?: return, player))
                    }

                    else -> return
                }
            }

            "${ChatColor.YELLOW}OP用" -> {
                e.isCancelled = true
                Smartphone().opClick(item ?: return, plugin, e.isShiftClick, player)
            }

            "${ChatColor.BLUE}プレイヤー評価" -> {
                e.isCancelled = true
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                if (item?.type == Material.PLAYER_HEAD) {
                    if (item.itemMeta?.displayName == player.name) {
                        return
                    }
                    Evaluation().voidGUI(player, item)
                } else if (item?.type == Material.STONE_BUTTON) {
                    Evaluation().void(gui.getItem(2) ?: return, item.itemMeta?.displayName ?: return, player)
                }
            }

            "${ChatColor.YELLOW}WorldGuardGUI" -> {
                e.isCancelled = true
                Smartphone().wgClick(item ?: return, plugin, player, e.isShiftClick)
            }

            "${ChatColor.BLUE}スマートフォン(並び替え)" -> {
                if ((item?.type != Material.GREEN_CONCRETE || !item.hasItemMeta()) && item != null) {
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        val block = e.block
        if (block.type == Material.OAK_SIGN) {
            val sign = block.state as Sign
            if (sign.getLine(0) != "${ChatColor.GREEN}shop") {
                return
            }
            val barrel = block.location.add(0.0, -1.0, 0.0).block
            if (barrel.type != Material.BARREL) {
                return
            }
            val shop = barrel.state as Barrel
            shop.customName = null
            player.sendMessage("${ChatColor.AQUA}ショップを撤去しました")
            shop.update()
        }
        if (player.world.name == "shop") {
            return
        }
        if (block.type.toString()
            .contains("ORE") && Job().get(player) != "${ChatColor.GOLD}ハンター" && player.gameMode != GameMode.CREATIVE
        ) {
            e.isCancelled = true
            AoringoEvents().onErrorEvent(player, "${ChatColor.RED}ハンター以外は鉱石を掘ることができません")
        } else if (block.type == Material.SMOKER) {
            for (
                entity in block.world.getNearbyEntities(
                    block.location.clone().add(0.0, 1.0, 0.0),
                    0.5,
                    0.5,
                    0.5
                )
            ) {
                if (entity !is ItemFrame) {
                    continue
                }
                entity.remove()
            }
        } else if ((block.type == Material.GRASS || block.type == Material.TALL_GRASS) && Job().get(player) == "${ChatColor.GOLD}ハンター") {
            if (Random.nextInt(0, 3) != 0) {
                return
            }
            Job().giveVegetables(block.location)
        } else if (block.type == Material.WATER_CAULDRON || block.type == Material.CAULDRON) {
            val location = block.location.add(0.5, 0.0, 0.5)
            val armorStandList = Cook().findArmorStandsInRadius(location, 0.5)
            for (armorStand in armorStandList) {
                val playerItem = armorStand.equipment?.helmet ?: continue
                Item().drop(location, playerItem)
                armorStand.remove()
            }
        }
        when (block.type) {
            Material.WHEAT -> {
                e.isCancelled = true
                for (item in block.drops) {
                    if (item.type != Material.WHEAT) {
                        block.world.dropItem(block.location, item)
                    } else {
                        block.world.dropItem(
                            block.location,
                            Item().make(
                                Material.WHEAT,
                                "${ChatColor.GREEN}小麦",
                                Food().giveExpirationDate(14),
                                0,
                                1
                            )
                        )
                    }
                }
                block.type = Material.AIR
            }

            Material.CARROTS -> {
                e.isCancelled = true
                for (item in block.drops) {
                    if (item.type != Material.CARROT) {
                        block.world.dropItem(block.location, item)
                    } else {
                        block.world.dropItem(
                            block.location,
                            Item().make(
                                Material.CARROT,
                                "${ChatColor.GOLD}人参",
                                Food().giveExpirationDate(14),
                                0,
                                1
                            )
                        )
                    }
                }
                block.type = Material.AIR
            }

            Material.POTATOES -> {
                e.isCancelled = true
                for (item in block.drops) {
                    if (item.type != Material.POTATO) {
                        block.world.dropItem(block.location, item)
                    } else {
                        block.world.dropItem(
                            block.location,
                            Item().make(
                                Material.POTATO,
                                "${ChatColor.GOLD}じゃがいも",
                                Food().giveExpirationDate(14),
                                0,
                                1
                            )
                        )
                    }
                }
                block.type = Material.AIR
            }

            Material.BARREL -> {
                val barrel = block.state as Barrel
                val ngName = mutableListOf("admingift", "クエスト")
                if (player.gameMode == GameMode.CREATIVE) {
                    return
                }
                if (!ngName.contains(barrel.customName)) {
                    return
                }
                e.isCancelled = true
                AoringoEvents().onErrorEvent(player, "破壊禁止です")
            }
        }
        val blockCount = Scoreboard().getValue("blockCount", player.name)
        if (blockCount == 0) {
            Bukkit.getScheduler().runTaskLater(
                plugin,
                Runnable {
                    Scoreboard().set("blockCount", player.name, 0)
                },
                100L
            )
        }
        Scoreboard().add("blockCount", player.name, 1)
        val out = 110
        if (blockCount >= out) {
            val number = Random.nextInt(1, 9999)
            val message = "${ChatColor.RED}[アンチチート]チートを感知したためBANされました。 誤BANの場合は運営まで連絡をしてください(ナンバー$number)"
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.name, message, null, "AntiNuker")
            player.kickPlayer(message)
            Discord().setJson(player, "AntiCheatプラグイン", "https://static.wikia.nocookie.net/minecraft_ja_gamepedia/images/2/27/Barrier.gif/revision/latest?cb=20201228114801", "25500", "BAN", "5秒以内に${out}ブロック以上破壊したためBANされました(ナンバー$number)", PluginData.DataManager.serverlog ?: return)
        }
        if (player.isOp) { return }
        if (block.type == Material.OAK_SIGN) {
            val sign = block.state as Sign
            if (sign.getLine(0) != "${ChatColor.YELLOW}[土地販売]") { return }
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onCraftItem(e: CraftItemEvent) {
        val player = e.whoClicked
        if (player !is org.bukkit.entity.Player) {
            return
        }
        val item = e.currentItem
        if (item?.type == Material.FERMENTED_SPIDER_EYE) {
            e.currentItem =
                Item().make(Material.FERMENTED_SPIDER_EYE, "${ChatColor.GOLD}発酵した蜘蛛の目", null, null, 1)
        }
        if (item?.itemMeta?.displayName?.contains("包丁") == true) {
            e.currentItem = Cook().knifeSharpness(item)
        }
        if (Job().get(player) == "${ChatColor.GRAY}鍛冶屋") {
            return
        }
        if (!Job().tool().contains(item?.type) && item?.hasItemMeta() == false) {
            return
        }
        if (item?.type == Material.HOPPER) {
            e.isCancelled = true
            AoringoEvents().onErrorEvent(player, "このアイテムをクラフトすることは禁止されています")
        } else if (item?.type == Material.WRITTEN_BOOK && item.itemMeta?.displayName?.contains("${ChatColor.RED}契約本@") == true) {
            e.currentItem = Contract().copyBlock(item, player)
        }

        e.isCancelled = true
        AoringoEvents().onErrorEvent(player, "${ChatColor.RED}鍛冶屋以外はツールをクラフトすることができません")
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val player = e.player as org.bukkit.entity.Player
        val gui = e.view
        when (gui.title) {
            "${ChatColor.YELLOW}カスタム金床" -> Anvil().close(gui, player as org.bukkit.entity.Player)
            "${ChatColor.RED}エンチャント" -> {
                val item = gui.getItem(4) ?: return
                if (item.type == Material.ENCHANTED_BOOK) {
                    return
                }
                player.inventory.addItem(item)
            }

            "${ChatColor.BLUE}カゴ" -> {
                Cage().clone(player, gui)
                player.playSound(player, Sound.BLOCK_CHEST_CLOSE, 1f, 1f)
            }
            "${ChatColor.YELLOW}アイテム保護" -> player.inventory.addItem(gui.getItem(3) ?: return)
            "${ChatColor.BLUE}スマートフォン(並び替え)" -> APK().setSort(player, gui, plugin)
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val player = e.player
        val block = e.block
        when (block.type) {
            Material.SMOKER -> {
                if (Job().get(player) != "${ChatColor.YELLOW}料理人") {
                    e.isCancelled = true
                    AoringoEvents().onErrorEvent(player, "使えるのは料理人だけです")
                    return
                }
                Cook().furnace(block)
            }

            else -> {}
        }
        if (block.type == Material.HOPPER) {
            e.isCancelled = true
            AoringoEvents().onErrorEvent(player, "ホッパーを設置することは禁止されています")
        }
    }

    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        val player = e.player
        val hook = e.hook
        if (e.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return
        }
        // プレイヤーが魚を釣り上げた場合
        e.caught?.remove()
        hook.world.dropItem(player.location, Job().givefish(player))
    }

    @EventHandler
    fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
        val player = e.player
        val item = e.item
        val itemType = item.type

        if (Food().isExpirationDate(player, item)) {
            Food().lowered(player, item)
        }
        if ((itemType == Material.POTION || itemType == Material.PUFFERFISH || itemType == Material.SPIDER_EYE || itemType == Material.MILK_BUCKET) && !item.hasItemMeta()) {
            return
        }
        e.isCancelled = true
        Food().eat(player, item)
    }

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        val entity = e.entity
        when (entity.type) {
            EntityType.COW -> Food().dropReplacement(e, Material.BEEF, Food().makeItem("${ChatColor.RED}牛肉", 78))
            EntityType.SHEEP -> Food().dropReplacement(
                e,
                Material.MUTTON,
                Food().makeItem("${ChatColor.RED}羊肉", 79)
            )

            EntityType.PIG -> Food().dropReplacement(
                e,
                Material.PORKCHOP,
                Food().makeItem("${ChatColor.RED}豚肉", 81)
            )

            EntityType.CHICKEN -> Food().dropReplacement(
                e,
                Material.CHICKEN,
                Food().makeItem("${ChatColor.RED}鶏肉", 80)
            )

            else -> {}
        }
    }

    @EventHandler
    fun onEntityDropItem(e: EntityDropItemEvent) {
        val entity = e.entity
        if (entity.type == EntityType.CHICKEN && e.itemDrop.itemStack.type == Material.EGG) {
            e.isCancelled = true
            e.itemDrop.world.dropItem(
                e.itemDrop.location,
                Item().make(Material.EGG, "卵", Food().giveExpirationDate(14), null, 1)
            )
        }
    }

    @EventHandler
    fun onEntitySpawn(e: EntitySpawnEvent) {
        val entity = e.entity
        val world = entity.world.name
        if (entity.type == EntityType.ARROW && (world == "shop" || world == "Home" || world == "testworld" || world == "world")) {
            e.isCancelled = true
        }
        if (entity is Villager && (world == "Survival" || world == "Home")) {
            entity.remove()
        } else if (entity is Wither && (world != "Survival")) {
            entity.remove()
            val dropItems = mutableListOf(Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.SOUL_SAND, Material.SOUL_SAND, Material.SOUL_SAND, Material.SOUL_SAND)
            for (item in dropItems) {
                entity.world.dropItem(entity.location, ItemStack(item))
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        val player = e.damager
        val entity = e.entity
        val damage = e.damage
        if (entity !is Mob) {
            return
        }
        if (player !is org.bukkit.entity.Player) {
            return
        }
        if (entity.world.name != "event" && entity is org.bukkit.entity.Player) {
            e.isCancelled = true
            return
        } else if (player is org.bukkit.entity.Player) {
            val health = entity.health - damage
            if (entity is Villager) {
                e.isCancelled = true
            }
            Player().sendActionBar(player, "${ChatColor.RED}" + if (health < 0) { "0" } else { health.toInt() } + "HP")
        }
        val power = Scoreboard().getValue("status_Power", player.uniqueId.toString())
        entity.damage(power * 0.1)
    }

    @EventHandler
    fun onPlayerChangedWorld(e: PlayerChangedWorldEvent) {
        val player = e.player
        Player().setTab(player)
    }

    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        val player = e.player
        val world = player.world
        if (world.name == "Survival") {
            e.respawnLocation = Bukkit.getWorld("world")?.spawnLocation ?: return
        }
        Bukkit.getScheduler().runTaskLater(
            plugin,
            Runnable {
                player.foodLevel = 6
            },
            20L
        ) // 20Lは1秒を表す（1秒 = 20ticks）
    }

    @EventHandler
    fun onPlayerPortal(e: PlayerPortalEvent) {
        e.isCancelled = true
        val player = e.player
        AoringoEvents().onErrorEvent(player, "${ChatColor.RED}ポータルの使用は禁止されております")
    }

    @EventHandler
    fun onPlayerToggleSneak(e: PlayerToggleSneakEvent) {
        if (!e.isSneaking) {
            return
        }
        val player = e.player
        val block = player.location.clone().add(0.0, -1.0, 0.0).block
        val downBlock = player.location.clone().add(0.0, -2.0, 0.0).block
        if (downBlock.type == Material.COMMAND_BLOCK) {
            when (block.type) {
                Material.IRON_BLOCK -> Resource().openTpGUI(player)
                Material.QUARTZ_BLOCK -> {
                    player.teleport(
                        Bukkit.getWorld(
                            if (player.world.name == "world") {
                                "shop"
                            } else {
                                "world"
                            }
                        )?.spawnLocation!!
                    )
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                }

                Material.GOLD_BLOCK -> {
                    player.teleport(
                        Bukkit.getWorld(
                            if (player.world.name == "world") {
                                "Home"
                            } else {
                                "world"
                            }
                        )?.spawnLocation!!
                    )
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                }

                else -> return
            }
        }
    }

    @EventHandler
    fun onBlockDispense(e: BlockDispenseEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onAsyncPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val chat = e.message
        if (chat.contains("@")) {
            player.sendMessage("${ChatColor.RED}メッセージに@を入れることは禁止されています")
            e.isCancelled = true
        } else if (chat.contains("!契約")) {
            e.isCancelled = true
            if (player.inventory.itemInMainHand.amount != 1) {
                AoringoEvents().onErrorEvent(player, "アイテムを1つのみ持ってください")
                return
            }
            when (player.inventory.itemInMainHand.itemMeta?.displayName) {
                "${ChatColor.YELLOW}契約書[未記入]" -> Contract().request(player, chat)
                "${ChatColor.YELLOW}契約書[契約待ち]" -> Contract().contract(player, chat)
            }
        } else if (player.scoreboardTags.contains("rg")) {
            e.isCancelled = true
            player.removeScoreboardTag("rg")
            Bukkit.getScheduler().runTask(
                plugin,
                Runnable {
                    Smartphone().protectionGUI(player, chat)
                }
            )
        }
    }
    @EventHandler
    fun onBlockPistonEvent(e: BlockPistonExtendEvent) {
        e.isCancelled = true
    }
    @EventHandler
    fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
        val command = e.message
        val player = e.player
        if (command.contains("/rg") && !player.isOp) {
            e.isCancelled = true
        }
    }
    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        val player = e.player
        val item = e.itemDrop
        if (ItemProtection().isProtection(item.itemStack)) {
            e.isCancelled = true
            AoringoEvents().onErrorEvent(player, "[アイテム保護]保護アイテムを捨てることはできません")
        }
    }
}
