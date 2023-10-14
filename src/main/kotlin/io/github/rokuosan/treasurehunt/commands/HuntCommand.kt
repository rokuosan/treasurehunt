package io.github.rokuosan.treasurehunt.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class HuntCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name.equals("hunt", true)) return false
        if(sender !is Player) return false
        if(args.isEmpty()) return false

        sender.sendMessage("You ran the command!")

        return true
    }
}

class HuntCommandTabCompletion : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return when(args.size){
            1 -> {
                mutableListOf("start", "stop")
            }
            else -> {
                mutableListOf()
            }
        }
    }

}