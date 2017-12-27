package com.mygdx.game.gui

open class GUIWindow {
    var updateFuncs:MutableList<()->Unit> = mutableListOf()

    open fun close(){

    }
}