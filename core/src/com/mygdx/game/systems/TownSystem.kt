package com.mygdx.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.mygdx.game.components.TownComponent
import com.mygdx.game.objects.CustomTimer
import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Factory
import com.mygdx.game.util.Mappers
import com.mygdx.game.objects.Transaction
import com.mygdx.game.util.Util
import com.mygdx.game.objects.WorkerGroup

class TownSystem : EntitySystem() {
    lateinit var entities:ImmutableArray<Entity>
    lateinit var dailyAverageTimer: CustomTimer

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        engine.addEntityListener(Family.all(TownComponent::class.java).get(), object:EntityListener{
            override fun entityRemoved(entity: Entity) {

            }

            override fun entityAdded(entity: Entity) {
                initTown(entity)
            }
        })

        entities = engine.getEntitiesFor(Family.all(TownComponent::class.java).get())

        dailyAverageTimer = CustomTimer(0f, 10f, false, {
            val towns = engine.getEntitiesFor(Family.all(TownComponent::class.java).get())
            towns.forEach { town ->
                val townComp = Mappers.town[town]
                townComp.workers.values.forEach { group ->
                    group.dailyTransactionAverages.add(group.dailyTransactions.sumBy { it.totalPrice })
                    group.dailyTransactions = Array() //Clear the list
                }
            }
        })
    }

    private fun initTown(entity:Entity){
        val town = Mappers.town[entity]
        val tc = Mappers.transform[entity]

        //For each worker type, we initialize the group and a single worker
        DefinitionManager.workerMap.values.forEach{ workerDef ->
            //Only do this if the worker is buyable
            if(workerDef.buyable) {
                town.workers.put(workerDef.name, WorkerGroup().apply {
                    type = workerDef.name
                    gold = 30000

                    //An inner function for adding a worker
                    fun addWorker() {
                        val color = when(type){
                            "Gatherer" -> Color.BLUE
                            "Processor" -> Color.RED
                            "Crafter" -> Color.PURPLE
                            else -> Color.TAN
                        }
                        val worker = Factory.createWorker(Vector2(tc.position), Sprite(Util.createPixel(color, 20, 20)), workerDef.name, true)
                        Mappers.worker[worker].town = entity
                        workers.add(worker)
                    }

                    //Initially run this to add a worker
                    addWorker()

                    //A timer to try to hire a worker
                    town.timers += CustomTimer(0f, 10f, false, {
                        //If we have enough gold, add a worker and subtract the gold
                        //TODO Re-enable this sometime
                        if (this.gold >= workerDef.initialCost) {
                            addWorker()
                            //Add a transaction for buying a worker
                            dailyTransactions.add(Transaction("Worker Bought", 1, -workerDef.initialCost))
                            this.gold -= workerDef.initialCost
                        }

                        //TODO The wages need to be balanced... so disabled for now
//                        val wageCost = workers.size*workerDef.dailyCost
//                        this.gold -= wageCost
//                        dailyTransactions.add(Transaction("Worker Wages", workers.size, -workerDef.dailyCost))
                    })
                })
            }
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        dailyAverageTimer.update(deltaTime)

        entities.forEach {
            val town = Mappers.town[it]
            town.timers.forEach { it.update(deltaTime) }
        }
    }
}