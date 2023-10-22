package io.github.rokuosan.treasurehunt.inventories

import io.github.rokuosan.treasurehunt.TreasureHunt
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
import org.bukkit.scheduler.BukkitRunnable

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
            val items = event.inventory.contents.toList()
                .filterNotNull()
                .filter{ it != ExchangeInventory.done }
                .filter{ it != ExchangeInventory.empty }
            if(items.isEmpty()) return
            event.inventory.removeItem(*items.toTypedArray())
            player.closeInventory()
            object: BukkitRunnable(){
                override fun run() {
                    player.openInventory(ExchangeConfirmInventory(TreasureHunt.plugin, player, items).inventory)
                }
            }.runTaskLater(TreasureHunt.plugin, 1)
        }
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
