package io.github.rokuosan.treasurehunt.schedulers

import io.github.rokuosan.treasurehunt.TreasureHunt
import io.github.rokuosan.treasurehunt.utils.GameResource
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.scheduler.BukkitRunnable
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
            val title = Title.title(Component.text(""), Component.text("Finish!!", NamedTextColor.RED))
            TreasureHunt.plugin.server.onlinePlayers.forEach {
                it.showTitle(title)
            }

            cancel()
            GameResource.status = GameResource.GameStatus.NOT_STARTED
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
}