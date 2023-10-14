package io.github.rokuosan.treasurehunt

import io.github.rokuosan.treasurehunt.commands.HuntCommand
import io.github.rokuosan.treasurehunt.commands.HuntCommandTabCompletion
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class TreasureHunt: JavaPlugin() {
    companion object {
        lateinit var plugin: TreasureHunt
            private set

        lateinit var logger: Logger
            private set
    }


    override fun onEnable() {
        // Initialization
        saveDefaultConfig()
        plugin = this
        Companion.logger = logger

        // Register commands
        getCommand("hunt")?.setExecutor(HuntCommand())
        getCommand("hunt")?.tabCompleter = HuntCommandTabCompletion()

    }
}