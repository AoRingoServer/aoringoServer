package com.github.Ringoame196

import com.github.Ringoame196.Items.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class Recipe {
    fun add(plugin: Plugin) {
        repairKit()
        remove("cuttingBoard", plugin)
        cuttingBoard(plugin)
        remove("bunMold", plugin)
        bunMold(plugin)
        remove("mixStick", plugin)
        mixStick(plugin)
        remove("stoneKnife", plugin)
        knife(Material.STONE_SWORD, Material.STONE, "${ChatColor.GOLD}石", "stoneKnife", plugin)
        remove("ironKnife", plugin)
        knife(Material.IRON_SWORD, Material.IRON_BLOCK, "鉄", "ironKnife", plugin)
        remove("diamondKnife", plugin)
        knife(Material.DIAMOND_SWORD, Material.DIAMOND_BLOCK, "${ChatColor.AQUA}ダイヤモンド", "diamondKnife", plugin)
        remove("handle", plugin)
        handle(plugin)
        remove("breadMold", plugin)
        breadMold(plugin)
        remove("bunMold", plugin)
        bunMold(plugin)
        remove("bambooSkewer", plugin)
        bambooSkewer(plugin)
    }
    fun repairKit() {
        val customItem = Item().make(Material.FLINT, "${ChatColor.YELLOW}修理キット", null, 1, 1)
        val recipe = ShapelessRecipe(customItem)
        recipe.addIngredient(1, Material.IRON_INGOT)
        recipe.addIngredient(1, Material.GOLD_INGOT)
        recipe.addIngredient(1, Material.COPPER_INGOT)
        recipe.addIngredient(1, Material.COPPER_INGOT)

        // レシピをサーバーに登録
        Bukkit.addRecipe(recipe)
    }
    fun cuttingBoard(plugin: Plugin) {
        val customResultItem = Item().make(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "まな板", null, null, 1)
        val customRecipe = ShapedRecipe(NamespacedKey(plugin, "cuttingBoard"), customResultItem)
        customRecipe.shape("AAA", "III", "OOO")
        customRecipe.setIngredient('I', Material.IRON_INGOT)
        customRecipe.setIngredient('O', Material.OAK_PLANKS)

        // レシピをサーバーに登録
        Bukkit.addRecipe(customRecipe)
    }
    fun bunMold(plugin: Plugin) {
        val customResultItem = Item().make(Material.PAPER, "食パンの型", null, 5, 1)
        val customRecipe = ShapedRecipe(NamespacedKey(plugin, "bunMold"), customResultItem)
        customRecipe.shape("AAC", "AIC", "CCC")
        customRecipe.setIngredient('I', Material.IRON_INGOT)
        customRecipe.setIngredient('C', Material.COPPER_INGOT)

        // レシピをサーバーに登録
        Bukkit.addRecipe(customRecipe)
    }
    fun breadMold(plugin: Plugin) {
        val customResultItem = Item().make(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "まな板", null, null, 1)
        val customRecipe = ShapedRecipe(NamespacedKey(plugin, "breadMold"), customResultItem)
        customRecipe.shape("AAC", "AIC", "CCC")
        customRecipe.setIngredient('I', Material.IRON_INGOT)
        customRecipe.setIngredient('C', Material.COPPER_INGOT)

        // レシピをサーバーに登録
        Bukkit.addRecipe(customRecipe)
    }
    fun mixStick(plugin: Plugin) {
        val customResultItem = Item().make(Material.STICK, "${ChatColor.YELLOW}おたま", null, 1, 1)
        val customRecipe = ShapedRecipe(NamespacedKey(plugin, "mixStick"), customResultItem)
        customRecipe.shape("SSS", "SWS", "SSS")
        customRecipe.setIngredient('S', Material.COBBLESTONE)
        customRecipe.setIngredient('W', Material.WOODEN_SHOVEL)

        // レシピをサーバーに登録
        Bukkit.addRecipe(customRecipe)
    }
    fun handle(plugin: Plugin) {
        val customResultItem = Item().make(Material.STICK, "${ChatColor.YELLOW}混ぜハンドル", null, 2, 1)
        val customRecipe = ShapedRecipe(NamespacedKey(plugin, "handle"), customResultItem)
        customRecipe.shape("ASA", "SIS", "ASA")
        customRecipe.setIngredient('S', Material.COBBLESTONE)
        customRecipe.setIngredient('I', Material.IRON_INGOT)

        // レシピをサーバーに登録
        Bukkit.addRecipe(customRecipe)
    }
    fun bambooSkewer(plugin: Plugin) {
        val customResultItem = Item().make(Material.STICK, "${ChatColor.GOLD}竹串", null, 3, 1)
        val customRecipe = ShapedRecipe(NamespacedKey(plugin, "bambooSkewer"), customResultItem)
        customRecipe.shape("AOA", "OSO", "AOA")
        customRecipe.setIngredient('O', Material.OAK_PLANKS)
        customRecipe.setIngredient('S', Material.STICK)

        // レシピをサーバーに登録
        Bukkit.addRecipe(customRecipe)
    }
    private fun knife(sword: Material, block: Material, kinds: String, id: String, plugin: Plugin) {
        val item = ItemStack(sword)
        val meta = item.itemMeta
        meta?.setDisplayName("${kinds}の包丁")
        meta?.setCustomModelData(1)
        item.setItemMeta(meta)

        val customRecipe = ShapedRecipe(NamespacedKey(plugin, id), item)
        customRecipe.shape("AAB", "ABA", "SAA")
        customRecipe.setIngredient('B', block)
        customRecipe.setIngredient('S', sword)

        Bukkit.addRecipe(customRecipe)
    }
    private fun remove(key: String, plugin: Plugin) {
        val recipeKey = NamespacedKey(plugin, key)
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey)
        }
    }
}
