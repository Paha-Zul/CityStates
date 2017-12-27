package com.mygdx.game.behaviours

import com.mygdx.game.behaviours.BlackBoard
import com.mygdx.game.behaviours.Composite

class Selector (blackboard: BlackBoard, taskName: String = "") : Composite(blackboard, taskName) {

    override fun ChildSucceeded() {
        this.controller.finishWithSuccess()
    }

    override fun ChildFailed() {
        this.controller.index++

        if (this.controller.index < this.controller.taskList.size) {
            this.controller.currTask = this.controller.taskList[this.controller.index]
        } else {
            this.controller.finishWithFailure()
        }
    }

    override fun finalize() {
        super.finalize()

        this.controller.taskList.forEach { it.finalize() }
    }
}