package com.github.Ringoame196

import com.github.Ringoame196.Blocks.Block
import com.github.Ringoame196.Data.Company
import com.github.Ringoame196.Data.ItemData
import com.github.Ringoame196.Data.PluginData
import com.github.Ringoame196.Data.WorldGuard
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Entity.ArmorStand
import com.github.Ringoame196.Items.Cookware.ChoppingBoard
import com.github.Ringoame196.Items.Cookware.FryBatter
import com.github.Ringoame196.Items.Cookware.Fryer
import com.github.Ringoame196.Items.Cookware.GasBurner
import com.github.Ringoame196.Items.Cookware.Pot
import com.github.Ringoame196.Items.FoodManager
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Items.WrittenBook
import com.github.Ringoame196.Job.JobManager
import com.github.Ringoame196.Shop.Fshop
import com.github.Ringoame196.Smartphone.APKs.ItemProtectionApplication
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
import com.github.Ringoame196.Smartphones.Applications.ClosingApplication
import com.github.Ringoame196.Smartphones.Applications.PlayerRatingApplication
import com.github.Ringoame196.Smartphones.Applications.SortApplication
import com.github.Ringoame196.Smartphones.Smartphone
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
import java.util.UUID
import kotlin.random.Random

class Events(private val plugin: Plugin) : Listener {
    private val moneyUseCase = MoneyUseCase()
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        val aoringoPlayer = AoringoPlayer(player)
        val participatedTags = "member"
        if (!player.scoreboardTags.contains(participatedTags)) {
            e.joinMessage = "${ChatColor.YELLOW}${player.name}さんが初めてサーバーに参加しました"
            aoringoPlayer.fastJoin()
        }
        aoringoPlayer.setPlayer(plugin)
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val aoringoPlayer = AoringoPlayer(player)
        val item = e.item ?: ItemStack(Material.AIR)
        val playerItem = player.inventory.itemInMainHand
        val itemName = item.itemMeta?.displayName ?: ""
        val block = e.clickedBlock
        val downBlock = block?.location?.clone()?.add(0.0, -1.0, 0.0)?.block
        if (e.action == Action.LEFT_CLICK_BLOCK) { return }
        if (e.action == Action.LEFT_CLICK_AIR) { return }
        if (item != playerItem && item.type != Material.AIR) { return }
        when (block?.type) {
            Material.OAK_SIGN -> {
                val sign = block.state as Sign
                when (sign.getLine(0)) {
                    "Fshop" -> {
                        e.isCancelled = true
                        aoringoPlayer.makeShop(sign)
                        sign.block.type = Material.AIR
                    }
                    "[土地販売]" -> {
                        if (!player.isOp) { return }
                        aoringoPlayer.makeLandPurchase(sign)
                    }
                    "${ChatColor.YELLOW}[土地販売]" -> LandPurchase().buyGUI(player, sign)
                }
            }
            Material.ANVIL, Material.DAMAGED_ANVIL -> {
                if (player.gameMode == GameMode.CREATIVE) { return }
                e.isCancelled = true
                aoringoPlayer.useAnvil()
            }
            Material.SMITHING_TABLE -> {
                if (JobManager().get(player) == "${ChatColor.GRAY}鍛冶屋") { return }
                e.isCancelled = true
                aoringoPlayer.sendErrorMessage("${ChatColor.RED}鍛冶屋以外は使用することができません")
            }
            Material.SMOKER -> e.isCancelled = true
            Material.LAVA_CAULDRON -> {
                e.isCancelled = true
                Fryer().deepFry(player, block, item, plugin)
            }
            Material.ENCHANTING_TABLE -> {
                e.isCancelled = true
                aoringoPlayer.useEnchantingTable()
            }
            Material.BARREL -> {
                val barrel = block.state as Barrel
                if (player.gameMode == GameMode.CREATIVE) {
                    return
                }
                when (barrel.customName) {
                    "admingift" -> {
                        e.isCancelled = true
                        ItemManager().giveBarrelGift(player, barrel, "admingift")
                    }
                }
            }
            else -> {}
        }
        when (itemName) {
            "職業スター" -> player.openInventory(JobManager().makeSelectGUI())
            "まな板" -> {
                e.isCancelled = true
                val upBlock = block?.location?.clone()?.add(0.0, 1.0, 0.0)?.block
                if (upBlock?.type != Material.AIR) { return }
                ChoppingBoard().summonChoppingBoard(block)
                ItemManager().reduceMainItem(player)
            }
            "${ChatColor.YELLOW}おたま" -> {
                if (block?.type != Material.BARREL) { return }
                if (downBlock?.type != Material.CAMPFIRE) { return }
                e.isCancelled = true
                Pot().boil(block, player, plugin)
                ItemManager().breakLadle(player)
            }
            "${ChatColor.YELLOW}カゴ" -> {
                e.isCancelled = true
                player.openInventory(Cage().createGUi(playerItem))
                player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
            }
            "${ChatColor.YELLOW}スマートフォン" -> player.openInventory(Smartphone().createGUI(plugin, player))
            "${ChatColor.RED}リンゴスクラッチ", "${ChatColor.YELLOW}金リンゴスクラッチ" -> {
                ItemManager().reduceMainItem(player)
                player.openInventory(Scratch().createGUI(itemName))
            }
            "${ChatColor.RED}会社情報本" -> {
                if (!ItemProtectionApplication().isPlayerProtection(item?:return, player)) {
                    aoringoPlayer.sendErrorMessage("会社情報本を使うには、アイテムを保護を設定する必要があります")
                } else {
                    player.openInventory(Company().createGUI())
                }
            }
            "${ChatColor.YELLOW}エンダーチェスト容量UP" -> {
                e.isCancelled = true
                aoringoPlayer.upDataEnderChestLevel(plugin)
            }
            "${ChatColor.YELLOW}契約書[未記入]" -> {
                player.sendMessage("${ChatColor.YELLOW}契約書を発行するには [!契約 (値段)]")
            }
            "${ChatColor.YELLOW}契約書[契約待ち]" -> {
                player.sendMessage("${ChatColor.YELLOW}契約書を完了するには [!契約 (契約書に書かれているお金)]")
            }
            "${ChatColor.YELLOW}送金申込書[未記入]" -> {
                player.sendMessage("${ChatColor.YELLOW}[送金申し込み書メニュー]")
                player.sendMessage("${ChatColor.AQUA}!送金 プレイヤー [送金先のプレイヤー名](※プレイヤーに送金する場合)")
                player.sendMessage("${ChatColor.AQUA}!送金 口座 [口座名](※プレイヤー以外に送金する場合)")
                player.sendMessage("${ChatColor.AQUA}!送金 金額 [値段]")
                player.sendMessage("${ChatColor.AQUA}!送金 口座登録")
                player.sendMessage("${ChatColor.YELLOW}上のメッセージを本を持った状態でチャットしてください")
            }
        }
        if (block?.type == Material.BEE_NEST || block?.type == Material.BEEHIVE) {
            if (item.type != Material.GLASS_BOTTLE) { return }
            e.isCancelled = true
            val beeNest = block.blockData as org.bukkit.block.data.type.Beehive
            if (beeNest.honeyLevel != 5 || player.inventory.itemInMainHand.type != Material.GLASS_BOTTLE) {
                return
            }
            beeNest.honeyLevel = 0
            e.clickedBlock!!.blockData = beeNest
            player.inventory.addItem(ItemManager().make(Material.HONEY_BOTTLE, "${ChatColor.GOLD}ハチミツ", FoodManager().makeExpirationDate(14),))
            ItemManager().reduceMainItem(player)
        }
        if (item.type == Material.EMERALD) {
            item.itemMeta?.customModelData.let {
                val money = itemName.replace("${ChatColor.GREEN}", "").replace("円", "").toInt()
                if (money == 0) {
                    return
                }
                val totalAmount = item.amount * money
                moneyUseCase.addMoney(aoringoPlayer, totalAmount)
                player.inventory.remove(item)
            }
        } else if (itemName.contains("${ChatColor.RED}契約本")) {
            if (player.isSneaking) {
                Contract().returnMoney(player)
            } else {
                aoringoPlayer.sendActionBar("お金を受け取るにはシフトをしてください")
            }
        } else if (itemName.contains("[アプリケーション]")) {
            ApplicationManager().install(player, itemName, plugin)
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
        val player = e.player
        val entity = e.rightClicked
        if (entity.type != EntityType.ITEM_FRAME) {
            return
        }
        val itemFrame = entity as ItemFrame
        val aoringoPlayer = AoringoPlayer(player)
        val name = entity.customName ?: ""
        val item = entity.item
        val itemName = item.itemMeta?.displayName
        val block = entity.location.clone().add(0.0, -1.0, 0.0).block
        if (name.contains("@Fshop")) {
            val fshop = Fshop(itemFrame)
            player.sendMessage(itemFrame.uniqueId)
            if (item.type == Material.AIR && fshop.isOwner(player)) {
                player.sendMessage("${ChatColor.GREEN}販売開始")
            } else {
                e.isCancelled = true
                if (item.type == Material.AIR) {
                    aoringoPlayer.sendErrorMessage("売り物が設定されていません 土地のオーナー または メンバーのみ売り物を設定可能です")
                    return
                }
                player.openInventory(fshop.makeBuyGUI(item))
            }
        } else if (name == "まな板") {
            val choppingBoard = ChoppingBoard()
            if (JobManager().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                aoringoPlayer.sendErrorMessage("料理人のみ包丁を使用することができます")
                return
            }
            val mainItem = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                return
            }
            if (mainItem.type.toString().contains("SWORD")) {
                e.isCancelled = true
            }
            choppingBoard.cutFoods(item, player, entity)
            return
        }
        when (itemName) {
            "衣" -> {
                e.isCancelled = true
                if (JobManager().get(player) != "${ChatColor.YELLOW}料理人") {
                    aoringoPlayer.sendErrorMessage("料理人のみ衣をつけることができます")
                    return
                }
                itemFrame.setItem(ItemStack(Material.AIR))
                FryBatter().dressing(player, entity)
            }
            "${ChatColor.YELLOW}混ぜハンドル" -> {
                if (block.type != Material.BARREL) { return }
                if (JobManager().get(player) != "${ChatColor.YELLOW}料理人") {
                    e.isCancelled = true
                    aoringoPlayer.sendErrorMessage("料理人のみ混ぜることができます")
                    return
                }
                player.playSound(player, Sound.BLOCK_BREWING_STAND_BREW, 1f, 1f)
                CookManager().mix(player, block.state as Barrel)
                ItemManager().breakHandle(itemFrame, aoringoPlayer)
            }
            "${ChatColor.RED}ポスト" -> {
                e.isCancelled = true
                if (!player.isSneaking) {
                    aoringoPlayer.sendActionBar("${ChatColor.RED}スニークでアイテム投下")
                    return
                }
                if (player.inventory.itemInMainHand.type == Material.AIR) { return }
                val direction: Vector = itemFrame.location.direction.normalize()
                val blockBehindLocation: Location = itemFrame.location.add(direction.multiply(-1))
                val blockBehind: org.bukkit.block.Block = blockBehindLocation.block
                if (blockBehind.type != Material.BARREL) { return }
                val barrel = blockBehind.state as Barrel
                aoringoPlayer.putItemInPost(barrel)
            }
        }
        if (block.type == Material.SMOKER) {
            if (JobManager().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                aoringoPlayer.sendErrorMessage("料理人のみコンロを使用することができます")
                return
            }
            val smoker = block.state as Smoker
            val gasBurner = GasBurner()
            if (item.type != Material.AIR) {
                return
            }
            val smokerTime = smoker.burnTime.toInt()
            if (smokerTime != 0) {
                e.isCancelled = true
                player.sendMessage("${ChatColor.RED}クールタイム中")
                return
            }
            gasBurner.bakingFoods(plugin, player, entity, smoker)
        }
    }

    @EventHandler
    fun onHangingBreak(e: HangingBreakEvent) {
        val entity = e.entity
        val name = entity.customName ?: ""
        val location = entity.location
        val downBlock = location.clone().add(0.0, -1.0, 0.0).block
        if (entity !is ItemFrame) { return }
        if (name.contains("@Fshop")) {
            e.isCancelled = true
            entity.remove()
            entity.world.dropItem(location, ItemStack(Material.OAK_SIGN))
        }
        if (downBlock.type == Material.SMOKER) {
            e.isCancelled = true
        } else if (entity.customName == "まな板") {
            e.isCancelled = true
            entity.remove()
            entity.world.dropItem(
                entity.location,
                ItemManager().make(material = Material.HEAVY_WEIGHTED_PRESSURE_PLATE, name = "まな板")
            )
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as? org.bukkit.entity.Player ?: return
        val aoringoPlayer = AoringoPlayer(player)
        val gui = e.view
        val item = e.currentItem ?: return
        val itemName = item.itemMeta?.displayName
        val title = gui.title
        val playerOpenInventory = player.openInventory.topInventory
        if (playerOpenInventory != e.clickedInventory) {
            if (playerOpenInventory.type != InventoryType.WORKBENCH) {
                return
            }
            if (!item.hasItemMeta()) {
                return
            }
            e.isCancelled = true
            return
        }
        when (gui.title) { // インスタンスできるかも？
            "${ChatColor.YELLOW}カスタム金床" -> Anvil().click(player, item, e)
            "${ChatColor.BLUE}職業選択" -> {
                e.isCancelled = true
                JobManager().change(player, item.itemMeta?.displayName ?: return)
            }
            "${ChatColor.BLUE}ヘルスケア" -> {
                e.isCancelled = true
            }
            "${ChatColor.RED}エンチャント" -> {
                val book = gui.getItem(4) ?: return
                if (book.type != Material.BOOK) { return }
                if (book.hasItemMeta()) { return }
                if (book.amount != 1) { return }
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                when (item.type) {
                    Material.BOOK -> {}
                    Material.ENCHANTING_TABLE -> Block().giveEnchantBook(player, gui, plugin)
                    else -> e.isCancelled = true
                }
            }

            "${ChatColor.BLUE}カゴ" -> {
                if (itemName == "${ChatColor.YELLOW}カゴ") {
                    e.isCancelled = true
                }
                val lore = item.itemMeta?.lore?.get(0) ?: ""
                if (lore.contains("消費期限:")) {
                    return
                }
                e.isCancelled = true
            }
            "${ChatColor.BLUE}Fショップ" -> {
                e.isCancelled = true
                val shopInfoSlot = 0
                val shopUUIDinfoNumber = 0
                val shopInfo = gui.getItem(shopInfoSlot) ?: return
                val shopUUID = shopInfo.itemMeta?.lore?.get(shopUUIDinfoNumber) ?: return
                val shop = Bukkit.getEntity(UUID.fromString(shopUUID))
                if (shop !is ItemFrame) { return }
                val fshop = Fshop(shop)
                if (itemName == "${ChatColor.GREEN}購入") {
                    val goodsSlot = 3
                    fshop.buy(aoringoPlayer, gui.getItem(goodsSlot) ?: return,)
                }
            }
            "${ChatColor.BLUE}スマートフォン" -> {
                e.isCancelled = true
                Smartphone().startUpAKS(player, item, plugin, e.isShiftClick)
            }

            "${ChatColor.GREEN}資源テレポート" -> {
                e.isCancelled = true
                Resource().guiClick(player, item.itemMeta?.displayName ?: return)
            }

            "${ChatColor.YELLOW}アイテム保護" -> {
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                when (item.type) {
                    Material.RED_STAINED_GLASS_PANE -> e.isCancelled = true
                    Material.ANVIL -> {
                        e.isCancelled = true
                        gui.setItem(3, ItemProtectionApplication().chekcProtection(gui.getItem(3) ?: return, player))
                    }
                    else -> return
                }
            }

            "${ChatColor.YELLOW}OP用" -> {
                e.isCancelled = true
                Smartphone().opClick(item, plugin, e.isShiftClick, player)
            }

            "${ChatColor.BLUE}プレイヤー評価" -> {
                val playerRatingApplication = PlayerRatingApplication()
                e.isCancelled = true
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                if (item.type == Material.PLAYER_HEAD) {
                    if (itemName != player.name) {
                        playerRatingApplication.voidGUI(player, item)
                    }
                } else if (item.type == Material.STONE_BUTTON) {
                    playerRatingApplication.void(gui.getItem(2) ?: return, item.itemMeta?.displayName ?: return, player)
                }
            }

            "${ChatColor.YELLOW}WorldGuardGUI" -> {
                e.isCancelled = true
                Smartphone().wgClick(item ?: return, plugin, player, e.isShiftClick)
            }

            "${ChatColor.BLUE}スマートフォン(並び替え)" -> {
                if (item.type != Material.GREEN_CONCRETE || !item.hasItemMeta()) {
                    e.isCancelled = true
                }
            }
        }
        if (title.contains("@土地購入")) {
            e.isCancelled = true
            LandPurchase().buy(player, item ?: return, gui.title, plugin)
        } else if (title.contains("@土地設定")) {
            e.isCancelled = true
            val name = title.replace("${ChatColor.BLUE}", "").replace("@土地設定", "")
            val money = item.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt()
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            when (itemName) {
                "${ChatColor.GREEN}メンバー追加" -> LandPurchase().addMemberGUI(player, name)
                "${ChatColor.RED}メンバー削除" -> LandPurchase().removeMemberGUI(player, name)
                "${ChatColor.YELLOW}前払い" -> LandPurchase().advancePayment(player, name, money ?: return)
            }
        } else if (title.contains("@メンバー追加")) {
            e.isCancelled = true
            val name = gui.title.replace("${ChatColor.BLUE}", "").replace("@メンバー追加", "")
            WorldGuard().addMemberToRegion(name, Bukkit.getPlayer(item.itemMeta?.displayName ?: return) ?: return)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            player.closeInventory()
        } else if (title.contains("@メンバー削除")) {
            e.isCancelled = true
            val name = gui.title.replace("${ChatColor.RED}", "").replace("@メンバー削除", "")
            WorldGuard().reduceMember(name, item.itemMeta?.displayName ?: return, player.world)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            player.closeInventory()
        } else if (title.contains("${ChatColor.BLUE}保護設定") && itemName == "${ChatColor.GREEN}作成") {
            e.isCancelled = true
            Smartphone().protection(player, item, title.replace("${ChatColor.BLUE}保護設定(", "").replace(")", ""))
        } else if (title == "${ChatColor.RED}リンゴスクラッチ" && e.clickedInventory != player.inventory) {
            e.isCancelled = true
            if (item.itemMeta?.displayName != "${ChatColor.RED}削る") { return }
            val itemList = mutableListOf(
                Material.APPLE,
                Material.BARRIER,
                Material.BARRIER,
                Material.BARRIER,
                Material.BARRIER
            )
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            val scratchItem = Scratch().click(itemList)
            e.currentItem = scratchItem
            if (Scratch().countItem(gui, ItemManager().make(Material.PAPER, "${ChatColor.RED}削る", customModelData = 7)) <= 6) {
                Scratch().result(Scratch().countItem(gui, scratchItem) == 3, player, 10000)
            }
        } else if (title == "${ChatColor.YELLOW}金リンゴスクラッチ" && e.clickedInventory != player.inventory) {
            e.isCancelled = true
            if (item.itemMeta?.displayName != "${ChatColor.RED}削る") { return }
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            val itemList = mutableListOf(
                Material.GOLDEN_APPLE,
                Material.BARRIER
            )
            val scratchItem = Scratch().click(itemList)
            e.currentItem = scratchItem
            if (Scratch().countItem(gui, ItemManager().make(Material.PAPER, "${ChatColor.RED}削る", customModelData = 7)) == 0) {
                Scratch().result(Scratch().countItem(gui, scratchItem) == 9, player, 1000000)
            }
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        val block = e.block
        val worldName = player.world.name
        val aoringoPlayer = AoringoPlayer(player)
        if (player.gameMode == GameMode.CREATIVE) { return }
        if (worldName == "shop" || worldName == "world") {
            return
        }
        if (worldName == "dungeon") {
            when (block.type) {
                Material.OBSIDIAN -> ArmorStand().summonMarker(block.location, " ").addScoreboardTag("breakObsidian")
                Material.FIRE -> {}
                else -> {
                    aoringoPlayer.sendErrorMessage("黒曜石以外破壊禁止されています")
                    e.isCancelled = true
                }
            }
        } else if (block.type.toString().contains("ORE")) {
            if (JobManager().get(player) == "${ChatColor.GOLD}ハンター") { return }
            e.isCancelled = true
            aoringoPlayer.sendErrorMessage("${ChatColor.RED}ハンター以外は鉱石を掘ることができません")
        }
        when (block.type) {
            Material.GRASS, Material.TALL_GRASS -> {
                if (WorldGuard().getOwnerOfRegion(player.location) != null) { return }
                if (JobManager().get(player) != "${ChatColor.GOLD}ハンター") { return }
                val probability = 3
                if (Random.nextInt(0, probability) != 0) { return }
                JobManager().giveVegetables(block.location)
            }
            Material.WHEAT, Material.CARROTS, Material.POTATOES -> {
                e.isCancelled = true
                aoringoPlayer.breakVegetables(block)
            }
            Material.OAK_SIGN -> {
                val sign = block.state as Sign
                if (sign.getLine(0) != "${ChatColor.YELLOW}[土地販売]") { return }
                e.isCancelled = true
            }
            Material.SMOKER -> GasBurner().breakGusBurner(block)
            else -> {}
        }
    }

    @EventHandler
    fun onCraftItem(e: CraftItemEvent) {
        val player = e.whoClicked
        if (player !is org.bukkit.entity.Player) { return }
        val item = e.currentItem ?: return
        val type = item.type
        val displayName = item.itemMeta?.displayName ?: ""
        val aoringoPlayer = AoringoPlayer(player)
        val ngItem = mutableListOf(Material.HOPPER, Material.TNT)
        val itemManagerClass = ItemManager()
        val job = JobManager()
        if (type == Material.FERMENTED_SPIDER_EYE) {
            e.currentItem = itemManagerClass.make(Material.FERMENTED_SPIDER_EYE, "${ChatColor.GOLD}発酵した蜘蛛の目")
        } else if (displayName.contains("包丁")) {
            e.currentItem = CookManager().knifeSharpness(item)
        } else if (displayName.contains("契約書")) {
            e.currentItem = itemManagerClass.copyBlock(item, player)
        } else if (ngItem.contains(type)) {
            e.isCancelled = true
            aoringoPlayer.sendErrorMessage("このアイテムをクラフトすることは禁止されています")
        } else if (type == Material.WRITTEN_BOOK && displayName.contains("${ChatColor.RED}契約本@")) {
            e.currentItem = Contract().copyBlock(item, player)
        }
        if (job.get(player) == "${ChatColor.GRAY}鍛冶屋") {
            return
        }
        if (job.tool.contains(type)) {
            e.isCancelled = true
            aoringoPlayer.sendErrorMessage("${ChatColor.RED}鍛冶屋以外はツールをクラフトすることができません")
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val player = e.player as org.bukkit.entity.Player
        val gui = e.view
        val application = mapOf<String, ClosingApplication>(
            "${ChatColor.YELLOW}アイテム保護" to ItemProtectionApplication(),
            "${ChatColor.BLUE}スマートフォン(並び替え)" to SortApplication()
        )
        when (gui.title) {
            "${ChatColor.YELLOW}カスタム金床" -> Anvil().returnItemFromPlayer(gui, player)
            "${ChatColor.RED}エンチャント" -> {
                val item = gui.getItem(4) ?: return
                if (item.type != Material.ENCHANTED_BOOK) {
                    player.inventory.addItem(item)
                }
            }

            "${ChatColor.BLUE}カゴ" -> {
                Cage().clone(player, gui)
                player.playSound(player, Sound.BLOCK_CHEST_CLOSE, 1f, 1f)
            }
        }
        application[gui.title]?.close(player, gui, plugin) ?: return
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val player = e.player
        val block = e.block
        val aoringoPlayer = AoringoPlayer(player)
        when (block.type) {
            Material.SMOKER -> {
                val gasBurner = GasBurner()
                if (JobManager().get(player) == "${ChatColor.YELLOW}料理人") {
                    gasBurner.summonIronPlate(block)
                } else {
                    e.isCancelled = true
                    aoringoPlayer.sendErrorMessage("使えるのは料理人だけです")
                }
            }
            Material.HOPPER -> {
                e.isCancelled = true
                aoringoPlayer.sendErrorMessage("ホッパーを設置することは禁止されています")
            }

            else -> {}
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
        hook.world.dropItem(player.location, JobManager().givefish(player))
    }

    @EventHandler
    fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
        val player = e.player
        val item = e.item
        val itemType = item.type
        val foodManager = FoodManager()
        val hiddenFoodLevel = 20.0f

        if (foodManager.isExpirationDateHasExpired(player, item)) {
            foodManager.giveDiarrheaEffect(player)
        }
        if ((itemType == Material.PUFFERFISH || itemType == Material.SPIDER_EYE || itemType == Material.MILK_BUCKET) && !item.hasItemMeta()) {
            return
        }
        ItemManager().reduceOneItem(player, item)
        e.isCancelled = true
        player.foodLevel = foodManager.calculateFoodLevel(player, item)
        player.saturation = hiddenFoodLevel // 隠し満腹度
        foodManager.increaseStatus(player, item)
    }

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        val entity = e.entity
        val dropITem = ItemData().getEntityDropItem(entity.type) ?: return
        FoodManager().dropReplacement(e, dropITem.material, FoodManager().makeItem(dropITem.displayName, dropITem.customModelData))
    }

    @EventHandler
    fun onEntityDropItem(e: EntityDropItemEvent) {
        val entity = e.entity
        if (entity.type == EntityType.CHICKEN && e.itemDrop.itemStack.type == Material.EGG) {
            val term = 14
            e.isCancelled = true
            e.itemDrop.world.dropItem(
                e.itemDrop.location,
                ItemManager().make(material = Material.EGG, name = "卵", lore = FoodManager().makeExpirationDate(term))
            )
        }
    }

    @EventHandler
    fun onEntitySpawn(e: EntitySpawnEvent) {
        val entity = e.entity
        val world = entity.world.name
        val ngMobs = setOf(EntityType.MINECART_HOPPER, EntityType.MINECART_TNT, EntityType.MINECART_COMMAND)
        val witherSummonBlocks = listOf(
            Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_SKULL,
            Material.SOUL_SAND, Material.SOUL_SAND, Material.SOUL_SAND, Material.SOUL_SAND
        )

        when {
            entity.type == EntityType.ARROW && (world != "Survival" && world != "dungeon") -> e.isCancelled = true
            ngMobs.contains(entity.type) -> entity.remove()
            entity is Villager && (world == "Survival" || world == "Home") -> entity.remove()
            entity is Wither && (world != "Survival") -> {
                entity.remove()
                witherSummonBlocks.forEach { item -> entity.world.dropItem(entity.location, ItemStack(item)) }
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
        val health = maxOf(0.0, entity.health - damage).toInt()
        if (entity is Villager) {
            e.isCancelled = true
        }
        AoringoPlayer(player).sendActionBar("${ChatColor.RED}${health}HP")
        val power = Scoreboard().getValue("status_Power", player.uniqueId.toString())
        val addPowerPercentage = 0.1
        entity.damage(power * addPowerPercentage)
    }

    @EventHandler
    fun onPlayerChangedWorld(e: PlayerChangedWorldEvent) {
        val player = e.player
        val aoringoPlayer = AoringoPlayer(player)
        aoringoPlayer.setTab()
        aoringoPlayer.setProtectionPermission(plugin)
    }

    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        val player = e.player
        val world = player.world
        if (world.name == "Survival") {
            e.respawnLocation = Bukkit.getWorld("world")?.spawnLocation ?: return
        }
        AoringoPlayer(player).reduceFoodLevel(plugin)
    }

    @EventHandler
    fun onPlayerPortal(e: PlayerPortalEvent) {
        e.isCancelled = true
        AoringoPlayer(e.player).sendErrorMessage("${ChatColor.RED}ポータルの使用は禁止されております")
    }

    @EventHandler
    fun onPlayerToggleSneak(e: PlayerToggleSneakEvent) {
        if (!e.isSneaking) {
            return
        }
        val player = e.player
        val aoringoPlayer = AoringoPlayer(player)
        val block = player.location.clone().add(0.0, -1.0, 0.0).block
        val downBlock = player.location.clone().add(0.0, -2.0, 0.0).block
        if (downBlock.type == Material.COMMAND_BLOCK) {
            when (block.type) {
                Material.IRON_BLOCK -> player.openInventory(Resource().createSelectTpGUI())
                Material.QUARTZ_BLOCK -> aoringoPlayer.teleporterWorld("shop")
                Material.GOLD_BLOCK -> aoringoPlayer.teleporterWorld("Home")
                else -> {}
            }
        }
    }

    @EventHandler
    fun onBlockDispense(e: BlockDispenseEvent) {
        // ディスペンサー無効
        e.isCancelled = true
    }

    @EventHandler
    fun onAsyncPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val message = e.message
        val aoringoPlayer = AoringoPlayer(player)
        val playerItem = player.inventory.itemInMainHand
        when (PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { AoringoPlayer.PlayerData() }.chatSettingItem) {
            "rg" -> {
                e.isCancelled = true
                aoringoPlayer.namingConservationLand(plugin, message)
            }
        }
        PluginData.DataManager.playerDataMap.getOrPut(player.uniqueId) { AoringoPlayer.PlayerData() }.chatSettingItem = null
        if (message.contains("@")) {
            e.isCancelled = true
            aoringoPlayer.sendErrorMessage("${ChatColor.RED}メッセージに@を入れることは禁止されています")
        } else if (message.contains("!契約")) {
            val writtenBook = WrittenBook(playerItem)
            e.isCancelled = true
            if (playerItem.amount != 1) {
                aoringoPlayer.sendErrorMessage("アイテムを1つのみ持ってください")
                return
            }
            val contractMoney = message.replace("!契約 ", "").toInt()
            if (contractMoney == 0) { return }
            when (playerItem.itemMeta?.displayName) {
                "${ChatColor.YELLOW}契約書[未記入]" -> aoringoPlayer.writeContractRequest(contractMoney)
                "${ChatColor.YELLOW}契約書[契約待ち]" -> aoringoPlayer.createContractBook(contractMoney)
            }
        } else if (message.contains("!送金")) {
            val writtenBook = WrittenBook(playerItem)
            e.isCancelled = true
            if (playerItem.itemMeta?.displayName != "${ChatColor.YELLOW}送金申込書[未記入]") { return }
            if (playerItem.amount != 1) {
                aoringoPlayer.sendErrorMessage("アイテムを1つのみ持ってください")
                return
            }
            val subCommand = message.replace("!送金 ", "")
            if (subCommand.contains("プレイヤー ")) {
                val targetPlayerName = subCommand.replace("プレイヤー ", "")
                val targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName)
                val targetPlayerUUID = targetPlayer.uniqueId
                if (Bukkit.getPlayer(targetPlayerUUID)?.name != targetPlayerName) {
                    aoringoPlayer.sendErrorMessage("指定したプレイヤーが見つかりませんでした")
                    return
                }
                val pageText = writtenBook.getCharactersPage(1)
                writtenBook.edit(player, 1, pageText.replace("送金先口座：[記入]", "送金先口座：$targetPlayerUUID"))
            } else if (subCommand.contains("口座 ")) {
                val targetAccount = subCommand.replace("口座 ", "")
                val pageText = writtenBook.getCharactersPage(1)
                writtenBook.edit(player, 1, pageText.replace("送金先口座：[記入]", "送金先口座：$targetAccount"))
            } else if (subCommand.contains("金額 ")) {
                val price = subCommand.replace("金額 ", "")
                val pageText = writtenBook.getCharactersPage(1)
                writtenBook.edit(player, 1, pageText.replace("送金金額：[記入]", "送金金額：$price 円"))
            } else if (subCommand == "口座登録") {
                val pageText = writtenBook.getCharactersPage(1)
                writtenBook.edit(player, 1, pageText.replace("お客様口座：[記入]", "お客様口座：${player.uniqueId}"))
            }
        }
    }
    @EventHandler
    fun onBlockPistonEvent(e: BlockPistonExtendEvent) {
        // ピストン使用禁止に
        e.isCancelled = true
    }
    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        val player = e.player
        val item = e.itemDrop
        if (ItemProtectionApplication().isProtection(item.itemStack)) {
            e.isCancelled = true
            AoringoPlayer(player).sendErrorMessage("[アイテム保護]保護アイテムを捨てることはできません")
        }
    }
}
