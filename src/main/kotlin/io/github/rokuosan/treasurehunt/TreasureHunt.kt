package io.github.rokuosan.treasurehunt

import io.github.rokuosan.treasurehunt.commands.*
import io.github.rokuosan.treasurehunt.events.PlayerJoinListener
import io.github.rokuosan.treasurehunt.inventories.ExchangeConfirmInventoryEventListener
import io.github.rokuosan.treasurehunt.inventories.ExchangeInventoryEventListener
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger


@Suppress("unused")
class TreasureHunt: JavaPlugin() {
    companion object {
        lateinit var plugin: TreasureHunt
            private set

        lateinit var logger: Logger
            private set

        var eco: Economy? = null
        var perms: Permission? = null
        var chat: Chat? = null
    }


    override fun onEnable() {
        // Initialization
        saveDefaultConfig()
        plugin = this
        Companion.logger = logger

        if (server.pluginManager.getPlugin("Vault") == null) {
            logger.severe("Vault not found! Disabling plugin...")
            server.pluginManager.disablePlugin(this)
            return
        }
        setupEconomy()
        setupPermissions()
        setupChat()

        // Register commands
        getCommand("hunt")?.setExecutor(HuntCommand())
        getCommand("hunt")?.tabCompleter = HuntCommandTabCompletion()

        getCommand("getrecipe")?.setExecutor(GetRecipeCommand())

        getCommand("calculate")?.setExecutor(CalculateCommand())
        getCommand("calculate")?.tabCompleter = CalculateCommandTabCompletion()

        getCommand("exchange")?.setExecutor(ExchangeCommand())

        // Register events
        server.pluginManager.registerEvents(ExchangeInventoryEventListener(), this)
        server.pluginManager.registerEvents(ExchangeConfirmInventoryEventListener(), this)
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
    }

    private fun setupEconomy(): Boolean {
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java
        )
        eco = rsp?.provider
        return eco != null
    }

    private fun setupChat(): Boolean {
        val rsp = server.servicesManager.getRegistration(
            Chat::class.java
        )
        chat = rsp?.provider
        return chat != null
    }

    private fun setupPermissions(): Boolean {
        val rsp = server.servicesManager.getRegistration(
            Permission::class.java
        )
        perms = rsp?.provider
        return perms != null
    }
}