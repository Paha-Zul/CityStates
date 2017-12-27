package com.mygdx.game.behaviours

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.objects.MutableNameAmountLink


class BlackBoard{
    lateinit var myself: Entity
    var targetEntity:Entity? = null
    var targetPosition = Vector2()

    var targetItem: MutableNameAmountLink = MutableNameAmountLink("", 0)
}