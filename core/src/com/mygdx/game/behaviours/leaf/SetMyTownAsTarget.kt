package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask

class SetMyTownAsTarget(bb:BlackBoard) : LeafTask(bb){
    override fun start() {
        super.start()
        bb.targetEntity = Mappers.worker[bb.myself].town
        controller.finishWithSuccess()
    }
}