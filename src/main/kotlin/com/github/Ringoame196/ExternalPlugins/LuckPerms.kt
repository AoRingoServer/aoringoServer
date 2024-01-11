package com.github.Ringoame196.ExternalPlugins

import com.github.Ringoame196.Entity.AoringoPlayer
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.Node

class LuckPerms(val aoringoPlayer: AoringoPlayer) {
    var luckPerms = LuckPermsProvider.get()
    var user = luckPerms.userManager.getUser(aoringoPlayer.player.uniqueId)
    fun hasPermission(permissionName: String): Boolean {
        return user?.nodes?.contains(Node.builder(permissionName).build()) ?: false
    }

    fun addPermission(permissionName: String) {
        user?.data()?.add(Node.builder(permissionName).build())
        luckPerms.getUserManager().saveUser(user ?: return)
    }
    fun revokePermission(permissionName: String) {
        user?.data()?.remove(Node.builder(permissionName).build())
        luckPerms.getUserManager().saveUser(user ?: return)
    }
}
