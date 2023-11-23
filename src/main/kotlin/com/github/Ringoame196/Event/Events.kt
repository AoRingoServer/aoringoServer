package com.github.Ringoame196.Event

import com.github.Ringoame196.Anvil
import com.github.Ringoame196.Blocks.Block
import com.github.Ringoame196.Cage
import com.github.Ringoame196.Entity.Player
import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Job.Cook
import com.github.Ringoame196.Job.Data.CookData
import com.github.Ringoame196.Job.Job
import com.github.Ringoame196.Job.Mission
import com.github.Ringoame196.Scoreboard
import com.github.Ringoame196.Shop.BarrelShop
import com.github.Ringoame196.Shop.Fshop
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
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
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
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import kotlin.random.Random

class Events(private val plugin: Plugin) : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (!player.scoreboardTags.contains("member")) {
            AoringoEvents().onFastJoinEvent(e)
        }
        Player().setName(player)
    }
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val item = e.item
        val itemName = item?.itemMeta?.displayName
        val block = e.clickedBlock
        val upBlock = block?.location?.clone()?.add(0.0, 1.0, 0.0)?.block
        val downBlock = block?.location?.add(0.0, -1.0, 0.0)?.block
        if (block?.type == Material.BARREL) {
            val shop = block.state as Barrel
            val PlayerItem = player.inventory.itemInMainHand
            if (player.isOp && player.gameMode == GameMode.CREATIVE && item?.type == Material.NAME_TAG) {
                e.isCancelled = true
                BarrelShop().changeOwner(shop, PlayerItem.itemMeta?.displayName ?: return, player)
            }
        }
        if (block?.type == Material.OAK_SIGN || downBlock?.type == Material.BARREL) {
            block ?: return
            val sign = block.state as Sign
            val barrel = downBlock?.state as Barrel
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
                    val itemFrame = block.world.spawn(block?.location, org.bukkit.entity.ItemFrame::class.java)
                    itemFrame.customName = "@Fshop,userID:${player.uniqueId},price:${sign.getLine(1)}"
                    block.type = Material.AIR
                }
            }
        }
        if (block?.type == Material.ANVIL || block?.type == Material.CHIPPED_ANVIL || block?.type == Material.DAMAGED_ANVIL) {
            if (player.gameMode == GameMode.CREATIVE) { return }
            e.isCancelled = true
            Anvil().open(player)
        } else if (block?.type == Material.SMITHING_TABLE) {
            if (Job().get(player) != "${ChatColor.GRAY}鍛冶屋") {
                AoringoEvents().onErrorEvent(player, "${ChatColor.RED}鍛冶屋以外は使用することができません")
                e.isCancelled = true
            }
        } else if (block?.type == Material.SMOKER) {
            if (e.action == Action.LEFT_CLICK_BLOCK) { return }
            e.isCancelled = true
        } else if (itemName == "職業スター") {
            if (player.inventory.itemInMainHand != item) { return }
            Job().selectGUI(player)
        } else if (itemName == "まな板") {
            e.isCancelled = true
            if (block?.location?.clone()?.add(0.0, 1.0, 0.0)?.block?.type != Material.AIR) { return }
            Cook().cuttingBoard(block)
            val playerItem = player.inventory.itemInMainHand
            playerItem.amount = playerItem.amount - 1
            player.inventory.setItemInMainHand(playerItem)
        } else if (downBlock?.type == Material.CAMPFIRE && (block.type == Material.WATER_CAULDRON || block.type == Material.CAULDRON)) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) { return }
            upBlock ?: return
            if (upBlock.type == Material.IRON_TRAPDOOR) { return }
            Cook().pot(block, player, plugin)
        } else if (block?.type == Material.ENCHANTED_BOOK) {
            e.isCancelled = true
        } else if (block?.type == Material.LAVA_CAULDRON) {
            item ?: return
            val fryItem = CookData().fly(item) ?: return
            if (!Cook().isCookLevel(fryItem.itemMeta?.displayName?:return, player)) {
                return
            }
            player.world.playSound(player.location, Sound.ITEM_BUCKET_EMPTY, 1f, 1f)
            Item().remove(player)
            Cook().fry(player, block, item, plugin)
        } else if (block?.type == Material.BARREL && downBlock?.type == Material.CAMPFIRE && item?.itemMeta?.displayName == "${ChatColor.YELLOW}おたま") {
            e.isCancelled = true
            Cook().pot(block, player, plugin)
            if (Random.nextInt(0, 100) != 0) { return }
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
            if (player.inventory.itemInMainHand != item) { return }
            e.isCancelled = true
            Cage().open(player)
        } else if (block?.type == Material.BARREL) {
            if (player.gameMode == GameMode.CREATIVE) { return }
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
            if (beeNest.honeyLevel != 5 || player.inventory.itemInMainHand.type != Material.GLASS_BOTTLE) { return }
            beeNest.honeyLevel = 0
            e.clickedBlock!!.blockData = beeNest
            player.inventory.addItem(Item().make(Material.HONEY_BOTTLE, "${ChatColor.GOLD}ハチミツ", Food().giveExpirationDate(14), null, 1))
            val playerItem = player.inventory.itemInMainHand.clone()
            playerItem.amount = playerItem.amount - 1
            player.inventory.setItemInMainHand(playerItem)
        }
    }
    @EventHandler
    fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
        val player = e.player
        val entity = e.rightClicked
        if (entity.type != EntityType.ITEM_FRAME) { return }
        val itemFrame = entity as ItemFrame
        val name = itemFrame.customName ?: return
        val item = entity.item
        val block = entity.location.clone().add(0.0, -1.0, 0.0).block
        if (name.contains("@Fshop")) {
            val owner = name.contains("userID:${player.uniqueId}")
            if (item.type == Material.AIR && owner) {
                player.sendMessage("${ChatColor.GREEN}販売開始")
            } else {
                e.isCancelled = true
                val index = name.indexOf("price:") ?: return
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
            val item = entity.item
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
        } else if (entity.item.itemMeta?.displayName == "衣") {
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
        } else if (entity.item.itemMeta?.displayName == "${ChatColor.YELLOW}混ぜハンドル" && block.type == Material.BARREL) {
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
            entity.world.dropItem(entity.location, Item().make(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "まな板", null, null, 1))
        }
    }
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked
        if (player !is org.bukkit.entity.Player) { return }
        val gui = e.view
        val item = e.currentItem ?: return
        if (gui.title.contains("${ChatColor.RED}ショップ購入:")) {
            e.isCancelled = true
            if (item.itemMeta?.displayName == "${ChatColor.GREEN}購入") {
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
                val parts = gui.title.replace("${ChatColor.RED}ショップ購入:", "").split(",")
                val location = Location(player.world, parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
                val block = location.block
                val barrel = block.state as Barrel
                val count = item.amount
                BarrelShop().buy(player, (item.itemMeta!!.lore?.get(0)?.replace("円", "")?.toInt()?.times(count) ?: return), barrel, count)
            }
        } else if (gui.title.contains("@shop")) {
            if (player.isOp && player.gameMode == GameMode.CREATIVE) { return }
            if (gui.title != "${player.name}@shop") {
                e.isCancelled = true
                return
            }
            val shopItem = gui.getItem(0)?.clone()
            shopItem?.amount = 1
            val playerItem = item.clone()
            playerItem.amount = 1
            if (playerItem != shopItem && item.type != Material.AIR) { e.isCancelled = true }
            if (e.clickedInventory != player.inventory && e.slot == 0) {
                e.isCancelled = true
                val giveItem = item.clone()
                giveItem.amount = giveItem.amount - 1
                player.inventory.addItem(giveItem)
                item.amount = 1
            }
        } else if (gui.title.contains("${ChatColor.BLUE}Fショップ")) {
            e.isCancelled = true
            if (item.itemMeta?.displayName != "${ChatColor.GREEN}購入") { return }
            val meta = item.itemMeta ?: return
            val price = meta.lore?.get(0)?.replace("円", "")?.toInt()
            Fshop().buy(player, gui.getItem(3) ?: return, price ?: return, gui.title)
        }
        if (player.openInventory.topInventory != e.clickedInventory && player.openInventory.topInventory.type == InventoryType.WORKBENCH) {
            if (item?.hasItemMeta() == false) { return }
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
                if (lore?.contains("消費期限:") == true || item?.type == null) { return }
                e.isCancelled = true
            }
            "${ChatColor.GOLD}クエスト" -> {
                e.isCancelled = true
                if (item?.itemMeta?.displayName != "${ChatColor.RED}辞退") { return }
                Mission().reset(player)
            }
        }
    }
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        val block = e.block
        if (block.type == Material.OAK_SIGN) {
            val sign = block.state as Sign
            if (sign.getLine(0) != "${ChatColor.GREEN}shop") { return }
            val barrel = block.location.add(0.0, -1.0, 0.0).block
            if (barrel.type != Material.BARREL) { return }
            val shop = barrel.state as Barrel
            shop.customName = null
            player.sendMessage("${ChatColor.AQUA}ショップを撤去しました")
            shop.update()
        }
        if (player.world.name == "shop") { return }
        if (block.type.toString().contains("ORE") && Job().get(player) != "${ChatColor.GOLD}ハンター" && player.gameMode != GameMode.CREATIVE) {
            e.isCancelled = true
            AoringoEvents().onErrorEvent(player, "${ChatColor.RED}ハンター以外は鉱石を掘ることができません")
        } else if (block.type == Material.SMOKER) {
            for (entity in block.world.getNearbyEntities(block.location.clone().add(0.0, 1.0, 0.0), 0.5, 0.5, 0.5)) {
                if (entity !is ItemFrame) { continue }
                entity.remove()
            }
        } else if ((block.type == Material.GRASS || block.type == Material.TALL_GRASS) && Job().get(player) == "${ChatColor.GOLD}ハンター") {
            if (Random.nextInt(0, 3) != 0) { return }
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
                        block.world.dropItem(block.location, Item().make(Material.WHEAT, "${ChatColor.GREEN}小麦", Food().giveExpirationDate(14), 0, 1))
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
                        block.world.dropItem(block.location, Item().make(Material.CARROT, "${ChatColor.GOLD}人参", Food().giveExpirationDate(14), 0, 1))
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
                        block.world.dropItem(block.location, Item().make(Material.POTATO, "${ChatColor.GOLD}じゃがいも", Food().giveExpirationDate(14), 0, 1))
                    }
                }
                block.type = Material.AIR
            }
            Material.BARREL -> {
                val barrel = block.state as Barrel
                val ngName = mutableListOf("admingift", "クエスト")
                if (player.gameMode == GameMode.CREATIVE) { return }
                if (!ngName.contains(barrel.customName)) { return }
                e.isCancelled = true
                AoringoEvents().onErrorEvent(player, "破壊禁止です")
            }
        }
    }
    @EventHandler
    fun onCraftItem(e: CraftItemEvent) {
        val player = e.whoClicked
        if (player !is org.bukkit.entity.Player) { return }
        val item = e.currentItem
        if (item?.type == Material.FERMENTED_SPIDER_EYE) {
            e.currentItem = Item().make(Material.FERMENTED_SPIDER_EYE, "${ChatColor.GOLD}発酵した蜘蛛の目", null, null, 1)
        }
        if (item?.itemMeta?.displayName?.contains("包丁") == true) {
            e.currentItem = Cook().knifeSharpness(item)
        }
        if (Job().get(player) == "${ChatColor.GRAY}鍛冶屋") { return }
        if (!Job().tool().contains(item?.type) && item?.hasItemMeta() == false) { return }
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
                if (item.type == Material.ENCHANTED_BOOK) { return }
                player.inventory.addItem(item)
            }
            "${ChatColor.BLUE}カゴ" -> {
                Cage().clone(player, gui)
                player.playSound(player, Sound.BLOCK_CHEST_CLOSE, 1f, 1f)
            }
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
    }
    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        val player = e.player
        val hook = e.hook
        if (e.state != PlayerFishEvent.State.CAUGHT_FISH) { return }
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
            EntityType.SHEEP -> Food().dropReplacement(e, Material.MUTTON, Food().makeItem("${ChatColor.RED}羊肉", 79))
            EntityType.PIG -> Food().dropReplacement(e, Material.PORKCHOP, Food().makeItem("${ChatColor.RED}豚肉", 81))
            EntityType.CHICKEN -> Food().dropReplacement(e, Material.CHICKEN, Food().makeItem("${ChatColor.RED}鶏肉", 80))
            else -> {}
        }
    }
    @EventHandler
    fun onEntityDropItem(e: EntityDropItemEvent) {
        val entity = e.entity
        if (entity.type == EntityType.CHICKEN && e.itemDrop.itemStack.type == Material.EGG) {
            e.isCancelled = true
            e.itemDrop.world.dropItem(e.itemDrop.location, Item().make(Material.EGG, "卵", Food().giveExpirationDate(14), null, 1))
        }
    }
    @EventHandler
    fun onEntitySpawn(e: EntitySpawnEvent) {
        val entity = e.entity
        val world = entity.world.name
        if (entity.type == EntityType.ARROW && (world == "shop" || world == "Home" || world == "testworld" || world == "world")) {
            e.isCancelled = true
        }
    }
    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        val player = e.damager
        val entity = e.entity
        if (entity !is Mob) { return }
        if (player !is org.bukkit.entity.Player) { return }
        val power = Scoreboard().getValue("status_Power", player.uniqueId.toString())
        entity.damage(power * 0.1)
    }
}
