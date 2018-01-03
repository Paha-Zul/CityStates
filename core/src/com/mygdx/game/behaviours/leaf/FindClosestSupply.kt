package com.mygdx.game.behaviours.leaf

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Mappers
import com.mygdx.game.MyGame
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask
import com.mygdx.game.components.ResourceNodeComponent
import com.mygdx.game.components.TownComponent
import com.mygdx.game.components.TransformComponent
import com.mygdx.game.objects.NameAmountLink

/**
 * Finds the closest supply
 * @param basic If true, finds a basic supply from a resource node. If false, finds a supply from a town
 */
class FindClosestSupply(bb:BlackBoard, private val basic:Boolean = true) : LeafTask(bb) {
    override fun start() {
        super.start()

        val tc = Mappers.transform[bb.myself]

        //TODO Maybe I should just split this into its own behaviour? But it's so similar...
        if(basic) {
            val nodeList = MyGame.entityEngine.getEntitiesFor(Family.all(ResourceNodeComponent::class.java).get())
            bb.targetEntity = findClosestBasicSupply(tc, nodeList)
        }else{
            val townList = MyGame.entityEngine.getEntitiesFor(Family.all(TownComponent::class.java).get())
            bb.targetEntity = findClosestNonBasicSupply(tc, townList)
        }

        if(bb.targetEntity != null)
            controller.finishWithSuccess()
        else
            controller.finishWithFailure()
    }

    private fun findClosestBasicSupply(tc:TransformComponent, nodeList:ImmutableArray<Entity>): Entity? {
        var closest:Entity? = null
        var closestDst = Float.MAX_VALUE

        nodeList.forEach {
            val t = Mappers.transform[it]
            val dst = t.position.dst2(tc.position) //dst2 is faster since it doesn't need square root
            if(dst < closestDst || closest == null){
                closest = it
                closestDst = dst
            }
        }

        return closest
    }

    private fun findClosestNonBasicSupply(tc:TransformComponent, townList:ImmutableArray<Entity>): Entity? {
        val worker = Mappers.worker[bb.myself]
        //My town
        val myTown = Mappers.worker[bb.myself].town
        //My market
        val myMarket = Mappers.market[myTown]
        //My worker group
        val myWorkerGroup = Mappers.town[myTown].workers[worker.workerDef.name]!!
        //This is the resource type we want to buy
        val resourceType = worker.workerDef.buysType
        //This is the worker type needed
        val workerTypeNeeded = DefinitionManager.workerProcessTypeMap[resourceType]!!

        //Compile a list of all potential items we can buy to make our item
        val listOfItemsNeeded = mutableListOf<String>()
        DefinitionManager.resourceTypeMap[resourceType]!!.forEach{ listOfItemsNeeded.add(it.name) }

        //Sort the towns from nearest to furthest
        val townsSortedByDistance = townList.sortedBy { Mappers.transform[it].position.dst2(tc.position) }

        val itemsNeeded = listOfItemsNeeded.sortedBy { myWorkerGroup.resources.getAmountInclIncoming(it) }
        var bestTown:Entity? = null
        var bestItem = ""

        //TODO Something isn't right here

        //Loop through each item needed.
        itemsNeeded.forEach items@{ itemName ->
            var bestPrice = Float.MAX_VALUE //Initialize this at the max price

            //Here we search through each town sorted by distance. We get the best price of the first available item in a nearby town
            townsSortedByDistance.forEach{ town ->
                val market = Mappers.market[town] //Get the market of the town
                val price = market.getAveragePriceForBuyingFromMarket(itemName) //Get the price of the item
                if(market.getAvailableAmount(itemName) > 0 && price <= bestPrice) { //If it has the item and beats the best price, save it!
                    bestPrice = price
                    bestItem = itemName
                    bestTown = town
                }
            }

            //If after the loop we have a best town (one with a cheap item), then return and finish this behaviour
            if(bestTown != null) {
                bb.targetItem.apply { name = bestItem; amount = 10 }
                return bestTown
            }
        }

//        //For each town from nearest to furthest...
//        townsSortedByDistance.forEach { townEnt ->
//            //Actually, we don't mind searching our own town here. If our own town is the closest, then use that
//            val otherMarket = Mappers.market[townEnt]
//            val town = Mappers.town[townEnt]
//
//            //Get the worker group we need
//            val targetWorkerGroup = town.workers[workerTypeNeeded.name]!!
//
//            //TODO We need to get a list of all needed items to craft something (like plank, metal, cloth -> carpentry good)
//            //TODO And then check the foreign market for it against our WORKER GROUP (not market) cause that's where we store bought materials
//            val availableItems = listOfItemsNeeded
//                    .filter { otherMarket.getAvailableAmount(it) != 0 } //Filter any items that are 0 (empty) in the market
//                    .sortedBy { myWorkerGroup.resources.getAmountInclIncoming(it) } //Sort from least to greatest
//
//
////            //Get the resource list based on THE WORKER TYPE that we are.
////            //We are trying to find an empty resource in the worker's group to fill.
////            val resourceList = DefinitionManager.resourceTypeMap[resourceType]!!
////            //Filter any items that the targetWorkerGroup doesn't have
////            .filter { targetWorkerGroup.resources.getAmount(it.name) != 0
////            //Sort the filtered list by the amount that my group currently has. This should hopefully balance the items
////            }.sortedWith(compareBy({myWorkerGroup.resources.getAmountInclIncoming(it.name)}))
//
//            //This is under the .filter conditions. We're trying it without it
//            //myWorkerGroup.resources.getAmount(it.name) == 0 &&
//
//            if (availableItems.isNotEmpty()) { //If the resource is 0, return this town
//                bb.targetItem.apply { name = availableItems[0]; amount = 1 }
//                return townEnt
//            }
//        }

        //If we made it here, return null. We found nothing
        return null
    }
}