package com.github.Ringoame196.Data

import com.github.Ringoame196.Items.FoodManager
import com.github.Ringoame196.Items.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CookData {
    fun food(name: String, custom: Int): ItemStack {
        return ItemManager().make(Material.MELON_SLICE, name, customModelData = custom)
    }
    fun cut(itemStack: ItemStack): ItemStack? {
        return when (itemStack.itemMeta?.displayName) {
            "${ChatColor.RED}鶏肉" -> food("${ChatColor.RED}切った鶏肉", 27)
            "${ChatColor.GOLD}じゃがいも" -> food("${ChatColor.GOLD}切ったじゃがいも", 8)
            "${ChatColor.GOLD}切ったじゃがいも" -> food("${ChatColor.GOLD}切りすぎたじゃがいも", 84)
            "${ChatColor.GOLD}サーモン" -> food("${ChatColor.GOLD}サーモンの切り身", 34)
            "${ChatColor.RED}マグロ" -> food("${ChatColor.RED}マグロの切り身", 33)
            "${ChatColor.GREEN}キュウリ" -> food("${ChatColor.GREEN}切ったキュウリ", 10)
            "${ChatColor.GOLD}人参" -> food("${ChatColor.GOLD}切った人参", 13)
            "${ChatColor.GOLD}キャベツ" -> food("${ChatColor.GREEN}切ったキャベツ", 15)
            "${ChatColor.YELLOW}生生地" -> food("${ChatColor.YELLOW}生麺", 16)
            "${ChatColor.RED}牛肉" -> food("${ChatColor.RED}切った牛肉", 93)
            "${ChatColor.RED}切った牛肉" -> food("${ChatColor.RED}牛のひき肉", 24)
            "${ChatColor.RED}豚肉" -> food("${ChatColor.RED}切った豚肉", 94)
            "${ChatColor.GOLD}たまねぎ" -> food("${ChatColor.GOLD}剥きたまねぎ", 9)
            "${ChatColor.GOLD}剥きたまねぎ" -> food("${ChatColor.GOLD}切ったたまねぎ", 97)
            "${ChatColor.GOLD}切ったたまねぎ" -> food("${ChatColor.GOLD}たまねぎのみじん切り", 98)
            "${ChatColor.DARK_PURPLE}なす" -> food("${ChatColor.DARK_PURPLE}切ったなす", 11)
            "${ChatColor.RED}タコ" -> food("${ChatColor.RED}タコの切り身", 39)
            "イカ" -> food("イカの切り身", 36)
            else -> return null
        }
    }
    fun bake(itemStack: ItemStack): ItemStack? {
        return when (itemStack.itemMeta?.displayName) {
            "${ChatColor.RED}牛肉" -> food("${ChatColor.RED}[完成品]ステーキ", 50)
            "生食パン" -> food("${ChatColor.GOLD}食パン", 90)
            "${ChatColor.RED}生ハンバーグ" -> food("${ChatColor.YELLOW}[完成品]ハンバーグ", 49)
            "${ChatColor.RED}生焼き鳥" -> food("${ChatColor.YELLOW}[完成品]焼き鳥", 100)
            else -> null
        }
    }
    fun dressing(itemStack: ItemStack): ItemStack? {
        return when (itemStack.itemMeta?.displayName) {
            "${ChatColor.RED}切った鶏肉" -> food("${ChatColor.RED}生からあげ", 23)
            "${ChatColor.RED}豚肉" -> food("${ChatColor.RED}生とんかつ", 83)
            "${ChatColor.RED}切った豚肉" -> food("衣付きの切った豚肉", 95)
            else -> null
        }
    }
    fun fly(itemStack: ItemStack): ItemStack? {
        val item = when (itemStack.itemMeta?.displayName) {
            "${ChatColor.RED}生からあげ" -> food("${ChatColor.YELLOW}[完成品]からあげ", 48)
            "${ChatColor.RED}生とんかつ" -> food("${ChatColor.YELLOW}[完成品]とんかつ", 82)
            "${ChatColor.GOLD}切りすぎたじゃがいも" -> food("${ChatColor.YELLOW}[完成品]フライドポテト", 58)
            "衣付きの切った豚肉" -> food("${ChatColor.YELLOW}揚げ豚肉", 96)
            else -> null
        }
        item?.amount = 1
        return item
    }
    fun mix(ingredients: MutableList<String>): ItemStack? {
        val salmonNigiri = mutableListOf("${ChatColor.GOLD}サーモンの切り身", "シャリ")
        val tunaNigiri = mutableListOf("${ChatColor.RED}マグロの切り身", "シャリ")
        val riceBall = mutableListOf("${ChatColor.BLACK}海苔", "ライス")
        val salad = mutableListOf("${ChatColor.GREEN}切ったキュウリ", "${ChatColor.GREEN}切ったキャベツ", "${ChatColor.GOLD}切った人参")
        val shari = mutableListOf("ライス", "${ChatColor.GOLD}お酢")
        val rawMaterial = mutableListOf("${ChatColor.GREEN}小麦", "${ChatColor.AQUA}飲料水", "卵")
        val tomatoPaste = mutableListOf("${ChatColor.RED}トマト")
        val bread = mutableListOf("${ChatColor.YELLOW}生生地", "食パンの型")
        val spaghetti = mutableListOf("${ChatColor.YELLOW}麺", "${ChatColor.RED}牛のひき肉", "${ChatColor.GOLD}スパイス", "${ChatColor.RED}トマトペースト")
        val rawHamburge = mutableListOf("${ChatColor.GOLD}スパイス", "${ChatColor.RED}牛のひき肉", "${ChatColor.GOLD}たまねぎのみじん切り", "卵")
        val rawYakitori = mutableListOf("${ChatColor.GOLD}竹串", "${ChatColor.RED}切った鶏肉")
        val octopusNigiri = mutableListOf("${ChatColor.RED}タコの切り身", "シャリ")
        val squidNigiri = mutableListOf("イカの切り身", "シャリ")
        val wort = mutableListOf("${ChatColor.GOLD}発酵した蜘蛛の目", "${ChatColor.GREEN}小麦", "${ChatColor.GOLD}ハチミツ", "${ChatColor.AQUA}飲料水")
        val curryrRice = mutableListOf("${ChatColor.GOLD}カレー", "ライス")
        val curryrUdon = mutableListOf("${ChatColor.GOLD}カレー", "${ChatColor.YELLOW}麺")
        return when (ingredients.toSet()) {
            salmonNigiri.toSet() -> food("${ChatColor.GOLD}[完成品]サーモンの握り", 71)
            tunaNigiri.toSet() -> food("${ChatColor.RED}[完成品]マグロの握り", 70)
            riceBall.toSet() -> food("[完成品]おにぎり", 85)
            salad.toSet() -> food("${ChatColor.GREEN}[完成品]サラダ", 59)
            shari.toSet() -> food("シャリ", 41)
            rawMaterial.toSet() -> food("${ChatColor.YELLOW}生生地", 18)
            tomatoPaste.toSet() -> food("${ChatColor.RED}トマトペースト", 12)
            bread.toSet() -> food("生食パン", 89)
            spaghetti.toSet() -> food("${ChatColor.YELLOW}[完成]スパゲッティー", 46)
            rawHamburge.toSet() -> food("${ChatColor.RED}生ハンバーグ", 22)
            rawYakitori.toSet() -> food("${ChatColor.RED}生焼き鳥", 99)
            octopusNigiri.toSet() -> food("${ChatColor.RED}タコの握り", 73)
            squidNigiri.toSet() -> food("イカの握り", 72)
            wort.toSet() -> ItemManager().make(Material.MILK_BUCKET, "${ChatColor.GREEN}麦汁", FoodManager().makeExpirationDate(0), 2, 1)
            curryrRice.toSet() -> food("${ChatColor.YELLOW}[完成]カレーライス", 44)
            curryrUdon.toSet() -> food("${ChatColor.YELLOW}[完成]カレーうどん", 61)
            else -> null
        }
    }
    fun fermentationMix(ingredients: MutableList<String>): ItemStack? {
        val liquor = mutableListOf("${ChatColor.GREEN}麦汁")
        return when (ingredients.toSet()) {
            liquor.toSet() -> ItemManager().make(Material.MILK_BUCKET, "${ChatColor.GOLD}[完成品]ビール", customModelData = 3)
            else -> null
        }
    }
    fun pot(ingredients: MutableList<String>): ItemStack? {
        val rice = mutableListOf<String>("稲", "${ChatColor.AQUA}飲料水")
        val noodles = mutableListOf<String>("${ChatColor.YELLOW}生麺")
        val subuta = mutableListOf("${ChatColor.YELLOW}揚げ豚肉", "${ChatColor.GOLD}切ったたまねぎ", "${ChatColor.GOLD}切った人参", "${ChatColor.GOLD}お酢")
        val mapoEggplant = mutableListOf("${ChatColor.DARK_PURPLE}切ったなす", "${ChatColor.RED}牛のひき肉", "${ChatColor.GOLD}スパイス")
        val curry = mutableListOf("${ChatColor.RED}切った牛肉", "${ChatColor.GOLD}切ったたまねぎ", "${ChatColor.GOLD}切ったじゃがいも", "${ChatColor.GOLD}切った人参", "${ChatColor.GOLD}スパイス", "${ChatColor.AQUA}飲料水")
        return when (ingredients.toSet()) {
            rice.toSet() -> food("ライス", 45)
            noodles.toSet() -> food("${ChatColor.YELLOW}麺", 17)
            subuta.toSet() -> food("${ChatColor.YELLOW}[完成品]酢豚", 101)
            mapoEggplant.toSet() -> food("${ChatColor.DARK_PURPLE}[完成品]麻婆なす", 102)
            curry.toSet() -> food("${ChatColor.GOLD}カレー", 69)
            else -> null
        }
    }
}
