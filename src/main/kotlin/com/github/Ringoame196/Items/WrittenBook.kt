package com.github.Ringoame196.Items

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class WrittenBook(private val writtenBook: ItemStack) {
    val meta = writtenBook.itemMeta as BookMeta
    fun getCharactersPage(page: Int): String {
        return meta.getPage(page)
    }
    fun conversionMap(pageText: String): Map<String, String> {
        val punctuationMark = "："
        val lineMap = mutableMapOf<String, String>()
        val lines = pageText.split("\n")
        for (line in lines) {
            if (!line.contains(punctuationMark)) { continue }
            val punctuationIndex = line.indexOf(punctuationMark)
            val key = line.substring(0, punctuationIndex - 1)
            val contents = line.substring(punctuationIndex)
            lineMap[key] = contents
        }
        return lineMap
    }
    fun edit(player: Player, page: Int, text: String) {
        meta.setPage(page, text)
        writtenBook.setItemMeta(meta)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
    fun changeItemName(player: Player, newItemName: String) {
        meta.setDisplayName(newItemName)
        writtenBook.setItemMeta(meta)
    }
}
