package com.github.Ringoame196

import org.bukkit.entity.Player
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Discord {
    fun sendMessage(json: String, webhook: String) {
        if (webhook == "URL") { return }
        try {
            val url = URL(webhook)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val outputStream = OutputStreamWriter(connection.outputStream)
            outputStream.write(json)
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode != 204) {
                println("メッセージの送信に失敗しました。レスポンスコード: $responseCode")
            }

            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun makeJson(player: Player, userName: String, icon: String, color: String, title: String, message: String, webhook: String): String {
        val location = "${player.world.name}x:${player.location.x.toInt()}y:${player.location.y.toInt()}z:${player.location.z.toInt()}"
        val json = """
{
  "username": "$userName",
  "avatar_url": "$icon",
  "embeds": [
    {
      "title": "$title",
      "description": "$message",
      "author": {
        "name": "${player.name}",
        "icon_url": "https://crafatar.com/avatars/${player.uniqueId}"
      },
      "timestamp": "${java.time.OffsetDateTime.now()}",
      "color": $color,
      "fields": [
        {
          "name": "IPアドレス",
          "value": "${player.address?.address?.hostAddress}"
        },
        {
          "name": "座標",
          "value": "$location"
        },
        {
          "name": "手持ちアイテム",
          "value": "${player.inventory.itemInMainHand.type}"
        }
      ]
    }
  ]
}
"""
        return json
    }
}
