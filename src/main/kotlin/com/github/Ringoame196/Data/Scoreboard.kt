package com.github.Ringoame196

import org.bukkit.Bukkit

class Scoreboard {
    val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
    fun make(id: String, name: String) {
        if (existence(id)) { return }
        scoreboard?.registerNewObjective(id, "dummy", name)
    }
    fun set(scoreName: String, name: String, value: Int) {
        val objective = scoreboard?.getObjective(scoreName) ?: return
        val score = objective.getScore(name)
        score.score = value
    }
    fun existence(scoreName: String): Boolean {
        return scoreboard?.getObjective(scoreName) != null
    }
    fun add(scoreName: String, name: String, add: Int) {
        val value = getValue(scoreName, name) + add
        set(scoreName, name, value)
    }
    fun reduce(scoreName: String, name: String, remove: Int) {
        val value = getValue(scoreName, name) - remove
        set(scoreName, name, value)
    }
    fun getSize(scoreName: String): Int {
        val objective = scoreboard?.getObjective(scoreName) ?: return 0
        var scoreCount = 0
        for (entry in scoreboard.entries) {
            val score = objective.getScore(entry)
            if (Scoreboard().getValue(scoreName, entry) == 0) { continue }
            if (score.isScoreSet) {
                scoreCount++
            }
        }
        return scoreCount
    }
    fun getValue(score: String, name: String): Int {
        val objective = scoreboard?.getObjective(score) ?: return 0
        val scoreObject = objective.getScore(name)
        return scoreObject.score
    }
    fun delete(score: String) {
        val objective = scoreboard?.getObjective(score) ?: return
        objective.unregister()
    }
}
