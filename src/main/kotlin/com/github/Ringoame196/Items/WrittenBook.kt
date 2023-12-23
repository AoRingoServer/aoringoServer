package com.github.Ringoame196.Items

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
}
