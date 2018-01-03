package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Mappers
import com.mygdx.game.objects.WorkerGroup
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask
import com.mygdx.game.objects.Transaction

/**
 * Transfers money for a purchase
 * @param bb The Blackboard object
 * @param fromTown True if the money should be taken from the owner's town (bb.myself). If false, the money will be
 * transferred from the owner (bb.myself) to the bb.targetEntity which is (needs to be) a Town.
 */
class TransferMoneyForPurchase(bb:BlackBoard, private val fromTown:Boolean = true) : LeafTask(bb) {
    override fun start() {
        super.start()

        val myWorker = Mappers.worker[bb.myself]!!
        val workerDef = DefinitionManager.workerMap[myWorker.workerDef.name]!!
        val workerInventory = Mappers.inventory[bb.myself]
        val target = if(fromTown) myWorker.town else bb.targetEntity

        val myWorkerGroup = Mappers.town[target].workers[myWorker.workerDef.name]!!

        //Here we get the worker group based on what the current worker buys (usually one grade below)
        val targetWorkerGroup: WorkerGroup = Mappers.town[target].workers[DefinitionManager.workerProcessTypeMap[workerDef.buysType]!!.name]!!

        //Get the market price of the item
        val itemListOfType = DefinitionManager.resourceTypeMap[workerDef.buysType]!! //We get the item list of the TYPE that the worker buys
        val marketPrice = itemListOfType.first { it.name == bb.targetItem.name }.marketPrice.toFloat()

        //If it's from the town AND my group has enough money for 1 of the resource
        if(fromTown && myWorkerGroup.gold >= marketPrice){
            //Take away from my worker group and give to my inventory
            workerInventory.money += marketPrice
            myWorkerGroup.gold -= marketPrice //Remove gold from my worker group
            myWorkerGroup.dailyTransactions.add(Transaction(bb.targetItem.name, bb.targetItem.amount, -marketPrice)) //Add a new transaction showing we lost gold
            controller.finishWithSuccess()
        }else if(!fromTown){
            //Remove gold from my inventory and give to the target group
            workerInventory.money -= marketPrice
            targetWorkerGroup.gold += marketPrice //Add gold to the target worker group
            targetWorkerGroup.dailyTransactions.add(Transaction(bb.targetItem.name, bb.targetItem.amount, marketPrice)) //Add a new transaction showing we gained gold
            controller.finishWithSuccess()
        }

        controller.finishWithFailure()
    }
}