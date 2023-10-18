package io.github.rokuosan.treasurehunt.inventories

import io.github.rokuosan.treasurehunt.TreasureHunt
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class ExchangeInventory(
    plugin: TreasureHunt
) : InventoryHolder {
    private val inventory = plugin.server.createInventory(this, InventoryType.CHEST)
    override fun getInventory(): Inventory = inventory

    private var clicks = 1

    init {
        this.inventory.setItem(0, ItemStack(Material.STONE, clicks));
    }

    fun addClick(){
        if (clicks >= 64) return
        clicks++
        this.inventory.setItem(0, ItemStack(Material.STONE, clicks));
    }

    fun minusClick(){
        if(clicks <= 1) return
        clicks--
        this.inventory.setItem(0, ItemStack(Material.STONE, clicks));
    }

    companion object {
        val map = mutableMapOf<Player, ExchangeInventory>()
    }
}

class  ExchangeInventoryEventListener: Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent){
        val inventory = event.inventory
        if(inventory.getHolder(false) !is ExchangeInventory) return

        event.isCancelled = true

        val clicked = event.currentItem

        event.whoClicked.sendMessage(event.viewers.toString())
        event.viewers.forEach {
            it.sendMessage("Clicked on ${event.currentItem?.type} at ${event.rawSlot}")
        }
        event.whoClicked.sendMessage(event.action.toString())

        if (clicked == null || clicked.type == Material.AIR) return

        if(clicked.type == Material.STONE){
            val inv = inventory.getHolder(false) as ExchangeInventory
            val act = event.action

            if (act == InventoryAction.PICKUP_ALL && event.isLeftClick){
                inv.addClick()
            }else if (act == InventoryAction.PICKUP_HALF && event.isRightClick){
                inv.minusClick()
            }
        }
    }
}
