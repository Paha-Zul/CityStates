package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask
import com.mygdx.game.util.moveTowards

class MoveTo(bb:BlackBoard) : LeafTask(bb) {
    private val tc by lazy { Mappers.transform[bb.myself] }
    private val targetPosition by lazy { Mappers.transform[bb.targetEntity].position }

    private val speed = 100f

    override fun update(delta: Float) {
        super.update(delta)

        //TODO Fix magic number here!!!
        //If we are not close enough, keep moving!
        if(tc.position.dst(targetPosition) > speed*delta){
            tc.position.moveTowards(targetPosition, speed*delta)
        }else{
            controller.finishWithSuccess()
        }
    }
}