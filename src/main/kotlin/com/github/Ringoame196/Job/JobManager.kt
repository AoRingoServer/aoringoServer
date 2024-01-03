package com.github.Ringoame196.Job

import com.github.Ringoame196.Entity.AoringoPlayer
import com.github.Ringoame196.Foods.FoodManager
import com.github.Ringoame196.Items.ItemManager
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class JobManager {
    private val jobList = mutableListOf("無職", "${ChatColor.YELLOW}料理人", "${ChatColor.GOLD}ハンター", "${ChatColor.GRAY}鍛冶屋")
    private val jobScoreboardName = "job"
    private val scoreboard = Scoreboard()
    private val foodManager = FoodManager()
    private val itemManager = ItemManager()
    fun setJob(player: Player, id: Int) {
        scoreboard.set(jobScoreboardName, player.uniqueId.toString(), id)
    }
    fun get(player: Player): String {
        val jobID = scoreboard.getValue(jobScoreboardName, player.uniqueId.toString())
        return jobList[jobID]
    }
    fun prefix(player: Player) {
        val displayName = when (get(player)) {
            "${ChatColor.YELLOW}料理人" -> "${ChatColor.DARK_PURPLE}"
            "${ChatColor.GOLD}ハンター" -> "${ChatColor.DARK_RED}"
            "${ChatColor.GRAY}鍛冶屋" -> "${ChatColor.GRAY}"
            else -> ""
        } + player.name
        val prefix = if (player.isOp) {
            "${ChatColor.YELLOW}[運営]"
        } else {
            ""
        }
        player.setDisplayName("${prefix}$displayName@${get(player)}")
        player.setPlayerListName("${prefix}$displayName")
        if (get(player) == "${ChatColor.YELLOW}料理人") {
            titleStar(player)
        }
    }
    private fun sendDescription(player: Player) {
        player.sendMessage(
            "${ChatColor.GOLD}[ハンター]\n" +
                "他の職業が作成に必要な素材アイテムは主にハンターにしか入手することができない為、かなり重要度の高い職業になっています。\n" +
                "${ChatColor.YELLOW}[料理人]\n" +
                "当サーバーでは死亡時など満腹度が大きく減少する場面が多々あります\n" +
                "\n" +
                "ハンターから買取った食材を調理し他の職業へ売りお金を稼ぎましょう\n" +
                "${ChatColor.AQUA}[鍛冶屋]\n" +
                "ツールや防具など生活にかかせない必要なアイテムを製作できる唯一の職業です。\n" +
                "\n" +
                "ハンターから買取った鉱石でアイテムを製作し他職業へ売りお金を稼ぎましょう"
        )
    }
    fun change(player: Player, jobName: String) {
        val itemManager = ItemManager()
        if (jobName == "${ChatColor.RED}職業説明") {
            player.closeInventory()
            sendDescription(player)
            return
        }
        val jobIDMap = mapOf(
            "${ChatColor.YELLOW}料理人" to 1,
            "${ChatColor.GOLD}ハンター" to 2,
            "${ChatColor.GRAY}鍛冶屋" to 3
        )
        scoreboard.reduce(jobScoreboardName, get(player), 1)
        setJob(player, jobIDMap[jobName] ?: return)
        scoreboard.add(jobScoreboardName, get(player), 1)
        prefix(player)
        player.sendMessage("${jobName}に就職しました")
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
        player.world.spawnParticle(Particle.EXPLOSION_HUGE, player.location, 1)
        itemManager.reduceMainItem(player)
        player.closeInventory()
        giveMoney(player)
    }
    private fun giveMoney(player: Player) {
        val aoringoPlayer = AoringoPlayer(player)
        val moneyUseCase = aoringoPlayer.moneyUseCase
        val list = mutableListOf(
            scoreboard.getValue(jobScoreboardName, "${ChatColor.YELLOW}料理人"),
            scoreboard.getValue(jobScoreboardName, "${ChatColor.GOLD}ハンター"),
            scoreboard.getValue(jobScoreboardName, "${ChatColor.GRAY}鍛冶屋"),
        )
        val rankList = list.sortedByDescending { it }
        val giveMoney = when (rankList.indexOf(Scoreboard().getValue("job", JobManager().get(player)))) {
            0 -> 1000
            1 -> 5000
            2 -> 10000
            else -> 0
        }
        moneyUseCase.getMoneyFromAdmin(aoringoPlayer, giveMoney)
    }
    fun makeSelectGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}職業選択")
        gui.setItem(1, itemManager.make(Material.BOOK, "${ChatColor.RED}職業説明", "クリックすると職業の説明が見れます"))
        gui.setItem(2, showPeopleEmployedNumber(Material.IRON_SWORD, "${ChatColor.GOLD}ハンター"))
        gui.setItem(4, showPeopleEmployedNumber(Material.MILK_BUCKET, "${ChatColor.YELLOW}料理人"))
        gui.setItem(6, showPeopleEmployedNumber(Material.ANVIL, "${ChatColor.GRAY}鍛冶屋"))
        return gui
    }
    private fun showPeopleEmployedNumber(material: Material, jobName: String): ItemStack {
        val employmentRate = "${Scoreboard().getValue(jobScoreboardName,jobName)}人が就職しています"
        return ItemManager().make(material, jobName, employmentRate)
    }
    val tool = mutableListOf(
        Material.IRON_SWORD,
        Material.GOLDEN_SWORD,
        Material.DIAMOND_SWORD,
        Material.IRON_PICKAXE,
        Material.GOLDEN_PICKAXE,
        Material.DIAMOND_PICKAXE,
        Material.IRON_AXE,
        Material.GOLDEN_AXE,
        Material.DIAMOND_AXE,
        Material.NETHERITE_AXE,
        Material.IRON_SHOVEL,
        Material.GOLDEN_SHOVEL,
        Material.DIAMOND_SHOVEL,
        Material.IRON_HOE,
        Material.GOLDEN_HOE,
        Material.DIAMOND_HOE,
        Material.IRON_HELMET,
        Material.IRON_CHESTPLATE,
        Material.IRON_LEGGINGS,
        Material.IRON_BOOTS,
        Material.GOLDEN_HELMET,
        Material.GOLDEN_CHESTPLATE,
        Material.GOLDEN_LEGGINGS,
        Material.GOLDEN_BOOTS,
        Material.DIAMOND_HELMET,
        Material.DIAMOND_CHESTPLATE,
        Material.DIAMOND_LEGGINGS,
        Material.DIAMOND_BOOTS,
        Material.SHIELD,
        Material.ARROW,
        Material.BOW
    )
    fun giveVegetables(location: Location) {
        val vegetables = mutableListOf(
            foodManager.makeItem("${ChatColor.GREEN}キュウリ", 1),
            foodManager.makeItem("${ChatColor.GOLD}キャベツ", 2),
            foodManager.makeItem("${ChatColor.GOLD}スパイス", 3),
            foodManager.makeItem("稲", 4),
            foodManager.makeItem("${ChatColor.DARK_PURPLE}なす", 5),
            foodManager.makeItem("${ChatColor.GOLD}たまねぎ", 6),
            foodManager.makeItem("${ChatColor.RED}トマト", 7),
        )
        location.world?.dropItem(location, vegetables[Random.nextInt(0, vegetables.size)])
    }
    fun givefish(player: Player): ItemStack {
        val fish = mutableListOf(
            foodManager.makeItem("${ChatColor.RED}マグロ", 31),
            foodManager.makeItem("${ChatColor.GOLD}サーモン", 32),
            itemManager.make(Material.EXPERIENCE_BOTTLE, "${ChatColor.GREEN}経験値瓶", ""),
            itemManager.enchant(Enchantment.LURE, 1)
        )
        if (JobManager().get(player) == "${ChatColor.GOLD}ハンター") {
            fish.add(FoodManager().makeItem("${ChatColor.RED}タコ", 38))
            fish.add(FoodManager().makeItem("イカ", 35))
            fish.add(FoodManager().makeItem("${ChatColor.AQUA}エビ", 28))
            fish.add(ItemStack(Material.LEATHER))
            fish.add(FoodManager().makeItem("${ChatColor.BLACK}海苔", 37))
        }
        if (JobManager().get(player) == "${ChatColor.GOLD}ハンター" || Random.nextInt(0, 10) == 0) {
            return fish.get(Random.nextInt(0, fish.size))
        }
        val rubbish = mutableListOf(
            ItemStack(Material.STRING),
            ItemStack(Material.FEATHER),
            ItemStack(Material.LILY_PAD),
            ItemStack(Material.BONE),
            ItemStack(Material.LEATHER)
        )
        return rubbish[Random.nextInt(0, rubbish.size)]
    }
    fun titleStar(player: Player) {
        player.setPlayerListName(
            player.playerListName + when (scoreboard.getValue("cookLevel", player.uniqueId.toString())) {
                1 -> "${ChatColor.YELLOW}★"
                2 -> "${ChatColor.YELLOW}★★"
                3 -> "${ChatColor.YELLOW}★★★"
                else -> ""
            }
        )
    }
    fun craftRandomDurable(material: Material): Int {
        return Random.nextInt(0, material.maxDurability.toInt() - 1)
    }
}
