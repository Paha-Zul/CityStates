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

class FindClosestSupply(bb:BlackBoard, private val basic:Boolean = true) : LeafTask(bb) {
    override fun start() {
        super.start()

        val tc = Mappers.transform[bb.myself]

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
        //My worker group
        val myWorkerGroup = Mappers.town[myTown].workers[worker.workerDef.name]!!
        //This is the resource type we want to buy
        val resourceType = worker.workerDef.buysType
        //This is the worker type needed
        val workerTypeNeeded = DefinitionManager.workerProcessTypeMap[resourceType]!!
        //Sort the towns from nearest to furthest
        val sorted = townList.sortedBy { Mappers.transform[it].position.dst2(tc.position) }


        //For each town from nearest to furthest...
        sorted.forEach { townEnt ->
            //Actually, we don't mind searching our own town here. If our own town is the closest, then use that
            val town = Mappers.town[townEnt]

            //Get the worker group we need
            val targetWorkerGroup = town.workers[workerTypeNeeded.name]!!
            //Get the resource list based on THE WORKER TYPE that we are.
            //We are trying to find an empty resource in the worker's group to fill.
            val resourceList = DefinitionManager.resourceTypeMap[resourceType]!!
            //Filter any items that the targetWorkerGroup doesn't have
            .filter { targetWorkerGroup.resources.getAmount(it.name) != 0
            //Sort the filtered list by the amount that my group currently has. This should hopefully balance the items
            }.sortedWith(compareBy({myWorkerGroup.resources.getAmountInclIncoming(it.name)}))

            //This is under the .filter conditions. We're trying it without it
            //myWorkerGroup.resources.getAmount(it.name) == 0 &&

            if (resourceList.isNotEmpty()) { //If the resource is 0, return this town
                bb.targetItem.apply { name = resourceList[0].name; amount = 1 }
                return townEnt
            }
        }

        //If we made it here, return null. We found nothing
        return null
    }
}