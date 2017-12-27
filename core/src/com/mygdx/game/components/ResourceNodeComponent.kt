package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.mygdx.game.objects.NameAmountLink

class ResourceNodeComponent : Component {
    var type = ""
    lateinit var resources:List<NameAmountLink>
}