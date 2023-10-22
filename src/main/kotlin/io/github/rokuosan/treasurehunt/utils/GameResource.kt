package io.github.rokuosan.treasurehunt.utils

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team


object GameResource {
    enum class GameStatus {
        NOT_STARTED,
        PLAYING,
    }

    var status = GameStatus.NOT_STARTED

    var teams:  Map<Team, MutableList<Player>> = mapOf()
    var playerRanking: List<Player> = listOf()


    fun initializePlayerStatus(player: Player){
        player.inventory.clear()
        player.activePotionEffects.forEach { effect ->
            player.removePotionEffect(effect.type)
        }
        player.health = 20.0
        player.foodLevel = 20
        player.saturation = 20.0F
        player.level = 0
        player.exp = 0.0F
    }

}