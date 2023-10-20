package io.github.rokuosan.treasurehunt.commands

import io.github.rokuosan.treasurehunt.TreasureHunt
import io.github.rokuosan.treasurehunt.inventories.ExchangeInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ExchangeCommand: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!command.name.equals("exchange", true)) return false
        if (sender !is Player) return false

        val plugin = TreasureHunt.plugin

        val inv = ExchangeInventory(plugin)
        sender.openInventory(inv.inventory)

        return true
    }
}