package io.github.rokuosan.treasurehunt.schedulers

import io.github.rokuosan.treasurehunt.TreasureHunt
import io.github.rokuosan.treasurehunt.utils.Calculator
import io.github.rokuosan.treasurehunt.utils.GameResource
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HuntGameScheduler: BukkitRunnable() {

    private val conf = TreasureHunt.plugin.config
    private val duration = conf.getInt("config.game.duration_seconds")
    private val start = System.currentTimeMillis()
    private val end = start + duration * 1000

    override fun run() {
        GameResource.status = GameResource.GameStatus.PLAYING
        val nt = System.currentTimeMillis()
        val timeLeft = (end - nt) / 1000

        if (timeLeft <= 0) {
            val vault = TreasureHunt.eco!!
            val calculator = Calculator()
            val title = Title.title(Component.text(""), Component.text("Finish!!", NamedTextColor.RED))
            TreasureHunt.plugin.server.onlinePlayers.forEach {
                val items = it.inventory.contents.filterNotNull()
                val price = calculator.performItems(items)
                vault.depositPlayer(it, price.toDouble())
                it.inventory.removeItem(*items.toTypedArray())

                it.showTitle(title)
            }

            cancel()
            GameResource.status = GameResource.GameStatus.NOT_STARTED
            makeRanking()

            TreasureHunt.plugin.server.onlinePlayers
                .filter { !it.isOp }
                .forEach {
                    it.gameMode = GameMode.SPECTATOR
                }
        }

        val players = TreasureHunt.plugin.server.onlinePlayers
        val now = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())
        for(player in players) {
            val bal = TreasureHunt.eco!!.getBalance(player).toInt()
            val msg = Component.text()
                .append(Component.text("\uD83D\uDD52: ", NamedTextColor.GREEN))
                .append(Component.text(now, NamedTextColor.YELLOW))
                .append(Component.text(" | ", NamedTextColor.WHITE))
                .append(Component.text("⌛: ", NamedTextColor.GREEN))
                .append(Component.text("$timeLeft", NamedTextColor.YELLOW))
                .append(Component.text(" | ", NamedTextColor.WHITE))
                .append(Component.text("\uD83D\uDCB0: ", NamedTextColor.GREEN))
                .append(Component.text(bal, NamedTextColor.YELLOW))
            player.sendActionBar(msg)
        }

        // Countdown last 10 seconds in player's subtitle
        if (timeLeft in 1..10) {
            val msg = Component.text()
                .append(Component.text("Time left: ", NamedTextColor.GREEN))
                .append(Component.text(timeLeft, NamedTextColor.YELLOW))
                .build()
            val title = Title.title(Component.text(""), msg)
            for(player in players) {
                player.showTitle(title)
            }
        }

    }

    private fun makeRanking(){
        val players = TreasureHunt.plugin.server.onlinePlayers
        val ranking = players.sortedByDescending { TreasureHunt.eco!!.getBalance(it) }
        GameResource.playerRanking = ranking

        fun row(number: Int, name: String, bal: Int) = Component.text()
            .append(Component.text("${number+1}. ", NamedTextColor.GREEN))
            .append(Component.text(name, NamedTextColor.YELLOW))
            .append(Component.text(" | ", NamedTextColor.WHITE))
            .append(Component.text("\uD83D\uDCB0: ", NamedTextColor.GREEN))
            .append(Component.text(bal, NamedTextColor.YELLOW))
            .build()

        // Show ranking on chat
        TreasureHunt.plugin.server.broadcast(Component.text("==[Player Ranking]=============", NamedTextColor.GREEN))
        for((i, player) in ranking.withIndex()) {
            val bal = TreasureHunt.eco!!.getBalance(player).toInt()
            val msg = row(i, player.name, bal)
            TreasureHunt.plugin.server.broadcast(msg)
        }

        if (GameResource.teams.isNotEmpty()){
            // Calculate team score
            val teamScore = mutableMapOf<Team, Int>()
            val teams = GameResource.teams

            for((team, members) in teams){
                var score = 0
                for(member in members){
                    score += TreasureHunt.eco!!.getBalance(member).toInt()
                }
                teamScore[team] = score
            }

            // Show team ranking on chat
            val teamRanking = teamScore.toList().sortedByDescending { (_, value) -> value }.toMap()
            TreasureHunt.plugin.server.broadcast(Component.text("==[Team Ranking]=============", NamedTextColor.GREEN))
            var i = 0
            for((team, score) in teamRanking){
                val msg = row(i, team.name, score)
                TreasureHunt.plugin.server.broadcast(msg)
                i++
            }
        }
    }
}