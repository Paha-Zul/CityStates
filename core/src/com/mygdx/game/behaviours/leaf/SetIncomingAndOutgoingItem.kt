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
        //First try to reserve an outgoing item from the other market
        val reserveOutgoingAmount = otherMarket.reserveOutgoingItem(bb.targetItem.name, bb.targetItem.amount)

        //If we couldn't reserve the item, unreserve the attempt and return
        if(reserveOutgoingAmount <= 0){
            controller.finishWithFailure()
            otherMarket.reserveOutgoingItem(bb.targetItem.name, -bb.targetItem.amount)
            controller.finishWithFailure()
            return
        }

        //Try to reserve an incoming item to our WORKER GROUP
        val reserveIncomingAmount = myGroup.resources.reserveIncomingItem(bb.targetItem.name, reserveOutgoingAmount)

        //If we couldn't reserve incoming or it doesn't match the outgoing amount, unreserve all and fail
        if(reserveIncomingAmount <= 0 || reserveIncomingAmount != reserveOutgoingAmount){
            controller.finishWithFailure()
            myGroup.resources.reserveIncomingItem(bb.targetItem.name, -bb.targetItem.amount)
            otherMarket.reserveOutgoingItem(bb.targetItem.name, -bb.targetItem.amount)
            controller.finishWithFailure()
            return
        }

        bb.targetItem.amount = reserveOutgoingAmount

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
            myGroup.resources.reserveIncomingItem(bb.targetItem.name, -bb.targetItem.amount)
//            otherMarket.reserveOutgoingItem(bb.targetItem.name, -bb.targetItem.amount)

            cleared = true
        }
    }
}