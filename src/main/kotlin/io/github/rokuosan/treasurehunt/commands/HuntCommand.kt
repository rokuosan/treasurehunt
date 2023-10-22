package io.github.rokuosan.treasurehunt.commands

import io.github.rokuosan.treasurehunt.TreasureHunt
import io.github.rokuosan.treasurehunt.utils.TeamManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HuntCommand : CommandExecutor {
    companion object {
        val PLAYER_TASK_MAP = mutableMapOf<Player, BukkitTask>()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("hunt", true)) return false
        if(sender !is Player) return false
        if(args.isEmpty()) return false

        val subCommands = listOf("start", "stop", "team")
        if (args[0] !in subCommands) return false

        when(args[0].lowercase(Locale.US)){
            "start" -> {
                if (PLAYER_TASK_MAP.containsKey(sender)) {
                    sender.sendMessage("Already started")
                    return true
                }

                sender.sendMessage("Start")
                val task = object: BukkitRunnable() {
                    override fun run() {
                        val players = TreasureHunt.plugin.server.onlinePlayers
                        val now = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())
                        for(player in players) {
                            val bal = TreasureHunt.eco!!.getBalance(player).toInt()
                            val msg = Component.text()
                                .append(Component.text("\uD83D\uDD52: ", NamedTextColor.GREEN))
                                .append(Component.text(now, NamedTextColor.YELLOW))
                                .append(Component.text(" | ", NamedTextColor.WHITE))
                                .append(Component.text("\uD83D\uDCB0: ", NamedTextColor.GREEN))
                                .append(Component.text(bal, NamedTextColor.YELLOW))
                            player.sendActionBar(msg)
                        }
                    }
                }.runTaskTimerAsynchronously(TreasureHunt.plugin, 0, 20)
                PLAYER_TASK_MAP[sender] = task
            }
            "stop" -> {
                if (!PLAYER_TASK_MAP.containsKey(sender)) {
                    sender.sendMessage("Not started")
                    return true
                }

                sender.sendMessage("Stop")
                val task = PLAYER_TASK_MAP[sender]!!
                task.cancel()
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
                        val teams = manager.randomGenerate(divideInto)

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