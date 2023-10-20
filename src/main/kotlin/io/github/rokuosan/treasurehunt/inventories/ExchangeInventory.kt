package io.github.rokuosan.treasurehunt.inventories

import io.github.rokuosan.treasurehunt.TreasureHunt
import io.github.rokuosan.treasurehunt.utils.Calculator
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class ExchangeInventory(
    plugin: TreasureHunt
) : InventoryHolder {
    private val inventory = plugin.server.createInventory(this, 54, Component.text("Exchange"))
    override fun getInventory(): Inventory = inventory

    companion object {
        val done: ItemStack = ItemStack(Material.LIME_STAINED_GLASS_PANE).apply {
            val meta = this.itemMeta
            meta.displayName(Component.text("Done"))
            meta.lore(listOf(Component.text("Click to finish the exchange")))
            this.itemMeta = meta
        }

        val empty: ItemStack = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
            val meta = this.itemMeta
            meta.displayName(Component.text(""))
            meta.lore(listOf(Component.text("")))
            this.itemMeta = meta
        }
    }

    init {
        for (i in 0..<this.inventory.size) {
            val x = i % 9
            val y = i / 9
            if (y == 0 || y == 5 || x == 0 || x == 8) {
                this.inventory.setItem(i, empty)
                continue
            }
        }

        this.inventory.setItem(49, done)
    }
}

class  ExchangeInventoryEventListener: Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent){
        if(event.inventory.getHolder(false) !is ExchangeInventory) return

        val clicked = event.currentItem
        if (clicked == null || clicked.type == Material.AIR) return

        // Do not allow moving frame items
        if (clicked == ExchangeInventory.empty){
            event.isCancelled = true
            return
        }

        // Calculate price of the items
        if(clicked == ExchangeInventory.done){
            event.isCancelled = true
            val player = event.whoClicked as Player
            val calculator = Calculator()

            val items = event.inventory.contents.toList()
                .filterNotNull()
                .filter{ it != ExchangeInventory.done }
                .filter{ it != ExchangeInventory.empty }
            player.sendMessage(items.toString())
            val price = calculator.performItems(items)
            val eco = TreasureHunt.eco
            if(eco == null){
                player.sendMessage("Economy plugin not found!")
                return
            }
            eco.depositPlayer(player, price.toDouble())
            player.sendMessage("You have been given $price")
            player.closeInventory()

            return
        }

        // Do not allow moving items which have no price
        val calc = Calculator()
        val value = calc.performItem(clicked)
        if(value == 0){
            event.isCancelled = true
            return
        }
        event.whoClicked.sendMessage("${clicked.type.name}: ${value}/block (${value * clicked.amount})")
    }

    @EventHandler
    fun onInventoryClosed(event: InventoryCloseEvent){
        // Return items to the player's inventory
        if(event.inventory.getHolder(false) !is ExchangeInventory) return
        val player = event.player
        val inv = event.inventory
        val items = inv.contents.toList()
            .filterNotNull()
            .filter{ it != ExchangeInventory.done }
            .filter{ it != ExchangeInventory.empty }
        for(item in items){
            player.inventory.addItem(item)
        }
    }
}
