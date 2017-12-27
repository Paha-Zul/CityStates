package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask

class ChangeHidden(bb:BlackBoard,val  hidden:Boolean) : LeafTask(bb) {
    override fun start() {
        super.start()
        Mappers.graphic[bb.myself].hide(hidden)
        controller.finishWithSuccess()
    }
}