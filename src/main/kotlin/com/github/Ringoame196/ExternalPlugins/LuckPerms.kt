package com.github.Ringoame196.ExternalPlugins

import com.github.Ringoame196.Entity.AoringoPlayer
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.Node
import java.util.*

class LuckPerms(val aoringoPlayer: AoringoPlayer) {
    var luckPerms = LuckPermsProvider.get()
    var user = luckPerms.userManager.getUser(aoringoPlayer.player.uniqueId)
    fun acquisitionPermisshons() {
    }

    fun addPermisshon(permisshonName: String) {
        user?.data()?.add(Node.builder(permisshonName).build())
        luckPerms.getUserManager().saveUser(user ?: return)
    }
    fun revokePermisshon(permisshonName: String) {
        user?.data()?.remove(Node.builder(permisshonName).build())
        luckPerms.getUserManager().saveUser(user ?: return)
    }
}
