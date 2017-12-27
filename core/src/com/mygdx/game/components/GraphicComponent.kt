package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

class GraphicComponent : Component {
    lateinit var sprite: Sprite
    val anchor = Vector2(0.5f, 0.5f)
    var hidden = false
    val fullyShown: Boolean
        get() = !hidden && sprite.color.a >= .95f

    fun hide(hide: Boolean = true) {
        when (hide) {
            true -> {
                hidden = true
                sprite.setAlpha(0f)
            }
            else -> {
                hidden = false
                sprite.setAlpha(1f)
            }
        }
    }
}