package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask

class CheckIfTargetIsMyTown(bb: BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        if(Mappers.worker[bb.myself].town == bb.targetEntity)
            controller.finishWithSuccess()
        else
            controller.finishWithFailure()
    }
}