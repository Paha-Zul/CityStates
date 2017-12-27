package com.mygdx.game.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import java.util.*

object GUIManager {
    val guiStack:Stack<GUIWindow> = Stack()

    fun update(delta:Float){
        guiStack.forEach { it.updateFuncs.forEach { it() } }
    }

    fun openEntityWindow(entity: Entity, x:Float = 200f, y:Float = 300f){
        if(!guiStack.any { (it as EntityWindow).entity == entity }) {
            val window = EntityWindow(entity).apply { this.mainTable.setPosition(x, y) }
            guiStack.add(window)
        }
    }

    fun closeEntityWindow(entity:Entity){
        guiStack.firstOrNull { (it as EntityWindow).entity == entity }?.close()
    }
}