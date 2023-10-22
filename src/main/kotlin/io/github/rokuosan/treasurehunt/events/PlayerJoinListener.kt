package io.github.rokuosan.treasurehunt.events

import io.github.rokuosan.treasurehunt.utils.GameResource
import io.github.rokuosan.treasurehunt.utils.TeamManager
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener: Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val status = GameResource.status
        if (status == GameResource.GameStatus.NOT_STARTED) {
            if (!event.player.isOp) {
                event.player.gameMode = GameMode.SPECTATOR
            }
        }else if (status == GameResource.GameStatus.PLAYING) {
            val player = event.player
            val teams = GameResource.teams

            if (player.isOp) return
            if (teams.isEmpty()) return

            // Check the player is in the team
            val team = teams.keys.find { player in teams[it]!! }
            if (team == null) {
                // Join the team which has the least players
                val t =  TeamManager.Balancer.getInsufficientTeam(teams)?: teams.keys.first()
                GameResource.teams[t]!!.add(player)
                t.addPlayer(player)
                player.scoreboard = TeamManager.board

                // Reset the player's status
                GameResource.initializePlayerStatus(player)
            }
        }
    }
}