package com.mygdx.game.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.google.common.collect.EvictingQueue
import com.mygdx.game.components.InventoryComponent

class WorkerGroup {
    var type = ""
    val workers:Array<Entity> = Array()
    var gold = 0
    var resources = InventoryComponent()
    var dailyTransactions = Array<Transaction>()
    var dailyTransactionAverages:EvictingQueue<Int> = EvictingQueue.create(7)
}