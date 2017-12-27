package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class TransformComponent : Component {
    val position = Vector2()
    var dimensions = Vector2()
}