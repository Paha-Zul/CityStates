package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.mygdx.game.util.DefinitionManager


class WorkerComponent : Component {
    lateinit var workerDef: DefinitionManager.WorkerDef
    var behaviour = ""
    lateinit var town:Entity
}