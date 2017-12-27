package com.mygdx.game.behaviours

open class LeafTask(blackboard: BlackBoard) : Task(blackboard) {

    final override val controller: TaskController = TaskController(this)
        get
}