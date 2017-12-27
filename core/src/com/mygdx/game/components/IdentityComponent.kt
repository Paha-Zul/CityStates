package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils

class IdentityComponent : Component {
    val uniqueID = MathUtils.random(Long.MAX_VALUE)
    var name = "DefaultName"
}