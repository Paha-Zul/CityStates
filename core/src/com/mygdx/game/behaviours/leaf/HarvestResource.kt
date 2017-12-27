package com.mygdx.game.behaviours.leaf

import com.mygdx.game.util.Mappers
import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.LeafTask

class HarvestResource(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val resource = Mappers.resource[bb.targetEntity]
        val inventory = Mappers.inventory[bb.myself]

        resource.resources.forEach {
            inventory.addItem(it.name, it.amount)
        }

        controller.finishWithSuccess()
    }
}