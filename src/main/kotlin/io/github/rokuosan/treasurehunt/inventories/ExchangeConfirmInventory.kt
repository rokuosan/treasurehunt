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
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class ExchangeConfirmInventory(
    plugin: TreasureHunt,
    private val player: Player,
    private val items: List<ItemStack>
) : InventoryHolder {
    private val inventory = plugin.server.createInventory(this, InventoryType.CHEST, Component.text("Confirm"))
    override fun getInventory(): Inventory = inventory

    companion object {
        val CONFIRM = ItemStack(Material.LIME_STAINED_GLASS_PANE).apply {
            val meta = this.itemMeta
            meta.displayName(Component.text("Confirm"))
            meta.lore(mutableListOf(
                Component.text("Exchange the items")
            ))
            this.itemMeta = meta
        }
        val CANCEL = ItemStack(Material.RED_STAINED_GLASS_PANE).apply {
            val meta = this.itemMeta
            meta.displayName(Component.text("Cancel"))
            meta.lore(mutableListOf(
                Component.text("Stop this transaction")
            ))
            this.itemMeta = meta
        }
        val PRICE_PAPER = ItemStack(Material.PAPER).apply {
            val meta = this.itemMeta
            meta.displayName(Component.text("Price"))
            this.itemMeta = meta
        }

        val PLAYER_ITEM_MAP = mutableMapOf<Player, List<ItemStack>>()
    }

    init {
        PRICE_PAPER.itemMeta = PRICE_PAPER.itemMeta?.apply {
            lore(mutableListOf(
                Component.text("Price: ${Calculator().performItems(items)}")
            ))
        }

        this.inventory.setItem(11, CANCEL)
        this.inventory.setItem(15, CONFIRM)
        this.inventory.setItem(13, PRICE_PAPER)

        PLAYER_ITEM_MAP[this.player] = items
    }
}

class ExchangeConfirmInventoryEventListener: Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent){
        if(event.inventory.getHolder(false) !is ExchangeConfirmInventory) return
        event.isCancelled = true

        val clicked = event.currentItem
        if (clicked == null || clicked.type == Material.AIR) return

        val player = event.whoClicked as Player
        val inventory = ExchangeConfirmInventory.PLAYER_ITEM_MAP[player] ?: return
        val calculator = Calculator()

        when(clicked){
            ExchangeConfirmInventory.CONFIRM -> {
                event.isCancelled = true
                val price = calculator.performItems(inventory)
                if(price > 0){
                    val vault = TreasureHunt.eco ?: return
                    vault.depositPlayer(player, price.toDouble())

                    player.sendMessage("You have exchanged the items for $price")
                }else{
                    player.sendMessage("You have exchanged the items for nothing")
                }
                ExchangeConfirmInventory.PLAYER_ITEM_MAP.remove(player)
                object: BukkitRunnable() {
                    override fun run() {
                        player.closeInventory()
                    }
                }.runTaskLater(TreasureHunt.plugin, 1)
            }
            ExchangeConfirmInventory.CANCEL -> {
                event.isCancelled = true
                player.sendMessage("You have cancelled the exchange")
                object: BukkitRunnable() {
                    override fun run() {
                        player.closeInventory()
                    }
                }.runTaskLater(TreasureHunt.plugin, 1)
            }
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent){
        if(event.inventory.getHolder(false) !is ExchangeConfirmInventory) return

        val player = event.player as Player
        val inventory = ExchangeConfirmInventory.PLAYER_ITEM_MAP[player] ?: return
        player.inventory.addItem(*inventory.toTypedArray())
    }
}