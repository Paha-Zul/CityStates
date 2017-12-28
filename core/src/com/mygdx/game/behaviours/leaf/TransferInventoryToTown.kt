package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask
import com.mygdx.game.util.TownManager

class TransferInventoryToTown(bb:BlackBoard, val toMarket:Boolean = false) : LeafTask(bb) {
    override fun start() {
        super.start()
        val town = Mappers.town[bb.targetEntity]
        val market = Mappers.market[bb.targetEntity]
        val workerGroup = town.workers[Mappers.worker[bb.myself].workerDef.name]!!
        val inventory = Mappers.inventory[bb.myself]

        //Make a list copy so we don't alter the backing map causing an error
        inventory.itemMap.values.toList().forEach {
            when(toMarket) {
                true -> market.sellItemToMarket(it.name, it.amount)
                else -> workerGroup . resources . addItem (it.name, it.amount)
            }
            inventory.removeItem(it.name, it.amount)
        }

        controller.finishWithSuccess()
    }
}