package com.mygdx.game.objects

import com.badlogic.ashley.core.Entity

class Town {
    var gold = 0
    val resources:HashMap<String, Int> = hashMapOf()
    val workers:HashMap<String, Array<Entity>> = hashMapOf()
}