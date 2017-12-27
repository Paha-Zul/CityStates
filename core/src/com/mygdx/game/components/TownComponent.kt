package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.mygdx.game.objects.CustomTimer
import com.mygdx.game.objects.WorkerGroup

class TownComponent : Component {
    var gold = 0
    val resources:HashMap<String, Int> = hashMapOf()
    val workers:HashMap<String, WorkerGroup> = hashMapOf()
    val timers = mutableListOf<CustomTimer>()
    //    val buildings
}