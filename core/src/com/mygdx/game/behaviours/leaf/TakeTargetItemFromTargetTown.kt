package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask

class TakeTargetItemFromTargetTown(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val myWorker = Mappers.worker[bb.myself]
        val myInventory = Mappers.inventory[bb.myself]

        val otherTown = Mappers.town[bb.targetEntity]

        //Get the worker group that has the item we need
        val workerGroup = otherTown.workers[DefinitionManager.workerProcessTypeMap[myWorker.workerDef.buysType]!!.name]!!

        val itemDef = DefinitionManager.resourceMap[bb.targetItem.name]!!

        //Remove (or try) the amount from the worker group. Add it to my inventory
        val amountRemoved = workerGroup.resources.takeOutgoingItem(bb.targetItem.name, bb.targetItem.amount)
        myInventory.addItem(bb.targetItem.name, amountRemoved)

        controller.finishWithSuccess()
    }
}