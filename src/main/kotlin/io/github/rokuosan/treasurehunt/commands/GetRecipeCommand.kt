package io.github.rokuosan.treasurehunt.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import kotlin.io.path.Path

class GetRecipeCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!command.name.equals("getrecipe", true)) return false
        if(sender !is Player) return false

        val item = sender.inventory.itemInMainHand
        val recipes = sender.server.getRecipesFor(item)

        val materialSet = emptyMap<Material, Long>().toMutableMap()
        Material.entries.filter { !it.isLegacy }.map {
            materialSet[it] = 1
        }

        for(recipe in recipes){
            if(recipe is ShapedRecipe){
                val shape = recipe.shape
                val ingredients = recipe.choiceMap
                val result = recipe.result

                for(i in ingredients){
                    val choice = i.value as MaterialChoice
                    val ist = choice.itemStack
                    sender.sendMessage(ist.type.name)
                    sender.sendMessage(ist.rarity.toString())
                }

                sender.sendMessage("Recipe for ${result.type.name}:")
                sender.sendMessage("Shape: ${shape.joinToString()}")
                sender.sendMessage("Ingredients: ${ingredients.entries.joinToString()}")
            }else if(recipe is ShapelessRecipe){
                val ingredients = recipe.choiceList
                val result = recipe.result

                sender.sendMessage("Recipe for ${result.type.name}:")
                sender.sendMessage("Ingredients: ${ingredients.joinToString()}")
            }
        }

        sender.sendMessage(item.rarity.toString())

        return true
    }

}