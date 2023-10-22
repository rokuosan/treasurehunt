package io.github.rokuosan.treasurehunt.utils

import io.github.rokuosan.treasurehunt.TreasureHunt
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

class TeamManager {
    private val plugin = TreasureHunt.plugin
    private val sbm = plugin.server.scoreboardManager
    private val board = sbm.newScoreboard

    fun randomGenerate(divideInto: Int): Map<Team, MutableList<Player>>{
        val players = ArrayDeque(plugin.server.onlinePlayers.shuffled())

        val playersPerTeam = players.size / divideInto

        val teams: MutableMap<Team, MutableList<Player>> = mutableMapOf()
        repeat(divideInto){
            val team = this.board.registerNewTeam("Team_${it + 1}")
            team.displayName(Component.text("Team ${it + 1}", NamedTextColor.GREEN))
            team.prefix(Component.text("[Team ${it + 1}] ", NamedTextColor.GREEN))
            team.setCanSeeFriendlyInvisibles(true)
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS)


            val ps = mutableListOf<Player>()
            repeat(playersPerTeam){
                val player = players.removeFirst()
                ps.add(player)
                team.addPlayer(player)
            }
            teams[team] = ps
        }
        for (team in teams.keys){
            if (players.size == 0) break
            val p = players.removeFirst()
            teams[team]!!.add(p)
            team.addPlayer(p)
        }
        plugin.server.onlinePlayers.forEach {
            it.scoreboard = board
        }
        return teams
    }

    fun reset(){
        board.teams.forEach {
            it.removeEntries(it.entries)
            it.unregister()
        }
        plugin.server.onlinePlayers.forEach {
            it.scoreboard = board
        }
    }
}