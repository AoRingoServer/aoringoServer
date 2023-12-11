package com.github.Ringoame196.Job

import com.github.Ringoame196.Items.Food
import com.github.Ringoame196.Items.Item
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

class Job {
    private val jobList = mutableListOf("無職", "${ChatColor.YELLOW}料理人", "${ChatColor.GOLD}ハンター", "${ChatColor.GRAY}鍛冶屋")
    fun setJob(player: Player, id: Int) {
        Scoreboard().set("job", player.uniqueId.toString(), id)
    }
    fun get(player: Player): String {
        val jobID = Scoreboard().getValue("job", player.uniqueId.toString())
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
    fun change(player: Player, jobName: String) {
        val item = player.inventory.itemInMainHand
        item.amount = item.amount - 1
        for (i in 0 until jobList.size) {
            if (jobList[i] != jobName) { continue }
            Scoreboard().reduce("job", get(player), 1)
            setJob(player, i)
            Scoreboard().add("job", get(player), 1)
            prefix(player)
            player.sendMessage("${jobName}に就職しました")
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
            player.world.spawnParticle(Particle.EXPLOSION_HUGE, player.location, 1)
            player.inventory.setItemInMainHand(item)
            player.closeInventory()
            giveMoney(player)
        }
    }
    private fun giveMoney(player: Player) {
        val list = mutableListOf(
            Scoreboard().getValue("job", "${ChatColor.YELLOW}料理人"),
            Scoreboard().getValue("job", "${ChatColor.GOLD}ハンター"),
            Scoreboard().getValue("job", "${ChatColor.GRAY}鍛冶屋"),
        )
        val rankList = list.sortedByDescending { it }
        val giveMoney = when (rankList.indexOf(Scoreboard().getValue("job", Job().get(player)))) {
            0 -> 1000
            1 -> 5000
            2 -> 10000
            else -> 0
        }
        Money().add(player.uniqueId.toString(), giveMoney, true)
    }
    fun makeSelectGUI(): Inventory {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}職業選択")
        gui.setItem(2, jobGUI(Material.IRON_SWORD, "${ChatColor.GOLD}ハンター"))
        gui.setItem(4, jobGUI(Material.MILK_BUCKET, "${ChatColor.YELLOW}料理人"))
        gui.setItem(6, jobGUI(Material.ANVIL, "${ChatColor.GRAY}鍛冶屋"))
        return gui
    }
    private fun jobGUI(material: Material, jobName: String): ItemStack {
        val employmentRate = "${Scoreboard().getValue("job",jobName)}人が就職しています"
        return Item().make(material, jobName, employmentRate, null, 1)
    }
    fun tool(): List<Material> {
        return mutableListOf(
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
    }
    fun giveVegetables(location: Location) {
        val vegetables = mutableListOf(
            Food().makeItem("${ChatColor.GREEN}キュウリ", 1),
            Food().makeItem("${ChatColor.GOLD}キャベツ", 2),
            Food().makeItem("${ChatColor.GOLD}スパイス", 3),
            Food().makeItem("稲", 4),
            Food().makeItem("${ChatColor.DARK_PURPLE}なす", 5),
            Food().makeItem("${ChatColor.GOLD}たまねぎ", 6),
            Food().makeItem("${ChatColor.RED}トマト", 7),
        )
        location.world?.dropItem(location, vegetables[Random.nextInt(0, vegetables.size)])
    }
    fun givefish(player: Player): ItemStack {
        val fish = mutableListOf(
            Food().makeItem("${ChatColor.RED}マグロ", 31),
            Food().makeItem("${ChatColor.GOLD}サーモン", 32),
            Item().make(Material.EXPERIENCE_BOTTLE, "${ChatColor.GREEN}経験値瓶", "", null, 1),
            Item().enchant(Enchantment.LURE, 1)
        )
        if (Job().get(player) == "${ChatColor.GOLD}ハンター") {
            fish.add(Food().makeItem("${ChatColor.RED}タコ", 38))
            fish.add(Food().makeItem("イカ", 35))
            fish.add(Food().makeItem("${ChatColor.AQUA}エビ", 28))
            fish.add(ItemStack(Material.LEATHER))
            fish.add(Food().makeItem("${ChatColor.BLACK}海苔", 37))
        }
        if (Job().get(player) == "${ChatColor.GOLD}ハンター" || Random.nextInt(0, 10) == 0) {
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
            player.playerListName + when (Scoreboard().getValue("cookLevel", player.uniqueId.toString())) {
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
