package com.mygdx.game.objects

class InventoryItem(val name:String, var amount:Int){
    var incoming = 0
    var outgoing = 0

    override fun toString(): String {
        return "[$name: A:$amount, I:$incoming, O:$outgoing]"
    }
}