package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask

class BuyResource(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val itemToBuy = Mappers.worker[bb.myself].workerDef.buysType
        val myInv = Mappers.inventory[bb.myself]

        val workerType = DefinitionManager.workerProcessTypeMap[itemToBuy]

        val otherTown = Mappers.town[bb.targetEntity]!!
        //Get the other group based on the item I'm producing (the item that goes into it)
        val otherGroup = otherTown.workers[DefinitionManager.workerProcessTypeMap[itemToBuy]!!.producesType]
    }
}