package com.mygdx.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.Tasks
import com.mygdx.game.components.BehaviourComponent
import com.mygdx.game.components.WorkerComponent

class WorkerSystem : EntitySystem() {
    lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(WorkerComponent::class.java, BehaviourComponent::class.java).get())

    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.forEach {
            val beh = Mappers.behaviour[it]

            if(beh.isIdle){
                val worker = Mappers.worker[it]
                when(worker.workerDef.name){
                    "Gatherer" -> beh.currTask = Tasks.gatherTask(beh.blackBoard)
                    "Processor" -> beh.currTask = Tasks.processorTask(beh.blackBoard)
                    "Crafter" -> beh.currTask = Tasks.processorTask(beh.blackBoard)
                    "Mechant" -> beh.currTask = Tasks.processorTask(beh.blackBoard)
                    else -> {}
                }
            }
        }
    }
}