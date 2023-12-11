package com.github.Ringoame196.Smartphones.APKs

import com.github.Ringoame196.APK
import com.github.Ringoame196.APKs
import com.github.Ringoame196.Items.Item
import com.github.Ringoame196.Smartphones.Smartphone
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class SortAPK:APKs {
    override val customModelData:Int = 8
    override fun openGUI(player: Player, plugin: Plugin) {
        val apk = APK()
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.BLUE}スマートフォン(並び替え)")
        player.openInventory(gui)
        val apkList = apk.get(plugin, player) ?: return
        for (apkName in apkList) {
            val apkLIst = Smartphone().apkList
            val customModelData = apkLIst[apkName]?.customModelData?:0
            gui.addItem(Item().make(Material.GREEN_CONCRETE, "[アプリケーション]$apkName", customModelData = customModelData))
        }
    }
}