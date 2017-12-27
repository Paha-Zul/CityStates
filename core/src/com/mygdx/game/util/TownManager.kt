package com.mygdx.game.util

import com.mygdx.game.objects.Town

object TownManager {
    val townMap = hashMapOf<String, Town>()
    val towns
        get() = townMap.values.toList()



    fun update(delta:Float){

    }

}