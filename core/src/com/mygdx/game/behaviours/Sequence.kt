package com.mygdx.game.behaviours

class Sequence (blackboard: BlackBoard, taskName: String = "") : Composite(blackboard, taskName) {

    override fun start() {
        super.start()
    }

    override fun update(delta: Float) {
        super.update(delta)
    }

    override fun end() {
        super.end()
    }

    override fun ChildFailed() {
        super.ChildFailed()
        this.controller.finishWithFailure()
        this.controller.currTask?.controller?.safeEnd()
    }

    override fun ChildSucceeded() {
        super.ChildSucceeded()
        this.controller.index++

        if (this.controller.index < this.controller.taskList.size) {
            this.controller.currTask = this.controller.taskList[this.controller.index]
        } else {
            this.controller.finishWithSuccess()
        }
    }

    override fun reset() {
        this.controller.taskList.forEach { it.controller.safeReset() } //Reset each task
        this.controller.index = 0 //Reset the indexCounter
    }

    override fun finalize() {
        super.finalize()
        this.controller.taskList.forEach { if(it.controller.started) it.finalize() }
    }
}