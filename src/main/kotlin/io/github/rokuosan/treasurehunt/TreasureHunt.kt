package io.github.rokuosan.treasurehunt

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class TreasureHunt: JavaPlugin(), Listener {
    override fun onEnable() {
        logger.info("Starting TreasureHunt...")

        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        logger.info("Player joined!")
        val p = event.player
        p.sendMessage("Welcome to the server!")
    }
}