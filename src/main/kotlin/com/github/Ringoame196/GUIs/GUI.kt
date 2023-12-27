package com.github.Ringoame196.GUIs

import org.bukkit.inventory.Inventory

interface GUI {
    fun createGUI():Inventory
    fun clickItem()
}