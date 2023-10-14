package io.github.rokuosan.treasurehunt.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BreakBlockListener: Listener {
    @EventHandler
    fun onBreakBlock(event: BlockBreakEvent){
        // 破壊されたブロックが自然生成されたブロックかどうか
        if (event.block.type.isAir) return
    }
}