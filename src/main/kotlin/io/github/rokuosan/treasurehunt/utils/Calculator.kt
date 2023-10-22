package io.github.rokuosan.treasurehunt.utils

import io.github.rokuosan.treasurehunt.TreasureHunt
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Calculator {

    /**
     * Perform calculation for the item
     *
     * @param item Item to be calculated
     * @return Price of the item
     */
    fun performItem(item: ItemStack): Int{
        val conf = TreasureHunt.plugin.config
        val value = conf.get("price.${item.type.name}")?: conf.get("price.default")?: 0

        return try{
            val price = value.toString().toInt()
            price * item.amount
        }catch (e: ClassCastException){
            TreasureHunt.logger.severe("Invalid price for ${item.type.name}!")
            0
        }
    }

    /**
     * Perform calculation for the items
     *
     * @param items Items to be calculated
     * @return Price of the items
     */
    fun performItems(items: List<ItemStack?>): Int {
        var price = 0
        for (item in items) {
            if (item == null) continue
            price += performItem(item)
        }
        return price
    }

    /**
     * Perform calculation for the items in the player's inventory
     *
     * @param player Player who is performing the calculation
     * @return Price of the items in the player's inventory
     */
    fun performInventory(player: Player): Int {
        return performItems(player.inventory.contents.toList())
    }
}