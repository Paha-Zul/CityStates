package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask

class CraftItem(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val worker = Mappers.worker[bb.myself]
        val workerGroup = Mappers.town[worker.town].workers[worker.workerDef.name]!!
        val itemList = DefinitionManager.resourceTypeMap[worker.workerDef.producesType]!!
        val town = Mappers.town[worker.town]
        val market = Mappers.market[worker.town]

        //To find an item to process, we search through a list of all the items within a type
        val itemToProcess = itemList.firstOrNull {
            //For each item, check the requirements to see if we can craft it
            it.reqs.all { req ->
                workerGroup.resources.getAmount(req.name) >= req.amount
            }
        }

        if(itemToProcess == null)
            controller.finishWithFailure()
        else {
            //For each requirement of the item, remove the amount we used from the group's resources
            itemToProcess.reqs.forEach {
                workerGroup.resources.removeItem(it.name, it.amount)
            }

            //TODO Make/Sell multiple at a time? NO MAGIC NUMBERS
            val result = market.sellItemToMarket(itemToProcess.name, 1) //Sell it to the market
            workerGroup.gold += result.totalPrice //Add the gold to the worker group

//            workerGroup.resources.addItem(itemToProcess.name, 1) //Add 1 of the crafted item here
//            itemToProcess.reqs.forEach {  //For each requirement of the item, remove the amount we used from the group's resources
//                workerGroup.resources.removeItem(it.name, it.amount)
//            }
            controller.finishWithSuccess()
        }
    }
}