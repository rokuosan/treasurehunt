package io.github.rokuosan.treasurehunt

import io.github.rokuosan.treasurehunt.commands.GetRecipeCommand
import io.github.rokuosan.treasurehunt.commands.HuntCommand
import io.github.rokuosan.treasurehunt.commands.HuntCommandTabCompletion
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger


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
        if(!setupEconomy()){
            logger.severe("Vault not found!")
            server.pluginManager.disablePlugin(this)
            return
        }
        setupPermissions()
        setupChat()

        // Register commands
        getCommand("hunt")?.setExecutor(HuntCommand())
        getCommand("hunt")?.tabCompleter = HuntCommandTabCompletion()

        getCommand("getrecipe")?.setExecutor(GetRecipeCommand())

    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
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