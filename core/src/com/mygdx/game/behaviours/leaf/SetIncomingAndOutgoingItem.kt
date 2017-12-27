package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Mappers
import com.mygdx.game.objects.WorkerGroup
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask
import com.mygdx.game.components.MarketComponent

class SetIncomingAndOutgoingItem(bb:BlackBoard) : LeafTask(bb) {
    lateinit var myGroup: WorkerGroup
    lateinit var otherGroup: WorkerGroup

    lateinit var myMarket: MarketComponent
    lateinit var otherMarket: MarketComponent

    var cleared = false

    override fun start() {
        super.start()

        cleared = false

        val worker = Mappers.worker[bb.myself]

        val myTown = Mappers.town[worker.town]
        val otherTown = Mappers.town[bb.targetEntity]

        myGroup = myTown.workers[DefinitionManager.workerProcessTypeMap[worker.workerDef.producesType]!!.name]!!
        otherGroup = otherTown.workers[DefinitionManager.workerProcessTypeMap[worker.workerDef.buysType]!!.name]!!

        myMarket = Mappers.market[worker.town]
        otherMarket = Mappers.market[bb.targetEntity]

        //Get the total money that's needed
        val moneyNeeded = otherMarket.getAveragePriceForBuyingFromMarket(bb.targetItem.name, bb.targetItem.amount)

        //Check if we have enough gold. If we don't, fail here
        if(myGroup.gold < moneyNeeded){
            controller.finishWithFailure()
            return
        }

        //TODO Need to check if it can be reserved here. The current method isn't working
        val amount1 = myMarket.reserveIncomingItem(bb.targetItem.name, bb.targetItem.amount)
        val amount2 = otherMarket.reserveOutgoingItem(bb.targetItem.name, bb.targetItem.amount)

//        val item1 = myGroup.resources.itemMap[bb.targetItem.name]
//        val item2 = otherGroup.resources.itemMap[bb.targetItem.name]

//        println("\n -- Reserving --")
//        println("mine: $item1")
//        println("other: $item2")
//        println(" -- Reserving -- \n")

        if(amount1 != bb.targetItem.amount || amount2 != bb.targetItem.amount){
            controller.finishWithFailure()
            unreserveItems()
//            println("Failed reserving")
            return
        }

        controller.finishWithSuccess()
    }

    override fun finalize() {
        super.finalize()
        if(!cleared){
            unreserveItems()
        }
    }

    private fun unreserveItems(){
        if(bb.targetItem.name != ""){

            //TODO Finalize needs to only be called when the task was actually started. Do we need a finished variable?
            //Notice how the amount items are reversed from above. We want to cancel out the reserving
            myMarket.reserveIncomingItem(bb.targetItem.name, -bb.targetItem.amount)
//            otherGroup.resources.reserveOutgoingItem(bb.targetItem.name, -bb.targetItem.amount)

            cleared = true
        }
    }
}