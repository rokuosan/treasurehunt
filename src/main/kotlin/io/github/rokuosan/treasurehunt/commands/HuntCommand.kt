package io.github.rokuosan.treasurehunt.commands

import io.github.rokuosan.treasurehunt.TreasureHunt
import io.github.rokuosan.treasurehunt.schedulers.HuntGameScheduler
import io.github.rokuosan.treasurehunt.utils.GameResource
import io.github.rokuosan.treasurehunt.utils.TeamManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*

class HuntCommand : CommandExecutor {
    private var task: BukkitTask? = null
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("hunt", true)) return false
        if(sender !is Player) return false
        if(args.isEmpty()) return false

        val subCommands = listOf("start", "stop", "team")
        if (args[0] !in subCommands) return false

        when(args[0].lowercase(Locale.US)){
            "start" -> {
                val players = TreasureHunt.plugin.server.onlinePlayers
                players.forEach{
                    val vault = TreasureHunt.eco!!
                    vault.withdrawPlayer(it, vault.getBalance(it))
                }

                GameResource.status = GameResource.GameStatus.PLAYING
                GameResource.playerRanking = listOf()

                TreasureHunt.plugin.server.onlinePlayers
                    .filter { !it.isOp }
                    .forEach{
                        it.gameMode = org.bukkit.GameMode.SURVIVAL
                        GameResource.initializePlayerStatus(it)
                    }

                this.task = HuntGameScheduler().runTaskTimer(TreasureHunt.plugin, 0, 20)
            }
            "stop" -> {
                this.task?.cancel()
            }
            "team" -> {
                val expect = listOf("random", "reset")
                val param = args.getOrNull(1)
                if(param !in expect){
                    sender.sendMessage("Invalid parameter")
                    return true
                }

                when(param){
                    "random" -> {
                        val divideInto = args.getOrNull(2)?.toIntOrNull()
                        if(divideInto == null){
                            sender.sendMessage("Need a number")
                            return true
                        }

                        val manager = TeamManager()
                        GameResource.teams = manager.randomGenerate(divideInto)

                        sender.sendMessage("Teams are generated")
                    }
                    "reset" -> {
                        val manager = TeamManager()
                        manager.reset()

                        sender.sendMessage("Teams are reset")
                    }
                }
            }
        }

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
                mutableListOf("start", "stop", "team")
            }
            2 -> {
                when(args[0]){
                    "team" -> {
                        mutableListOf("random", "reset")
                    }
                    else -> {
                        mutableListOf()
                    }
                }
            }
            else -> {
                mutableListOf()
            }
        }
    }

}