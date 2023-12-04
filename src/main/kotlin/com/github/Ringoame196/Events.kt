package com.github.Ringoame196

import com.github.Ringoame196.Blocks.Block
import com.github.Ringoame196.Data.Company
import com.github.Ringoame196.Data.ItemData
import com.github.Ringoame196.Data.Money
import com.github.Ringoame196.Data.WorldGuard
import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Entity.ArmorStand
import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Cook
import com.github.Ringoame196.Job.Job
import com.github.Ringoame196.Job.Mission
import com.github.Ringoame196.Shop.Fshop
import com.github.Ringoame196.Smartphone.APKs.ItemProtection
import com.github.Ringoame196.Smartphone.APKs.LandPurchase
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
        val playerClass = AoringoPlayer(player)
        if (!player.scoreboardTags.contains("member")) {
            e.joinMessage = "${ChatColor.YELLOW}${player.name}さんが初めてサーバーに参加しました"
            playerClass.fastJoin()
        }
        playerClass.setPlayer(plugin)
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val playerClass = AoringoPlayer(player)
        val item = e.item ?: ItemStack(Material.AIR)
        val playerItem = player.inventory.itemInMainHand
        val itemName = item.itemMeta?.displayName ?: ""
        val block = e.clickedBlock
        val downBlock = block?.location?.clone()?.add(0.0, -1.0, 0.0)?.block
        if (e.action == Action.LEFT_CLICK_BLOCK) { return }
        if (item != playerItem) { return }
        when (block?.type) {
            Material.OAK_SIGN -> {
                val sign = block.state as Sign
                when (sign.getLine(0)) {
                    "Fshop" -> Fshop().make(sign, player)
                    "[土地販売]" -> LandPurchase().make(player, sign)
                    "${ChatColor.YELLOW}[土地販売]" -> LandPurchase().buyGUI(player, sign)
                }
            }
            Material.ANVIL, Material.DAMAGED_ANVIL -> {
                if (player.gameMode == GameMode.CREATIVE) { return }
                e.isCancelled = true
                playerClass.useAnvil()
            }
            Material.SMITHING_TABLE -> {
                if (Job().get(player) == "${ChatColor.GRAY}鍛冶屋") { return }
                e.isCancelled = true
                playerClass.sendErrorMessage("${ChatColor.RED}鍛冶屋以外は使用することができません")
            }
            Material.SMOKER -> e.isCancelled = true
            Material.LAVA_CAULDRON -> {
                e.isCancelled = true
                Cook().fry(player, block, item, plugin)
            }
            Material.ENCHANTING_TABLE -> {
                e.isCancelled = true
                playerClass.useEnchantingTable()
            }
            Material.BARREL -> {
                val barrel = block.state as Barrel
                if (player.gameMode == GameMode.CREATIVE) {
                    return
                }
                when (barrel.customName) {
                    "クエスト" -> {
                        e.isCancelled = true
                        when (Scoreboard().getValue("mission", player.name)) {
                            0 -> Mission().set(player, barrel)
                            else -> Mission().check(player, barrel)
                        }
                    }
                    "admingift" -> {
                        e.isCancelled = true
                        Item().giveBarrelGift(player, barrel, "admingift")
                    }
                }
            }
            else -> {}
        }
        when (itemName) {
            "職業スター" -> player.openInventory(Job().makeSelectGUI())
            "まな板" -> {
                e.isCancelled = true
                val upBlock = block?.location?.clone()?.add(0.0, 1.0, 0.0)?.block
                if (upBlock?.type == Material.AIR) {
                    Cook().cuttingBoard(block)
                    Item().removeMainItem(player)
                }
            }
            "${ChatColor.YELLOW}おたま" -> {
                if (block?.type != Material.BARREL) { return }
                if (downBlock?.type != Material.CAMPFIRE) { return }
                e.isCancelled = true
                Cook().pot(block, player, plugin)
                Item().breakLadle(player)
            }
            "${ChatColor.YELLOW}カゴ" -> {
                e.isCancelled = true
                player.openInventory(Cage().createGUi(playerItem))
                player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
            }
            "${ChatColor.YELLOW}スマートフォン" -> player.openInventory(Smartphone().createGUI(plugin, player))
            "${ChatColor.RED}リンゴスクラッチ", "${ChatColor.YELLOW}金リンゴスクラッチ" -> {
                Item().removeMainItem(player)
                player.openInventory(Scratch().createGUI(itemName))
            }
            "${ChatColor.RED}会社情報本" -> {
                if (!ItemProtection().isPlayerProtection(item?:return, player)) {
                    playerClass.sendErrorMessage("会社情報本を使うには、アイテムを保護を設定する必要があります")
                } else {
                    player.openInventory(Company().createGUI())
                }
            }
            "${ChatColor.YELLOW}エンダーチェスト容量UP" -> {
                e.isCancelled = true
                playerClass.upDataEnderChestLevel(plugin)
            }
            "${ChatColor.YELLOW}契約書[未記入]" -> {
                player.sendMessage("${ChatColor.YELLOW}契約書を発行するには [!契約 (値段)]")
            }
            "${ChatColor.YELLOW}契約書[契約待ち]" -> {
                player.sendMessage("${ChatColor.YELLOW}契約書を完了するには [!契約 (契約書に書かれているお金)]")
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
            player.inventory.addItem(
                Item().make(
                    material = Material.HONEY_BOTTLE,
                    name = "${ChatColor.GOLD}ハチミツ",
                    lore = Food().giveExpirationDate(14),
                )
            )
            Item().removeMainItem(player)
        }
        if (item.type == Material.EMERALD && (item.itemMeta?.customModelData ?: return) >= 1) {
            val money = itemName.replace("${ChatColor.GREEN}", "").replace("円", "").toInt()
            if (money == 0) { return }
            Money().add(player.uniqueId.toString(), (money.times(item.amount)), true)
            player.inventory.remove(item)
        } else if (itemName.contains("${ChatColor.RED}契約本")) {
            if (player.isSneaking) {
                Contract().returnMoney(player)
            } else {
                playerClass.sendActionBar("お金を受け取るにはシフトをしてください")
            }
        } else if (itemName.contains("[アプリケーション]")) {
            APK().add(player, itemName, plugin)
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
        val playerClass = AoringoPlayer(player)
        val name = entity.customName ?: ""
        val item = entity.item
        val itemName = item.itemMeta?.displayName
        val block = entity.location.clone().add(0.0, -1.0, 0.0).block
        if (name.contains("@Fshop")) {
            if (item.type == Material.AIR && Fshop().isOwner(player, entity.location)) {
                player.sendMessage("${ChatColor.GREEN}販売開始")
            } else {
                e.isCancelled = true
                if (item.type == Material.AIR) {
                    playerClass.sendErrorMessage("売り物が設定されていません 土地のオーナー または メンバーのみ売り物を設定可能です")
                    return
                }
                Fshop().buyGUI(item, name, entity.uniqueId.toString())
            }
            return
        }
        if (name == "まな板") {
            if (Job().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                playerClass.sendErrorMessage("料理人のみ包丁を使用することができます")
                return
            }
            val mainItem = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                return
            }
            if (mainItem.type.toString().contains("SWORD")) {
                e.isCancelled = true
            }
            Cook().cut(item, player, entity)
            return
        }
        when (itemName) {
            "衣" -> {
                e.isCancelled = true
                if (Job().get(player) != "${ChatColor.YELLOW}料理人") {
                    playerClass.sendErrorMessage("料理人のみ衣をつけることができます")
                    return
                }
                itemFrame.setItem(ItemStack(Material.AIR))
                Cook().dressing(player, itemFrame)
            }
            "${ChatColor.YELLOW}混ぜハンドル" -> {
                if (block.type != Material.BARREL) { return }
                if (Job().get(player) != "${ChatColor.YELLOW}料理人") {
                    e.isCancelled = true
                    playerClass.sendErrorMessage("料理人のみ混ぜることができます")
                    return
                }
                player.playSound(player, Sound.BLOCK_BREWING_STAND_BREW, 1f, 1f)
                Cook().mix(player, block.state as Barrel)
                Item().breakHandle(itemFrame, playerClass)
            }
            "${ChatColor.RED}ポスト" -> {
                e.isCancelled = true
                if (!player.isSneaking) {
                    playerClass.sendActionBar("${ChatColor.RED}スニークでアイテム投下")
                    return
                }
                if (player.inventory.itemInMainHand.type == Material.AIR) { return }
                val direction: Vector = itemFrame.location.direction.normalize()
                val blockBehindLocation: Location = itemFrame.location.add(direction.multiply(-1))
                val blockBehind: org.bukkit.block.Block = blockBehindLocation.block
                if (blockBehind.type != Material.BARREL) { return }
                val barrel = blockBehind.state as Barrel
                playerClass.putItemInPost(barrel)
            }
        }
        if (block.type == Material.SMOKER) {
            if (Job().get(player) != "${ChatColor.YELLOW}料理人") {
                e.isCancelled = true
                playerClass.sendErrorMessage("料理人のみコンロを使用することができます")
                return
            }
            val smoker = block.state as Smoker
            if (item.type != Material.AIR) {
                return
            }
            val smokerTime = smoker.burnTime.toInt()
            if (smokerTime != 0) {
                e.isCancelled = true
                player.sendMessage("${ChatColor.RED}クールタイム中")
                return
            }
            Cook().bake(plugin, player, entity, smoker)
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
                Item().make(material = Material.HEAVY_WEIGHTED_PRESSURE_PLATE, name = "まな板")
            )
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as? org.bukkit.entity.Player ?: return
        val gui = e.view
        val item = e.currentItem ?: return
        val itemName = item.itemMeta?.displayName
        val title = gui.title
        val playerOpenInventory = player.openInventory.topInventory
        if (playerOpenInventory != e.clickedInventory && playerOpenInventory.type == InventoryType.WORKBENCH) {
            if (!item.hasItemMeta()) {
                return
            }
            e.isCancelled = true
            return
        }
        when (gui.title) {
            "${ChatColor.YELLOW}カスタム金床" -> Anvil().click(player, item, e)
            "${ChatColor.BLUE}職業選択" -> {
                e.isCancelled = true
                Job().change(player, item.itemMeta?.displayName ?: return)
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
                    Material.ENCHANTING_TABLE -> Block().enchant(player, gui, plugin)
                    else -> e.isCancelled = true
                }
            }

            "${ChatColor.BLUE}カゴ" -> {
                if (itemName == "${ChatColor.YELLOW}カゴ") {
                    e.isCancelled = true
                }
                val lore = item.itemMeta?.lore?.get(0)
                if (lore?.contains("消費期限:") == false) {
                    e.isCancelled = true
                }
            }

            "${ChatColor.GOLD}クエスト" -> {
                e.isCancelled = true
                if (itemName == "${ChatColor.RED}辞退") {
                    Mission().reset(player)
                }
            }
            "${ChatColor.BLUE}Fショップ" -> {
                e.isCancelled = true
                if (itemName == "${ChatColor.GREEN}購入") {
                    val meta = item.itemMeta ?: return
                    val price = meta.lore?.get(0)?.replace("円", "")?.toInt() ?: return
                    Fshop().buy(
                        player,
                        gui.getItem(3) ?: return,
                        price,
                        gui.getItem(0)?.itemMeta?.lore?.get(0) ?: return
                    )
                }
            }
            "${ChatColor.BLUE}スマートフォン" -> {
                e.isCancelled = true
                Smartphone().clickItem(player, item, plugin, e.isShiftClick)
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
                        gui.setItem(3, ItemProtection().chekcProtection(gui.getItem(3) ?: return, player))
                    }
                    else -> return
                }
            }

            "${ChatColor.YELLOW}OP用" -> {
                e.isCancelled = true
                Smartphone().opClick(item, plugin, e.isShiftClick, player)
            }

            "${ChatColor.BLUE}プレイヤー評価" -> {
                e.isCancelled = true
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                if (item.type == Material.PLAYER_HEAD) {
                    if (itemName != player.name) {
                        Evaluation().voidGUI(player, item)
                    }
                } else if (item.type == Material.STONE_BUTTON) {
                    Evaluation().void(gui.getItem(2) ?: return, item.itemMeta?.displayName ?: return, player)
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
            WorldGuard().removeMember(name, item.itemMeta?.displayName ?: return, player.world)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            player.closeInventory()
        } else if (title.contains("${ChatColor.BLUE}保護設定") && itemName == "${ChatColor.GREEN}作成") {
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
            if (item.itemMeta?.displayName == "${ChatColor.RED}削る") {
                e.currentItem = scratchItem
            }
            if (Scratch().check(gui, Item().make(material = Material.PAPER, name = "${ChatColor.RED}削る", customModelData = 7)) <= 6) {
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
            if (item.itemMeta?.displayName == "${ChatColor.RED}削る") {
                e.currentItem = scratchItem
            }
            if (Scratch().check(gui, Item().make(material = Material.PAPER, name = "${ChatColor.RED}削る", customModelData = 7)) == 0) {
                Scratch().result(Scratch().check(gui, scratchItem) == 9, player, 1000000)
            }
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        val block = e.block
        val worldName = player.world.name
        val playerClass = AoringoPlayer(player)
        if (player.gameMode == GameMode.CREATIVE) { return }
        if (worldName == "shop" || worldName == "world") {
            return
        }
        if (worldName == "dungeon") {
            when (block.type) {
                Material.OBSIDIAN -> ArmorStand().summonMarker(block.location, " ").addScoreboardTag("breakObsidian")
                Material.FIRE -> return
                else -> {
                    playerClass.sendErrorMessage("黒曜石以外破壊禁止されています")
                    e.isCancelled = true
                }
            }
        } else if (block.type.toString().contains("ORE") && Job().get(player) != "${ChatColor.GOLD}ハンター") {
            e.isCancelled = true
            playerClass.sendErrorMessage("${ChatColor.RED}ハンター以外は鉱石を掘ることができません")
        }
        when (block.type) {
            Material.GRASS, Material.TALL_GRASS -> {
                if (WorldGuard().getOwnerOfRegion(player.location) != null) { return }
                if (Job().get(player) != "${ChatColor.GOLD}ハンター") { return }
                if (Random.nextInt(0, 3) != 0) { return }
                Job().giveVegetables(block.location)
            }
            Material.WHEAT, Material.CARROTS, Material.POTATOES -> {
                e.isCancelled = true
                for (item in block.drops) {
                    val vegetablesName = ItemData().getVegetablesDisplayName(item.type)
                    if (vegetablesName == null) {
                        block.world.dropItem(block.location, item)
                    } else {
                        block.world.dropItem(
                            block.location,
                            Item().make(
                                item.type,
                                vegetablesName,
                                Food().giveExpirationDate(14),
                                0,
                                1
                            )
                        )
                    }
                }
                block.type = Material.AIR
            }
            Material.OAK_SIGN -> {
                val sign = block.state as Sign
                if (sign.getLine(0) != "${ChatColor.YELLOW}[土地販売]") { return }
                e.isCancelled = true
            }
            Material.SMOKER -> {
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
            }
        }
    }

    @EventHandler
    fun onCraftItem(e: CraftItemEvent) {
        val player = e.whoClicked
        if (player !is org.bukkit.entity.Player) { return }
        val item = e.currentItem
        val type = item?.type
        val playerClass = AoringoPlayer(player)
        val ngItem = mutableListOf(Material.HOPPER, Material.TNT)
        if (type == Material.FERMENTED_SPIDER_EYE) {
            e.currentItem = Item().make(material = Material.FERMENTED_SPIDER_EYE, name = "${ChatColor.GOLD}発酵した蜘蛛の目")
        } else if (item?.itemMeta?.displayName?.contains("包丁") == true) {
            e.currentItem = Cook().knifeSharpness(item)
        } else if (item?.itemMeta?.displayName?.contains("契約書") == true) {
            e.currentItem = Item().copyBlock(item, player)
        }
        if (Job().get(player) == "${ChatColor.GRAY}鍛冶屋") {
            if (Job().tool().contains(item?.type)) {
                if (e.isShiftClick) {
                    playerClass.sendErrorMessage("ツールを一括作成はできません")
                    e.isCancelled = true
                } else {
                    e.currentItem?.durability = Job().craftRandomDurable(item?.type ?: return).toShort()
                }
            }
            return
        }
        if (!Job().tool().contains(item?.type) && item?.hasItemMeta() == true) {
            e.isCancelled = true
            playerClass.sendErrorMessage("${ChatColor.RED}鍛冶屋以外はツールをクラフトすることができません")
        } else if (ngItem.contains(item?.type)) {
            e.isCancelled = true
            playerClass.sendErrorMessage("このアイテムをクラフトすることは禁止されています")
        } else if (item?.type == Material.WRITTEN_BOOK && item.itemMeta?.displayName?.contains("${ChatColor.RED}契約本@") == true) {
            e.currentItem = Contract().copyBlock(item, player)
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val player = e.player as org.bukkit.entity.Player
        val gui = e.view
        when (gui.title) {
            "${ChatColor.YELLOW}カスタム金床" -> Anvil().close(gui, player)
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
            "${ChatColor.YELLOW}アイテム保護" -> player.inventory.addItem(gui.getItem(3) ?: return)
            "${ChatColor.BLUE}スマートフォン(並び替え)" -> APK().setSort(player, gui, plugin)
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val player = e.player
        val block = e.block
        val playerClass = AoringoPlayer(player)
        when (block.type) {
            Material.SMOKER -> {
                if (Job().get(player) == "${ChatColor.YELLOW}料理人") {
                    Cook().furnace(block)
                } else {
                    e.isCancelled = true
                    playerClass.sendErrorMessage("使えるのは料理人だけです")
                }
            }
            Material.HOPPER -> {
                e.isCancelled = true
                playerClass.sendErrorMessage("ホッパーを設置することは禁止されています")
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
        val dropITem = ItemData().getEntityDropItem(entity.type) ?: return
        Food().dropReplacement(e, dropITem.material, Food().makeItem(dropITem.displayName, dropITem.customModelData))
    }

    @EventHandler
    fun onEntityDropItem(e: EntityDropItemEvent) {
        val entity = e.entity
        if (entity.type == EntityType.CHICKEN && e.itemDrop.itemStack.type == Material.EGG) {
            e.isCancelled = true
            e.itemDrop.world.dropItem(
                e.itemDrop.location,
                Item().make(material = Material.EGG, name = "卵", lore = Food().giveExpirationDate(14))
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
        entity.damage(power * 0.1)
    }

    @EventHandler
    fun onPlayerChangedWorld(e: PlayerChangedWorldEvent) {
        val player = e.player
        val playerClass = AoringoPlayer(player)
        playerClass.setTab()
        playerClass.setProtectionPermission(plugin)
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
        AoringoPlayer(e.player).sendErrorMessage("${ChatColor.RED}ポータルの使用は禁止されております")
    }

    @EventHandler
    fun onPlayerToggleSneak(e: PlayerToggleSneakEvent) {
        if (!e.isSneaking) {
            return
        }
        val player = e.player
        val playerClass = AoringoPlayer(player)
        val block = player.location.clone().add(0.0, -1.0, 0.0).block
        val downBlock = player.location.clone().add(0.0, -2.0, 0.0).block
        if (downBlock.type == Material.COMMAND_BLOCK) {
            when (block.type) {
                Material.IRON_BLOCK -> player.openInventory(Resource().createSelectTpGUI())
                Material.QUARTZ_BLOCK -> playerClass.activationTeleporter("shop")
                Material.GOLD_BLOCK -> playerClass.activationTeleporter("Home")

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
            e.isCancelled = true
            player.sendMessage("${ChatColor.RED}メッセージに@を入れることは禁止されています")
        } else if (chat.contains("!契約")) {
            e.isCancelled = true
            if (player.inventory.itemInMainHand.amount != 1) {
                AoringoPlayer(player).sendErrorMessage("アイテムを1つのみ持ってください")
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
                Runnable
                {
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
            AoringoPlayer(player).sendErrorMessage("[アイテム保護]保護アイテムを捨てることはできません")
        }
    }
}