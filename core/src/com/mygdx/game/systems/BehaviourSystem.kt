package com.mygdx.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.mygdx.game.util.Mappers
import com.mygdx.game.components.BehaviourComponent
import com.mygdx.game.util.TimeUtil

/**
 * Created by Paha on 1/16/2017.
 */

class BehaviourSystem : EntitySystem() {
    lateinit var entities:ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(BehaviourComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if(TimeUtil.paused)
            return

        entities.forEach { ent ->
            val bc = Mappers.behaviour.get(ent)

            //If the behaviour is not idle, we should update it!
            if (!bc.isIdle) {
                bc.currTask.update(deltaTime)
                bc.currTaskName = bc.currTask.toString().split("[/]").toTypedArray()

                //If the behaviour has finished running, invoke the final completion callback
                if (!bc.currTask.controller.running) {
                    bc.currTask.finalize()
                    bc.onCompletionCallback?.invoke()
                }
            }
        }
    }


}