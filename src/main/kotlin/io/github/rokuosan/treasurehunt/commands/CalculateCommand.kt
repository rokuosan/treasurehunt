package io.github.rokuosan.treasurehunt.commands

import io.github.rokuosan.treasurehunt.utils.Calculator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CalculateCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!command.name.equals("calculate", true)) return false
        if(sender !is Player) return false
        if(args.isNullOrEmpty()) return false

        val calculator = Calculator()
        val price = when(args[0]){
            "inventory" -> calculator.performInventory(sender)
            "hand" -> calculator.performItem(sender.inventory.itemInMainHand)
            "offhand" -> calculator.performItem(sender.inventory.itemInOffHand)
            else -> 0
        }

        sender.sendMessage("Price: $price")

        return true
    }
}

class CalculateCommandTabCompletion: TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return when(args.size){
            1 -> mutableListOf("inventory", "hand", "offhand")
            else -> mutableListOf()
        }
    }
}